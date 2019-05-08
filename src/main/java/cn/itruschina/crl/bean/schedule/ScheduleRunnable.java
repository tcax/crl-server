package cn.itruschina.crl.bean.schedule;

import cn.itruschina.crl.bean.domain.CaConfig;
import cn.itruschina.crl.bean.service.CrlRecordService;
import cn.itruschina.crl.context.CrlContext;
import cn.itruschina.crl.tca.CertApiException;
import cn.itruschina.crl.tca.CrlDownloader;
import cn.itruschina.crl.tca.TcaUtil;
import lombok.extern.slf4j.Slf4j;

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
        ScheduleService scheduleService = CrlContext.getApplicationContext().getBean("scheduleService", ScheduleService.class);
        CrlRecordService crlRecordService = CrlContext.getApplicationContext().getBean("crlRecordService", CrlRecordService.class);
        try {
            X509Certificate caCert = TcaUtil.convB64Str2Cert(caConfig.getBase64CertString());
            CrlDownloader crlDownloader = new CrlDownloader(caCert, caConfig.getDeltaCrlUrl(), caConfig.getRetryTime());
            if (crlDownloader.getCRL() != null) {
                crlRecordService.updateCrlToDB(caConfig.getId(), crlDownloader.getCRL(), true);
            }
        } catch (CertApiException e) {
            log.error("增量CRL下载失败", e);
        } finally {
            scheduleService.startFollowDeltaCrl(this.caConfig.getId());
        }
    }
}
