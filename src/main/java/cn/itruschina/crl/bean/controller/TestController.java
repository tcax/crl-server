package cn.itruschina.crl.bean.controller;

import cn.itruschina.crl.bean.dao.CrlRecordDao;
import cn.itruschina.crl.bean.domain.CrlRecord;
import cn.itruschina.crl.util.ApiResponse;
import cn.itruschina.crl.util.JsonBuilder;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 13:20
 */

@RestController
@RequestMapping(value = {"/test"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class TestController {

    @Autowired
    CrlRecordDao crlRecordDao;

    @PostMapping(value = {"/insert"})
    public JSONObject enroll(HttpServletRequest request, @RequestBody CrlRecord crlRecord) {
        crlRecordDao.save(crlRecord);
        return JsonBuilder.build(ApiResponse.SUCCESS);
    }

    @PostMapping(value = {"/getById/{id}"})
    public JSONObject getById(HttpServletRequest request, @PathVariable long id) {
        CrlRecord crlRecord = crlRecordDao.findOne(id);
        return JsonBuilder.build(ApiResponse.SUCCESS, crlRecord);
    }

    @PostMapping(value = {"/get"})
    public JSONObject get(HttpServletRequest request, @RequestBody CrlRecord crlRecord1) {
        CrlRecord crlRecord = crlRecordDao.findByCaConfigIdAndSerialNumber(crlRecord1.getCaConfigId(), crlRecord1.getSerialNumber());
        return JsonBuilder.build(ApiResponse.SUCCESS, crlRecord);
    }
}

