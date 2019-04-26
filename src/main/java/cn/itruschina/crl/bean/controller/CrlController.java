package cn.itruschina.crl.bean.controller;

import cn.itruschina.crl.bean.domain.CrlRecord;
import cn.itruschina.crl.bean.service.CrlRecodeService;
import cn.itruschina.crl.util.ApiResponse;
import cn.itruschina.crl.util.JsonBuilder;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 13:20
 */

@RestController
@RequestMapping(value = {"/crl"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class CrlController {

    @Autowired
    CrlRecodeService crlRecodeService;

    /**
     * @param param crl查询参数
     * @return 证书状态查询结果
     * @description 查询证书吊销状态
     */
    @PostMapping(value = {"/checkRevoked"})
    public JSONObject enroll(HttpServletRequest request, @RequestBody JSONObject param) {
        String serialNumber = param.getString("serialNumber");
        String issuerDn = param.getString("issuerDn");
        String crlUrl = param.getString("crlUrl");
        if (StringUtils.isEmpty(serialNumber)) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "serialNumber");
        } else if (StringUtils.isEmpty(issuerDn)) {
            return JsonBuilder.build(ApiResponse.ERROR_PARAMS_MISS, "issuerDn");
        }
        CrlRecord crlRecord;
        try {
            crlRecord = crlRecodeService.checkRevoked(serialNumber, crlUrl, issuerDn);
        } catch (Exception e) {
            return JsonBuilder.build(ApiResponse.ERROR_NOT_EXIST);
        }
        if (crlRecord != null) {
            return JsonBuilder.build(ApiResponse.ERROR_CERT_REVOKED, crlRecord);
        } else {
            return JsonBuilder.build(ApiResponse.ERROR_CERT_ENABLE);

        }
    }
}

