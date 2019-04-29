package cn.itruschina.crl.bean.service;

import cn.itruschina.crl.bean.dao.AuthAndCaDao;
import cn.itruschina.crl.bean.dao.CaConfigDao;
import cn.itruschina.crl.bean.dao.CrlRecodeDao;
import cn.itruschina.crl.bean.domain.AuthAndCa;
import cn.itruschina.crl.bean.domain.Authorization;
import cn.itruschina.crl.bean.domain.CaConfig;
import cn.itruschina.crl.bean.domain.CrlRecord;
import cn.itruschina.crl.bean.schedule.ScheduleService;
import cn.itruschina.crl.context.CrlContext;
import cn.itruschina.crl.tca.CrlDownloader;
import cn.itruschina.crl.tca.TcaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/16 13:37
 */


@Service
@Slf4j
public class CaConfigService {

    @Autowired
    CaConfigDao caConfigDao;
    @Autowired
    CrlRecodeDao crlRecodeDao;
    @Autowired
    AuthAndCaDao authAndCaDao;
    @Autowired
    CrlRecodeService crlRecodeService;
    @Autowired
    ScheduleService scheduleService;

    @Transactional(rollbackFor = {Exception.class})
    public CaConfig initCaconfig(CaConfig caConfig, Authorization authorization) throws Exception {
        X509Certificate cert = TcaUtil.convB64Str2Cert(caConfig.getBase64CertString());
        caConfig.setSubjectDn(cert.getSubjectDN().toString());

        this.handleBaseUrlDownload(cert, caConfig);
        caConfig = caConfigDao.save(caConfig);
        AuthAndCa authAndCa = new AuthAndCa(caConfig.getId(), authorization.getId());
        authAndCaDao.save(authAndCa);
        scheduleService.startFollowDeltaCrl(caConfig);
        return caConfig;
    }

    public CaConfig updateCaConfig(CaConfig caConfig) {
        caConfig = caConfigDao.save(caConfig);
        scheduleService.startFollowDeltaCrl(caConfig);
        return caConfig;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteCaConfig(Authorization authorization, String crlUrl) {
        CaConfig dbCaConfig = caConfigDao.findByBaseCrlUrlOrDeltaCrlUrl(crlUrl);
        if (dbCaConfig != null) {
            List<CrlRecord> crlRecordList = crlRecodeDao.findByCaConfigId(dbCaConfig.getId());
            crlRecordList.stream().forEach(crlRecord -> {
                crlRecodeDao.delete(crlRecord);
            });
            caConfigDao.delete(dbCaConfig);
            authAndCaDao.deleteByAuthorizationIdAndCaConfigId(authorization.getId(), dbCaConfig.getId());
        }

    }

    public CaConfig getCaConfig(String crlUrl) {
        return caConfigDao.findByBaseCrlUrlOrDeltaCrlUrl(crlUrl);
    }

    public void handleBaseUrlDownload(X509Certificate cert, CaConfig caConfig) {
        CrlContext.threadPoolExecutor.execute(() -> {
            try {
                CrlDownloader crlDownloader = new CrlDownloader(cert, caConfig.getBaseCrlUrl(), caConfig.getRetryTime());
                if (crlDownloader.getCRL() != null) {
                    X509CRL crl = crlDownloader.getCRL();
                    crlRecodeService.updateCrlToDB(caConfig.getId(), crl, false);
                }
            } catch (Exception e) {
                log.error("全量CRL下载失败", e);
            }
        });
    }

}
