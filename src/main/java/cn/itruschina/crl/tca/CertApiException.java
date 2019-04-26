package cn.itruschina.crl.tca;

import lombok.Getter;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 10:42
 * @Remark reference from esa-java-3.7.4
 */

public class CertApiException extends Exception {
    @Getter
    protected int errorCode;
    @Getter
    protected String errorMessage;

    public CertApiException(TcaErrCode tcaErrCode) {
        super(tcaErrCode.getErrorMessage());
        this.errorCode = tcaErrCode.getErrorCode();
        this.errorMessage = tcaErrCode.getErrorMessage();
    }

    public CertApiException(TcaErrCode tcaErrCode, Throwable t) {
        super(tcaErrCode.getErrorMessage(), t);
        this.errorCode = tcaErrCode.getErrorCode();
        this.errorMessage = tcaErrCode.getErrorMessage();
    }
}
