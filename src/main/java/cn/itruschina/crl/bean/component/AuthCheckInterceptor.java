package cn.itruschina.crl.bean.component;

import cn.itruschina.crl.bean.dao.AuthorizationDao;
import cn.itruschina.crl.bean.domain.Authorization;
import cn.itruschina.crl.util.ApiResponse;
import cn.itruschina.crl.util.CommonUtil;
import cn.itruschina.crl.util.JsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/24 14:46
 */

@Aspect
@Slf4j
@Component
public class AuthCheckInterceptor implements HandlerInterceptor {

    @Autowired
    AuthorizationDao authorizationDao;

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) {
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        boolean isEnableToken = false;
        String accessToken = request.getHeader("Authorization");
        Authorization authorization = null;
        if (StringUtils.isEmpty(accessToken)) {
            isEnableToken = false;
        } else {
            authorization = authorizationDao.findByAccessToken(accessToken);
            if (authorization != null) {
                isEnableToken = true;
            }
        }
        if (isEnableToken) {
            request.setAttribute("authorization", authorization);
            log.info(authorization.toString());
            return true;
        } else {
            String result = JsonBuilder.build(ApiResponse.ERROR_UNAUTHORIZED).toJSONString();
            CommonUtil.writeJsonResponse(response, result);
            return false;
        }
    }

}