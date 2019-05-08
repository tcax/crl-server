package cn.itruschina.crl.bean.component;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/04/16 10:50
 */
@Configuration
@Slf4j
public class DruidConfiguration {


    @Value("${spring.druid.managerAccount}")
    private String managerAccount;

    @Value("${spring.druid.managerPassword}")
    private String managerPassword;

    @Bean
    public ServletRegistrationBean druidStatViewServle() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        //控制台用户
        servletRegistrationBean.addInitParameter("loginUsername", managerAccount);
        servletRegistrationBean.addInitParameter("loginPassword", managerPassword);
        //禁用HTML页面上的“Reset All”功能
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean statFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
}
