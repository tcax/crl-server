package cn.itruschina.crl.bean.controller;

import cn.itruschina.crl.bean.domain.Authorization;
import cn.itruschina.crl.bean.domain.CaConfig;
import cn.itruschina.crl.bean.service.CaConfigService;
import cn.itruschina.crl.util.ApiResponse;
import cn.itruschina.crl.util.JsonBuilder;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/16 14:09
 */

@RestController
@RequestMapping(value = {"/caconfig"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
public class CaConfigController {

    @Autowired
    CaConfigService caConfigService;

    private static final Logger logger = LoggerFactory.getLogger(CaConfigController.class);

    /**
     * @param caConfig CA配置信息
     * @return 注册CA配置处理结果
     * @description 注册CA配置
     */
    @PostMapping(value = {"/init"})
    public JSONObject init(HttpServletRequest request, @RequestBody CaConfig caConfig, @RequestAttribute("authorization") Authorization authorization) throws Exception {
        if (StringUtils.isEmpty(caConfig.getName())) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "name");
        } else if (StringUtils.isEmpty(caConfig.getBaseCrlUrl())) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "baseCrlUrl");
        } else if (StringUtils.isEmpty(caConfig.getDeltaCrlUrl())) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "deltaCrlUrl");
        } else if (StringUtils.isEmpty(caConfig.getBase64CertString())) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "base64CertString");
        }
        caConfigService.initCaconfig(caConfig, authorization);
        return JsonBuilder.build(ApiResponse.SUCCESS);
    }

    /**
     * @param param crlUrl配置信息
     * @return 查询CA配置信息
     * @description 查询CA配置
     */
    @PostMapping(value = {"/get"})
    public JSONObject update(HttpServletRequest request, @RequestBody JSONObject param) throws Exception {
        String crlUrl = param.getString("crlUrl");
        if (StringUtils.isEmpty(crlUrl)) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "crlUrl");
        }
        CaConfig dbCaConfig = caConfigService.getCaConfig(crlUrl);
        if (dbCaConfig == null) {
            return JsonBuilder.build(ApiResponse.ERROR_NOT_EXIST, "crlUrl");
        } else {
            return JsonBuilder.build(ApiResponse.SUCCESS, dbCaConfig);
        }
    }

    /**
     * @param caConfig CA配置信息
     * @return 更新CA配置处理结果
     * @description 更新CA配置
     */
    @PostMapping(value = {"/update"})
    public JSONObject update(HttpServletRequest request, @RequestBody CaConfig caConfig) throws Exception {
        if (caConfig.getId() == 0) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "id");
        } else if (StringUtils.isEmpty(caConfig.getName())) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "name");
        } else if (StringUtils.isEmpty(caConfig.getBaseCrlUrl())) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "baseCrlUrl");
        } else if (StringUtils.isEmpty(caConfig.getDeltaCrlUrl())) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "deltaCrlUrl");
        } else if (StringUtils.isEmpty(caConfig.getBase64CertString())) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "base64CertString");
        }
        caConfigService.updateCaConfig(caConfig);
        return JsonBuilder.build(ApiResponse.SUCCESS);
    }

    /**
     * @param
     * @return 删除CA配置处理结果
     * @description 删除CA配置
     */
    @PostMapping(value = {"/delete"})
    public JSONObject delete(HttpServletRequest request, @RequestBody JSONObject param, @RequestAttribute("authorization") Authorization authorization) throws Exception {
        String crlUrl = param.getString("crlUrl");
        if (StringUtils.isEmpty(crlUrl)) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS);
        }
        caConfigService.deleteCaConfig(authorization, crlUrl);
        return JsonBuilder.build(ApiResponse.SUCCESS);
    }
}

