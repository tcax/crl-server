package cn.itruschina.crl.bean.service;

import cn.itruschina.crl.bean.dao.CaConfigDao;
import cn.itruschina.crl.bean.dao.CrlRecodeDao;
import cn.itruschina.crl.bean.domain.CaConfig;
import cn.itruschina.crl.bean.domain.CrlRecord;
import cn.itruschina.crl.tca.CertApiException;
import cn.itruschina.crl.tca.TcaErrCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/16 13:37
 */


@Service
@Slf4j
public class CrlRecodeService {

    @Autowired
    CrlRecodeDao crlRecodeDao;
    @Autowired
    CaConfigDao caConfigDao;


    public static String formatCertSn(BigInteger b) {
        if (b.signum() >= 0) {
            return b.toString(16).toUpperCase();
        }

        byte[] a1 = b.toByteArray();
        byte[] a2 = new byte[a1.length + 1];
        a2[0] = 0;
        System.arraycopy(a1, 0, a2, 1, a1.length);
        return new BigInteger(a2).toString(16).toUpperCase();
    }


    /***
     * 检查证书吊销状态
     * @param serialNumber
     * @param crlUrl
     * @param issuerDn
     * @return
     * @throws Exception
     */
    public CrlRecord checkRevoked(String serialNumber, String crlUrl, String issuerDn) throws Exception {

        List<CaConfig> list;
        if (!StringUtils.isEmpty(crlUrl)) {
            if (isDeltaCrlUrl(crlUrl)) {
                list = caConfigDao.findByDeltaCrlUrl(crlUrl);
            } else {
                list = caConfigDao.findByBaseCrlUrl(crlUrl);
            }
        } else {
            list = caConfigDao.findBySubjectDn(issuerDn);
        }
        if (list == null || list.size() != 1) {
            throw new Exception("CA配置不唯一");
        }
        CaConfig caConfig = list.get(0);
        CrlRecord crlRecord = crlRecodeDao.findByCaConfigIdAndSerialNumber(caConfig.getId(), serialNumber.toUpperCase());
        return crlRecord;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateCrlToDB(long caConfigId, X509CRL crl, boolean isDelta) throws CertApiException {
        Date now = new Date();
        if ((now.before(crl.getThisUpdate())) || (now.after(crl.getNextUpdate()))) {
            throw new CertApiException(TcaErrCode.ERR_CRL_OUTDATE);
        }
        if (isDelta) {
            CaConfig caConfig = caConfigDao.findOne(caConfigId);
            caConfig.setDeltaThisUpdate(crl.getThisUpdate());
            caConfig.setDeltaNextUpdate(crl.getNextUpdate());
            caConfigDao.save(caConfig);
        }
        Iterator<?> iterator = crl.getRevokedCertificates().iterator();
        CrlRecord crlRecord;
        X509CRLEntry crlEntry;
        String serialNumber;
        Date revocationDate;
        String revocationReason;
        while (iterator.hasNext()) {
            crlEntry = (X509CRLEntry) iterator.next();
            serialNumber = formatCertSn(crlEntry.getSerialNumber());
            if (crlRecodeDao.findByCaConfigIdAndSerialNumber(caConfigId, serialNumber) == null) {
                revocationDate = crlEntry.getRevocationDate();
                revocationReason = crlEntry.getRevocationReason().toString();
                crlRecord = new CrlRecord(caConfigId, serialNumber, revocationDate, revocationReason);
                crlRecodeDao.save(crlRecord);
            }
        }
    }

    /***
     * 检查是否为增量CRL地址
     * @param crlUrl
     * @return
     */
    private boolean isDeltaCrlUrl(String crlUrl) {
        return crlUrl.contains("downDelteCrl=true");
    }

}
