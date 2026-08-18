package com.firefox.center.common.enums;

import lombok.Getter;

/**
 * @author ZJL
 * @Description: 公共异常代码
 * @Date 2020/6/16
 */
@Getter
public enum CodeEnum {

    /*成功状态码*/
    SUCCESS(0, "成功"),
    /*公共操作失败返回码*/
    FAILURE(-1, "系统错误"),
    SYSTEM_TIMEOUT(-2, "系统维护中~请稍后再试~"),
    PARAM_EX(-3, "参数类型解析异常"),
    SQL_EX(-4, "运行SQL出现异常"),
    NULL_POINT_EX(-5, "空指针异常"),
    ILLEGAL_ARGUMENT_EX(-6, "无效参数异常"),
    MEDIA_TYPE_EX(-7, "请求类型异常"),
    LOAD_RESOURCES_ERROR(-8, "加载资源出错"),
    BASE_VALID_PARAM(-9, "统一验证参数异常"),
    OPERATION_EX(-10, "操作异常"),
    SERVICE_MAPPER_ERROR(-11, "Mapper类转换异常"),
    CAPTCHA_ERROR(-12, "验证码校验失败"),
    JSON_PARSE_ERROR(-13, "JSON解析异常"),

    OK(200, "OK"),
    BAD_REQUEST(400, "错误的请求"),
    UNAUTHORIZED(401, "未经授权"),
    NOT_FOUND(404, "没有找到资源"),
    METHOD_NOT_ALLOWED(405, "不支持当前请求类型"),
    BUSINESS_ERROR_PARAMETER(409, "请求参数错误"),
    BUSINESS_ERROR_DATA(408, "数据异常"),

    TOO_MANY_REQUESTS(429, "请求超过次数限制"),
    INTERNAL_SERVER_ERROR(500, "内部服务错误"),
    BAD_GATEWAY(502, "网关错误"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    NO_AUTHZ(510, "访问权限认证未通过"),

    AUTHORIZATION_FAILURE(601, "认证失败"),
    NONE_AUTHORIZATION(602, "无效的凭证"),
    NONE_IP_USERNAME(603, "无效的IP与用户名"),
    CREATE_TOKEN_ERROR(604, "令牌生成失败"),
    NONE_AUTHORITIES(605, "用户无权限"),
    EXP_AUTHORIZATION(606, "认证已过期"),

    NONE_PACKAGE(607, "无有效的套餐"),

    METABASE_TOKEN_FAIL(608, "metabase获取token失败"),

    NONE_REDIS_KEY(615, "无效的缓存key"),
    PARAMS_INVALID(616, "请求参数错误"),

    BUSINESS_NONE_BUSINESS_ID(621, "业务ID为空"),
    BUSINESS_NONE_SQL(622, "业务执行方法为空"),
    BUSINESS_CLASS_LOADER_ERROR(623, "业务部署配置加载错误"),
    BUSINESS_NONE_CLASS(624, "业务部署错误"),
    BUSINESS_SELECT_ERROR(625, "查询业务错误"),
    BUSINESS_NONE_SAVE_TABLE_NAME(626, "业务表不能为空"),
    BUSINESS_NONE_DELETE_ID(627, "ID不能为空"),
    BUSINESS_NONE_BUSINESS_INFO(628, "业务信息配置错误，可能clazz-name错误"),
    BUSINESS_ERROR(629, "业务执行错误"),

    DATA_SAVE_ERROR(901, "新增数据失败"),
    DATA_UPDATE_ERROR(902, "修改数据失败"),
    TOO_MUCH_DATA_ERROR(903, "批量新增数据过多"),


    /**认证相关>应用**/
    APP_NOT_FOUND(10000,"应用不存在"),
    OAUTH_UNAUTHORIZED(10001,"授权失败"),
    OAUTH_ERROR(10002,"授权发生异常"),
    OAUTH_UNSUPORT(10003,"不支持的认证类型"),

    /**认证相关》用户**/
    NOT_LOGGED_IN(10100,"用户未登录"),
    INVALID_AUTHENTICATION(10101,"无效TOKEN"),
    PERMISSION_DENIED(10102,"权限被拒绝"),
    CLIENT_LOGIN_ERR(10103,"客户端登录异常"),
    OAUTH_EXCEPTION(10104,"未知OAUTH认证异常"),
    REFRESH_TOKEN_ERR(10105,"刷新token异常"),
    USER_NOT_LOGGED_IN(10106,"用户未登录"),
    INVALID_TOKEN(10107,"无效的令牌"),
    USER_PERMISSION_DENIED(10108,"权限被拒绝"),
    USER_NAME_OR_PASSWORD_ERROR(10109,"用户名或密码错误"),
    USERNAME_OR_PASSWORD_NULL(10110,"用户名或密码为空"),
    INVALID_GRANT_REFRESH_TOKEN(10111,"无效的刷新令牌"),
    INVALID_GRANT_AUTHORIZATION_CODE(10112,"无效的授权码"),
    USER_IS_DISABLED(10114,"用户被禁用"),

    VERIFICATION_CODE_ERR(10200,"验证码为空"),
    VERIFICATION_CODE_EMPTY(10201,"验证码错误"),
    VERIFICATION_CODE_EXPIRED(10202,"验证码已过期"),

    PHONE_NULL(10210,"手机号为空"),
    PHONE_VERIFICATION_CODE_NULL(10211,"手机验证码为空"),
    PHONE_CHECK_ERROR(10212,"手机号格式错误"),
    PHONE_VERIFICATION_CODE_ERR_OR_EXPIRE(10213,"手机验证码错误或已过期"),
    PASSWORD_VERIFICATION_FAILURE(10214,"密码验证失败"),
    MAIL_NULL(10215,"邮箱为空"),
    MAIL_CODE_NULL(10216,"验证码为空"),
    MAIL_CHECK_ERROR(10217,"邮箱格式错误"),

    CLIENT_ID_EMPTY(10220,"client_id为空"),
    TENANT_ID_EMPTY(10221,"租户id为空"),
    TENANT_ID_INVALID(10222,"租户id格式错误"),
    TENANT_NOT_EXIST(10223,"租户不存在"),
    TENANT_IS_DISABLED(10224,"租户不存在"),
    TENANT_NO_OAUTH(10225,"租户未开通此应用"),
    TENANT_NO_PACKAGE(10226,"租户未购买服务中心套餐"),

    THIRD_ID_EMPTY(10230,"thirdid为空"),
    ACCESS_TOKEN_EMPTY(10231,"access_token为空"),
    AUTH_CODE_EMPTY(10232,"code为空"),
    THIRD_ALREADY_BIND(10233,"已绑定第三方帐号"),
    APP_THIRD_CONF_EMPTY(10234,"应用第三方登录配置信息缺失"),
    WEIXIN_GET_ACCESSTOKEN_ERROR(10235,"查询accessToken接口调用失败"),
    WEIXIN_USERINFO_QUERY_ERROR(10236,"查询微信用户信息接口调用失败"),
    APPLE_TOKEN_EXPIRE(10237,"授权码验证失败"),
    UID_EMPTY(10238,"uid为空"),
    SID_EMPTY(10239,"sid为空"),
    USER_NOT_EXIST(10240,"sid为空"),
    TOKEN_EMPTY(10241,"token为空"),
    ID_TOKEN_INVALID(10242,"无法识别的identityToken"),

    PARAMS_ARGS_NULL(10300,"args参数为空"),
    PARAMS_SIGN_NULL(10301,"sign参数为空"),
    PARAMS_TS_NULL(10302,"时间戳ts参数为空"),
    SIGN_ERROR(10303,"签名错误"),
    TS_OVERDUE(10304,"时间戳差异超过5分钟"),

    TENANT_API_NO_AUTH(10400,"无此api访问权限"),

    /**认证相关》短信**/
    SMS_APP_TEMPLATE_NULL(11000,"应用未设置发送短信模板"),
    SMS_SEND_ERROR(11001,"短信发送失败"),

    /**业务相关》用户**/
    UID_NOT_FOUND(12000,"用户不存在"),
    USER_PASSWORD_ERROR(12001,"密码错误")

    ;


    /**
     * 状态码
     */
    private Integer code;
    /**
     * 消息
     */
    private String message;

    CodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static CodeEnum getException(int code) {
        for (CodeEnum ele : CodeEnum.values()) {
            if (ele.getCode() == code) {
                return ele;
            }
        }
        return null;
    }

}
