package com.firefox.center.sys.common.exception;

public enum ExceptionCode {

    //系统相关 start
    SUCCESS(0, "成功"),
    SYSTEM_ERROR(-1, "系统错误"),
    SYSTEM_TIMEOUT(-2, "系统维护中~请稍后再试~"),
    PARAM_EX(-3, "参数类型解析异常"),
    SQL_EX(-4, "运行SQL出现异常"),
    NULL_POINT_EX(-5, "空指针异常"),
    ILLEGALA_ARGUMENT_EX(-6, "无效参数异常"),
    MEDIA_TYPE_EX(-7, "请求类型异常"),
    LOAD_RESOURCES_ERROR(-8, "加载资源出错"),
    BASE_VALID_PARAM(-9, "统一验证参数异常"),
    OPERATION_EX(-10, "操作异常"),
    TRANS_EX(-11, "事务异常"),

    OK(200, "OK"),
    BAD_REQUEST(200, "错误的请求"),
    UNAUTHORIZED(201, "未经授权"),
    NOT_FOUND(204, "没有找到资源"),
    METHOD_NOT_ALLOWED(205, "不支持当前请求类型"),

    TOO_MANY_REQUESTS(429, "请求超过次数限制"),
    INTERNAL_SERVER_ERROR(500, "内部服务错误"),
    BAD_GATEWAY(502, "网关错误"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    //系统相关 end

    //错误 start
    ID_IS_NULL(10000, "id为空"),

    //支付错误 start
    //微信小程序错误 start
    WX_MINI_TOKEN_(20000, "token验证失败"),
    WX_MINI_APPID_GET_FAIL(20001, "appId获取失败"),
    WX_MINI_USER_NULL(20002, "用户不存在"),
    WX_MINI_USER_UNBIND(20003, "用户未绑定"),
    WX_MINI_USER_MISS(20004, "openId对应的用户已不存在"),
    WX_MINI_TOKEN_NULL(20005, "x-access-token请求头为空"),
    WX_MINI_TOKEN_ERROR(20006, "x-access-token解析失败"),
    WX_MINI_TOKEN_INVALID(20007, "无效的x-access-token"),

    PAY_TOKEN_NUll(20100, "支付token获取失败"),
    PAY_REQ_FAIL(20101, "支付接口请求失败"),
    //错误 end
    ;

    private int code;
    private String msg;

    ExceptionCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public ExceptionCode build(String msg, Object... param) {
        this.msg = String.format(msg, param);
        return this;
    }

    public ExceptionCode param(Object... param) {
        msg = String.format(msg, param);
        return this;
    }
}
