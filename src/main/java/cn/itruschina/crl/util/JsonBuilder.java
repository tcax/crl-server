package cn.itruschina.crl.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 10:19
 */

@Slf4j
public class JsonBuilder {

    private static final String EMPTY = "";

    /*public static JSONObject build(Object code, String msg) {
        JSONObject result = new JSONObject();
        try {
            result.put("code", code);
            result.put("msg", msg);
        } catch (JSONException e) {
            log.debug("构建JSON对象失败", e);
        }
        return result;
    }*/

    public static JSONObject build(ApiResponse apiResponse) {
        JSONObject result = new JSONObject();
        try {
            result.put("code", apiResponse.getCode());
            result.put("msg", apiResponse.getMessage());
        } catch (JSONException e) {
            log.debug("构建JSON对象失败", e);
        }
        return result;
    }

    /*public static JSONObject build(Object code, String msg, Object object) {
        JSONObject result = new JSONObject();
        try {
            result.put("code", code);
            result.put("msg", msg);
            if (object != null) {
                result.put("data", object);
            }
        } catch (JSONException e) {
            log.debug("构建JSON对象失败", e);
        }
        return result;
    }*/

    public static JSONObject build(ApiResponse apiResponse, Object object) {
        JSONObject result = new JSONObject();
        try {
            result.put("code", apiResponse.getCode());
            result.put("msg", apiResponse.getMessage());
            if (object != null) {
                result.put("data", object);
            }
        } catch (JSONException e) {
            log.debug("构建JSON对象失败", e);
        }
        return result;
    }

    public static JSONObject build(Map map) throws JSONException {
        JSONObject object = new JSONObject();
        for (Object key : map.keySet()) {
            object.put(key.toString(), map.get(key));
        }
        return object;
    }

    public static JSONArray build(Collection collection) throws JSONException {
        JSONArray array = new JSONArray();
        for (Object object : collection) {
            array.add(object);
        }
        return array;
    }

    public static String nullForEmpty(Object source) {
        return source == null ? EMPTY : ((source instanceof String) ? ((String) source).trim() : source.toString());
    }
}
