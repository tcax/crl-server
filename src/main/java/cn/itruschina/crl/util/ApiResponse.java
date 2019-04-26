package cn.itruschina.crl.util;

import lombok.Getter;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/24 11:32
 */

public enum ApiResponse {
    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),
    /**
     * 操作失败
     */
    ERROR_FAILED(100, "操作失败"),
    ERROR_EXCEPTION(101, "操作异常"),
    ERROR_PARAMS_MISS(102, "请求参数缺失"),
    ERROR_PARAMS_ILLEGAL(103, "请求参数不合法"),
    ERROR_NOT_EXIST(104, "不存在的信息"),
    ERROR_ENABLE(105, "启用状态"),
    ERROR_DISABLED(106, "禁用状态"),
    ERROR_EXPIRED(107, "过期信息"),
    ERROR_UNKNOWN(108, "未知错误"),
    ERROR_CERT_ENABLE(109, "证书未吊销"),
    ERROR_CERT_REVOKED(110, "证书已吊销"),

    ERROR_UNAUTHORIZED(111, "身份认证失败");

    @Getter
    private int code;
    @Getter
    private String message;

    ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
