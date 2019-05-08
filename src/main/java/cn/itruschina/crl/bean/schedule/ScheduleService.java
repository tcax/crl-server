package cn.itruschina.crl.bean.schedule;

import cn.itruschina.crl.bean.dao.CaConfigDao;
import cn.itruschina.crl.bean.domain.CaConfig;
import cn.itruschina.crl.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/18 15:19
 */

@Service
public class ScheduleService {

    private static Map<Long, ScheduleActuator> actuatorMap = new HashMap<Long, ScheduleActuator>();

    @Value("${scheduler.pool.size}")
    private int poolSize;
    @Value("${scheduler.pool.waitForTasksToCompleteOnShutdown}")
    private boolean waitForTasksToCompleteOnShutdown;
    @Value("${scheduler.pool.awaitTerminationSeconds}")
    private int awaitTerminationSeconds;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private CaConfigDao caConfigDao;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        scheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        return scheduler;
    }

    public void startFollowDeltaCrl(long caConfigId) {
        CaConfig caConfig = caConfigDao.findOne(caConfigId);
        if (caConfig == null) {
            removeActuatorConfig(caConfigId);
        } else {
            startFollowDeltaCrl(caConfig);
        }
    }

    public void startFollowDeltaCrl(CaConfig caConfig) {
        Date date = new Date();
        String cronStr;
        if (StringUtils.isEmpty(caConfig.getDeltaNextUpdate()) || (date.before(caConfig.getDeltaThisUpdate())) || (date.after(caConfig.getDeltaNextUpdate()))) {
            cronStr = "0 */1 * * * *";
        } else {
            cronStr = DateUtil.formatDateToCron(caConfig.getDeltaNextUpdate());
        }

        long caConfigId = caConfig.getId();
        if (actuatorMap.containsKey(caConfigId)) {
            ScheduleActuator actuator = actuatorMap.get(caConfigId);
            actuator.changeCron(cronStr);
        } else {
            ScheduleActuator actuator = new ScheduleActuator(caConfig, cronStr, threadPoolTaskScheduler);
            actuator.startCron();
            actuatorMap.put(caConfigId, actuator);
        }
    }

    public void removeActuatorConfig(long caConfigId) {
        if (actuatorMap.containsKey(caConfigId)) {
            ScheduleActuator actuator = actuatorMap.get(caConfigId);
            actuator.stopCron();
            actuatorMap.remove(caConfigId);
        }
    }

}
