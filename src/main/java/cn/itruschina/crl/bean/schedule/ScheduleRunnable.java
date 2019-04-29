package cn.itruschina.crl.bean.schedule;

import cn.itruschina.crl.bean.domain.CaConfig;
import cn.itruschina.crl.bean.service.CrlRecodeService;
import cn.itruschina.crl.context.CrlContext;
import cn.itruschina.crl.tca.CertApiException;
import cn.itruschina.crl.tca.CrlDownloader;
import cn.itruschina.crl.tca.TcaUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/18 15:38
 */

@Slf4j
public class ScheduleRunnable implements Runnable {

    private CaConfig caConfig;

    ScheduleRunnable(CaConfig caConfig) {
        this.caConfig = caConfig;
    }

    @Override
    public void run() {
        ScheduleService scheduleService = (ScheduleService) CrlContext.getApplicationContext().getBean("scheduleService");
        CrlRecodeService crlRecodeService = (CrlRecodeService) CrlContext.getApplicationContext().getBean("crlRecodeService");
        try {
            X509Certificate caCert = TcaUtil.convB64Str2Cert(caConfig.getBase64CertString());
            CrlDownloader crlDownloader = new CrlDownloader(caCert, caConfig.getDeltaCrlUrl(), caConfig.getRetryTime());
            if (crlDownloader.getCRL() != null) {
                X509CRL crl = crlDownloader.getCRL();
                crlRecodeService.updateCrlToDB(caConfig.getId(), crl, true);
                scheduleService.startFollowDeltaCrl(this.caConfig.getId());
            }
        } catch (CertApiException e) {
            log.error("增量CRL下载失败", e);
        } finally {
            scheduleService.startFollowDeltaCrl(this.caConfig.getId());
        }
    }
}
