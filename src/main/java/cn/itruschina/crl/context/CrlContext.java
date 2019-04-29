package cn.itruschina.crl.context;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/19 11:26
 */

@Component
@Slf4j
public class CrlContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 200, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), new
            ThreadFactoryBuilder().
            setNameFormat("base-crl-download").
            build(), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        if (CrlContext.applicationContext == null) {
            CrlContext.applicationContext = applicationContext;
        }
        log.info("ApplicationContext配置成功,applicationContext对象：" + CrlContext.applicationContext);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    @Override
    protected void finalize() throws Throwable {
        threadPoolExecutor.shutdown();
        super.finalize();
    }
}
