package com.firefox.center.common;

import com.alibaba.fastjson.JSON;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class R<T> {

    public static final String DEF_ERROR_MESSAGE = "系统繁忙，请稍候再试";
    public static final String HYSTRIX_ERROR_MESSAGE = "请求超时，请稍候再试";
    public static final String DEFAULT_MESSAGE_SUCCESS = "success";
    public static final String DEFAULT_MESSAGE_FAIL = "fail";

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_FAIL = -1;
    public static final int CODE_TIMEOUT = -2;
    //统一参数验证异常
    public static final int CODE_VALID_EX = -9;
    public static final int CODE_OPERATION_EX = -10;


    private boolean success = true;
    //调用是否成功标识，0：成功，-1:系统繁忙
    private int code;

    //结果消息，如果调用成功，消息通常为空T
    private String msg = "成功";

    //调用结果
    private T data;

    //响应时间
    private long timestamp = System.currentTimeMillis();

    public R() {
        this.timestamp = System.currentTimeMillis();
    }

    public R(boolean success, int code, String msg, T data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return new R<T>(true, CODE_SUCCESS, DEFAULT_MESSAGE_SUCCESS, null);
    }

    public static <T> R<T> ok(String msg) {
        return new R<T>(true, CODE_SUCCESS, msg, null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(true, CODE_SUCCESS, DEFAULT_MESSAGE_SUCCESS, data);
    }

    public static <T> R<T> ok(T data, String msg) {
        return new R<>(true, CODE_SUCCESS, msg, data);
    }

    public static <T> R<T> ok(int code, T data, String msg) {
        return new R<>(true, code, msg, data);
    }

    public static <T> R<T> error() {
        return new R<T>(false, CODE_FAIL, DEFAULT_MESSAGE_FAIL, null);
    }

    public static <T> R<T> error(String msg) {
        return new R<T>(false, CODE_FAIL, msg, null);
    }

    public static <T> R<T> error(T data) {
        return new R<>(false, CODE_FAIL, DEFAULT_MESSAGE_FAIL, data);
    }

    public static <T> R<T> error(T data, String msg) {
        return new R<>(false, CODE_FAIL, msg, data);
    }

    public static <T> R<T> error(int code, String msg) {
        return new R<>(false, code, (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg,  null);
    }

    public static <T> R<T> error(int code, String msg, T data) {
        return new R<>(false, code, (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg,  data);
    }

    public static <T> R<T> error(CodeEnum responseCode) {
        return validFail(responseCode);
    }

    public static <T> R<T> error(BusinessException exception) {
        if (exception == null) {
            return error(DEF_ERROR_MESSAGE);
        }
        return new R<>(false, exception.getCode(), exception.getMessage(), null );
    }

    /**
     * 请求失败消息，根据异常类型，获取不同的提供消息
     *
     * @param throwable 异常
     * @return RPC调用结果
     */
    public static <T> R<T> error(Throwable throwable) {
        String msg = throwable != null ? throwable.getMessage() : DEF_ERROR_MESSAGE;
        return error(CODE_FAIL, msg, null);
    }

    public static <T> R<T> noauth(String msg) {
        return error(CodeEnum.NO_AUTHZ.getCode(), msg, null);
    }

    public static <T> R<T> validFail(String msg) {
        return new R<>(false, CODE_VALID_EX, (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg, null);
    }

    public static <T> R<T> validFail(String msg, Object... args) {
        String message = (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg;
        return new R<>(false, CODE_VALID_EX, String.format(message, args), null);
    }

    public static <T> R<T> validFail(CodeEnum responseCode) {
        return new R<>(false, responseCode.getCode(),
                (responseCode.getMessage() == null || responseCode.getMessage().isEmpty()) ? DEF_ERROR_MESSAGE : responseCode.getMessage(), null);
    }

    public static <T> R<T> error(CodeEnum responseCode, T data) {
        return new R<>(false, responseCode.getCode(), responseCode.getMessage(), data);
    }

    public static <T> R<T> timeout() {
        return error(CODE_TIMEOUT, HYSTRIX_ERROR_MESSAGE);
    }

    /**
     * 逻辑处理是否成功
     *
     * @return 是否成功
     */
    public Boolean isSuccess() {
        return this.success == true;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public R okMsg(String msg) {
        this.success = true;
        this.msg=msg;
        return this;
    }

    public R errorMsg(String msg) {
        this.success = false;
        this.msg=msg;
        return this;
    }

}
