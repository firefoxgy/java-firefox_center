package com.firefox.center.common.constants;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/3/9 3:07
 */
public class Consts {

    public static class user{
        public static final int USER_TYPE_USER = 1; // 1前台用户 2后台用户
        public static final int USER_TYPE_MANAGE = 2; // 1前台用户 2后台用户
    }

    public static class getWay{
        public static final String PREFIX = "/api"; // 网关前缀
    }

    public static class oauth{
        public static final String PARAM_GRANT_TYPE = "grant_type";
        public static final String PARAM_CLIENT_ID = "client_id";
        public static final String PARAM_USER_TYPE = "user_type";
        public static final String PARAM_TENANT_ID = "tenant_id";
        public static final String PARAM_USERNAME = "username";
        public static final String PASSWORD = "pass";

    }

    public static class grantType{
        public static final String MAIL = "mail";
        public static final String QQ = "qq";
        public static final String WEIXIN = "wx";
        public static final String WEIBO = "weibo";
        public static final String APPLEID = "appleId";
        public static String[] types={QQ, WEIXIN, WEIBO, APPLEID};

    }

    public static class paramsSMS{
        public static final String PARAM_PHONE = "phone";
        public static final String PARAM_CODE = "code";
        public static final String PARAM_USERNAME = "username";
        public static final String PARAM_PASSWORD = "password";

    }

    public static class paramsThird{
        public static final String PARAM_THIRDID = "thirdid";
        public static final String PARAM_ACCESS_TOKEN = "access_token";
        public static final String PARAM_NICKNAME = "nickname";
        public static final String PARAM_FIGUREURL = "figureurl";
        public static final String PARAM_GENDER = "gender";

    }

}
