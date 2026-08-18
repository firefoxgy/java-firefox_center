package com.firefox.center.sys.common.exception;

import lombok.Getter;

@Getter
public class RollBackException extends RuntimeException {
    private int code;
    private String msg;

    // 手动设置异常
    public RollBackException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    // 默认异常使用SYSTEM_ERROR状态码
    public RollBackException(String message) {
        super(message);
        this.code = ExceptionCode.TRANS_EX.getCode();
        this.msg = ExceptionCode.TRANS_EX.getMsg();
    }

}