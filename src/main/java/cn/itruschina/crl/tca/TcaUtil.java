package cn.itruschina.crl.tca;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.CollectionStore;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.*;
import java.util.List;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 10:19
 * @Remark reference from esa-java-3.7.4
 */

public final class TcaUtil {
    private static CertificateFactory cf = null;

    private static Provider sm2Provider = Security.getProvider("TopSM");
    private static Provider bcProvider;
    private static String bcProviderName = "BC";
    private static byte[] plain;
    private static List<SignerInfoGenerator> signerInfoGenerators;
    private static List<X509CertificateHolder> certs;
    private static List<X509CRLHolder> crls;
    private static boolean includeContent;

    public static Provider getBcProvider() {
        return bcProvider;
    }

    public static Provider getSm2Provider() {
        return sm2Provider;
    }

    public static X509Certificate convB64Str2Cert(String b64Str)
            throws CertApiException {
        b64Str = b64Str.replaceAll("-----BEGIN CERTIFICATE-----", "").replaceAll("-----END CERTIFICATE-----", "").replaceAll("\r", "").replaceAll("\n", "");

        return convBin2Cert(Base64.decodeBase64(b64Str));
    }

    public static X509Certificate convBin2Cert(byte[] certData)
            throws CertApiException {
        try {
            if (cf == null) {
                cf = CertificateFactory.getInstance("X.509", bcProvider);
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(certData);
            return (X509Certificate) cf.generateCertificate(bais);
        } catch (CertificateException e) {
            throw new CertApiException(TcaErrCode.ERR_CONV_CERT, e);
        }
    }

    public static X509Certificate readFile2Cert(String certFilePath)
            throws CertApiException {
        byte[] binData = readFile2Byte(certFilePath);
        return (binData[0] == 77) || (binData[0] == 45) ? convB64Str2Cert(new String(binData)) : convBin2Cert(binData);
    }

    public static X509CRL convB642CRL(String b64Str)
            throws CertApiException {
        b64Str = b64Str.replaceAll("-----BEGIN CRL-----", "").replaceAll("-----BEGIN X509 CRL-----", "").replaceAll("-----END CRL-----", "").replaceAll("-----END X509 CRL-----", "").replaceAll("\r", "").replaceAll("\n", "");

        return convBin2CRL(decode(b64Str));
    }

    public static X509CRL convBin2CRL(byte[] in)
            throws CertApiException {
        try {
            if (cf == null) {
                cf = CertificateFactory.getInstance("X.509");
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(in);
            return (X509CRL) cf.generateCRL(bais);
        } catch (CertificateException e) {
            throw new CertApiException(TcaErrCode.ERR_CONV_CERT, e);
        } catch (CRLException e) {
            throw new CertApiException(TcaErrCode.ERR_CRL, e);
        }
    }

    public static String encode(byte[] in) {
        return Base64.encodeBase64String(in);
    }

    public static byte[] decode(String in) {
        return Base64.decodeBase64(in);
    }

    public static byte[] readURL2Byte(String urlStr) throws CertApiException {
        try {
            URL url = new URL(urlStr);
            InputStream is = url.openStream();
            byte[] buf = new byte[1024];
            int size = is.read(buf);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (size != -1) {
                bos.write(buf, 0, size);
                size = is.read(buf);
            }
            return bos.toByteArray();
        } catch (MalformedURLException e) {
            throw new CertApiException(TcaErrCode.ERR_BAD_URL, e);
        } catch (IOException e) {
            throw new CertApiException(TcaErrCode.ERR_STREAM, e);
        }
    }

    public static byte[] readIS2Byte(InputStream is) throws CertApiException {
        try {
            byte[] buf = new byte[1024];
            int size = is.read(buf);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (size != -1) {
                bos.write(buf, 0, size);
                size = is.read(buf);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new CertApiException(TcaErrCode.ERR_STREAM, e);
        }
    }

    public static byte[] readFile2Byte(String certFilePath) throws CertApiException {
        try {
            FileInputStream fis = new FileInputStream(certFilePath);
            return readIS2Byte(fis);
        } catch (FileNotFoundException e) {
            throw new CertApiException(TcaErrCode.ERR_FILE_NOTFOUND, e);
        }
    }

    public static boolean writeByte2File(byte[] data, String filePath) throws CertApiException {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.flush();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            throw new CertApiException(TcaErrCode.ERR_FILE_NOTFOUND, e);
        } catch (IOException e) {
            throw new CertApiException(TcaErrCode.ERR_STREAM, e);
        }
    }

    public static byte[] pbeEncrypt(char[] pwd, byte[] in) throws CertApiException {
        return doPBE(pwd, in, true);
    }

    public static byte[] pbeDecrypt(char[] pwd, byte[] in) throws CertApiException {
        return doPBE(pwd, in, false);
    }

    public static byte[] md5(String in) throws CertApiException {
        return md5(in, "UTF8");
    }

    public static byte[] md5(String in, String charsetName) throws CertApiException {
        try {
            return md5(in.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new CertApiException(TcaErrCode.ERR_ENCODE, e);
        }
    }

    public static byte[] md5(byte[] in) throws CertApiException {
        return doHash("MD5", in);
    }

    public static byte[] sha1(String in) throws CertApiException {
        return sha1(in, "UTF8");
    }

    public static byte[] sha1(String in, String charsetName) throws CertApiException {
        try {
            return sha1(in.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new CertApiException(TcaErrCode.ERR_ENCODE, e);
        }
    }

    public static byte[] sha1(byte[] in) throws CertApiException {
        return doHash("SHA1", in);
    }

    public static byte[] sha256(String in) throws CertApiException {
        return sha256(in, "UTF8");
    }

    public static byte[] sha256(String in, String charsetName) throws CertApiException {
        try {
            return sha256(in.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new CertApiException(TcaErrCode.ERR_ENCODE, e);
        }
    }

    public static byte[] sha256(byte[] in) throws CertApiException {
        return doHash("SHA256", in);
    }

    public static byte[] sm3(String in) throws CertApiException {
        return sm3(in, "UTF8");
    }

    public static byte[] sm3(String in, String charsetName) throws CertApiException {
        try {
            return sm3(in.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new CertApiException(TcaErrCode.ERR_ENCODE, e);
        }
    }

    public static byte[] sm3(byte[] in) throws CertApiException {
        return doHash("SM3", in);
    }

    public static String bin2HexStr(byte[] in) {
        return Hex.encodeHexString(in);
    }

    public static String genPubKeyHash(PublicKey pubKey) throws CertApiException {
        return bin2HexStr(sha1(pubKey.getEncoded()));
    }

    public static CMSSignedData doGenSignedData(byte[] plain, List<SignerInfoGenerator> signerInfoGenerators, List<X509CertificateHolder> certs, List<X509CRLHolder> crls, boolean includeContent)
            throws CMSException {
        TcaUtil.plain = plain;
        TcaUtil.plain = plain;
        TcaUtil.signerInfoGenerators = signerInfoGenerators;
        TcaUtil.signerInfoGenerators = signerInfoGenerators;
        TcaUtil.certs = certs;
        TcaUtil.certs = certs;
        TcaUtil.crls = crls;
        TcaUtil.crls = crls;
        TcaUtil.includeContent = includeContent;
        TcaUtil.includeContent = includeContent;
        CMSSignedDataGenerator cmsSignedDataGen = new CMSSignedDataGenerator();
        if ((certs != null) && (!certs.isEmpty())) {
            cmsSignedDataGen.addCertificates(new CollectionStore(certs));
        }
        if ((crls != null) && (!crls.isEmpty())) {
            cmsSignedDataGen.addCRLs(new CollectionStore(crls));
        }
        for (SignerInfoGenerator sig : signerInfoGenerators) {
            cmsSignedDataGen.addSignerInfoGenerator(sig);
        }
        CMSTypedData msgContent = new CMSProcessableByteArray(plain);

        return cmsSignedDataGen.generate(msgContent, includeContent);
    }

    /*public static SignerInfoGenerator genSignerInfoGenerator(X509Certificate cert, PrivateKey priKey, String hashAlg, boolean addDeftAttr, Provider provider)
            throws OperatorCreationException {
        IssuerAndSerialNumber id = getIssuerAndSerialNumber(cert);
        SignerIdentifier signerIdentifier = new SignerIdentifier(id);
        String signAlg;
        if ((hashAlg == null) || (hashAlg.isEmpty())) {
            signAlg = cert.getSigAlgName();
        } else {
            signAlg = hashAlg + "with" + cert.getPublicKey().getAlgorithm();
        }
        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder(signAlg);
        contentSignerBuilder.setProvider(provider);
        ContentSigner contentSigner = contentSignerBuilder.build(priKey);
        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().build();

        return new SignerInfoGenerator(signerIdentifier, contentSigner, digestCalculatorProvider, !addDeftAttr);
    }*/

    /*private static TBSCertificateStructure getTBSCertificateStructure(X509Certificate cert) {
        try {
            return TBSCertificateStructure.getInstance(ASN1Object.fromByteArray(cert.getTBSCertificate()));
        } catch (Exception e) {
        }
        throw new IllegalArgumentException("can't extract TBS structure from this cert");
    }

    private static IssuerAndSerialNumber getIssuerAndSerialNumber(X509Certificate cert) {
        TBSCertificateStructure tbsCert = getTBSCertificateStructure(cert);
        return new IssuerAndSerialNumber(X500Name.getInstance(tbsCert.getIssuer()), tbsCert.getSerialNumber().getValue());
    }*/

    private static byte[] doHash(String alg, byte[] in) throws CertApiException {
        try {
            MessageDigest md = MessageDigest.getInstance(alg);
            return md == null ? null : md.digest(in);
        } catch (NoSuchAlgorithmException e) {
            throw new CertApiException(TcaErrCode.ERR_UNKNOWN_ALG, e);
        }
    }

    private static byte[] doPBE(char[] pwd, byte[] in, boolean isEnc) throws CertApiException {
        try {
            byte[] pbeSlat = "noEvilShallEscapeMySight".getBytes();
            PBEKeySpec keySpec = new PBEKeySpec(pwd);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
            Key key = keyFactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
            if (isEnc) {
                cipher.init(1, key, new PBEParameterSpec(pbeSlat, pbeSlat.length));
            } else {
                cipher.init(2, key, new PBEParameterSpec(pbeSlat, pbeSlat.length));
            }
            return cipher.doFinal(in);
        } catch (Exception e) {
            TcaErrCode tcaErrCode = isEnc ? TcaErrCode.ERR_PBEENC : TcaErrCode.ERR_PBEDEC;
            throw new CertApiException(tcaErrCode, e);
        }
    }

    static {
        /*if (Security.getProvider("TopSM") == null)
            Security.addProvider(TopSMProvider.INSTANCE);*/
        if (Security.getProperty(bcProviderName) == null) {
            Security.addProvider(new BouncyCastleProvider());
            bcProvider = Security.getProvider("BC");
        }
    }
}
