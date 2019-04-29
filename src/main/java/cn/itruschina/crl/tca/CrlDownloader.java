package cn.itruschina.crl.tca;

import lombok.extern.slf4j.Slf4j;
import sun.security.util.ObjectIdentifier;

import java.security.*;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 11:32
 * @Remark reference from esa-java-3.7.4
 */

@Slf4j
public class CrlDownloader {
    private X509CRL crl;
    private X509Certificate caCert;

    public X509CRL getCRL() {
        return this.crl;
    }

    public CrlDownloader(X509Certificate caCert, String crlUrl, int retryTime) throws CertApiException {
        this.caCert = caCert;
        this.crl = runTask(crlUrl, retryTime);
    }

    private X509CRL runTask(String url, int retryTime) throws CertApiException {
        log.debug("runTask");
        X509CRL crl = null;
        for (int i = 0; i <= retryTime; i++) {
            try {
                crl = downloadCRL(url);
                if (verifyCRL(crl, this.caCert, new Date())) {
                    log.debug("runTask SUCCESS Exit");
                    return crl;
                }
                return crl;
            } catch (CertApiException e) {
                log.error("CRL下载失败", e);
                throw new CertApiException(TcaErrCode.ERR_BASE_DOWNLOAD);
            }
        }
        log.debug("runTask ERROR Exit");
        return crl;
    }

    private X509CRL downloadCRL(String url) throws CertApiException {
        byte[] data = TcaUtil.readURL2Byte(url);
        byte b77 = 77;
        byte b45 = 45;
        if ((data[0] == b77) || (data[0] == b45)) {
            return TcaUtil.convB642CRL(new String(data));
        }
        return TcaUtil.convBin2CRL(data);
    }

    private boolean verifyCRL(X509CRL crl, X509Certificate caCert, Date date) throws CertApiException {
        if ((date.before(crl.getThisUpdate())) || (date.after(crl.getNextUpdate()))) {
            throw new CertApiException(TcaErrCode.ERR_CRL_OUTDATE);
        }
        try {
            Signature signature;
            if (crl.getSigAlgOID().equals(getSM3withSM2Oid().toString())) {
                signature = Signature.getInstance("SM3withSM2", Security.getProvider("TopSM"));
                signature.initVerify(caCert.getPublicKey());
                signature.update(crl.getTBSCertList());
                signature.verify(crl.getSignature());
            } else {
                crl.verify(caCert.getPublicKey());
            }
            log.debug("verify CRL SUCCESS");
            return true;
        } catch (NoSuchProviderException e) {
            throw new CertApiException(TcaErrCode.ERR_BAD_PROVIDER, e);
        } catch (InvalidKeyException e) {
            throw new CertApiException(TcaErrCode.ERR_INVALID_KEY, e);
        } catch (NoSuchAlgorithmException e) {
            throw new CertApiException(TcaErrCode.ERR_UNKNOWN_ALG, e);
        } catch (CRLException e) {
            throw new CertApiException(TcaErrCode.ERR_CRL, e);
        } catch (SignatureException e) {
            throw new CertApiException(TcaErrCode.ERR_CERT_SIGNATRUE, e);
        } finally {
            log.debug("verify CRL FINAL");
        }
    }

    private ObjectIdentifier getSM3withSM2Oid() {
        return ObjectIdentifier.newInternal(new int[]{1, 2, 156, 10197, 1, 501});
    }
}