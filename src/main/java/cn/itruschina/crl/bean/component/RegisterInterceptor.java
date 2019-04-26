package cn.itruschina.crl.bean.component;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/24 15:26
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class RegisterInterceptor extends WebMvcConfigurerAdapter {

    @Autowired
    AuthCheckInterceptor authCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authCheckInterceptor);
        super.addInterceptors(registry);
    }
}
