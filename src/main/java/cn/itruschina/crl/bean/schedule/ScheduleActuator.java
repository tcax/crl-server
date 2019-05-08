package cn.itruschina.crl.bean.schedule;

import cn.itruschina.crl.bean.domain.CaConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/18 15:19
 */

public class ScheduleActuator {

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private String cronStr;
    private CaConfig caConfig;

    public ScheduleActuator(CaConfig caConfig, String cronStr, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.caConfig = caConfig;
        this.cronStr = cronStr;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    private ScheduledFuture<?> future;

    public void startCron() {
        this.future = this.threadPoolTaskScheduler.schedule(new ScheduleRunnable(this.caConfig), new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                return new CronTrigger(cronStr).nextExecutionTime(triggerContext);
            }
        });
    }

    public void stopCron() {
        if (future != null) {
            future.cancel(true);
        }
    }

    public void changeCron(String cronStr) {
        if (!StringUtils.equals(this.cronStr, cronStr)) {
            this.cronStr = cronStr;
        }
    }


}
