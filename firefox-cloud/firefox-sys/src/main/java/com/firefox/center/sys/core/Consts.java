package com.firefox.center.sys.core;

/**
 * 	系统常量
 */
public final class Consts {


	public static class Config{
		public static final String BASE_DEFAULT_PASSWORD = "base_default_password";
		public static final String CONF_GROUP_CODE = "system_config_group_list";
	}

	public static class Dic{
		public static final String ROOM_TYPE_CODE ="daily_room_type";
		public static final String BASE_USER_TYPE ="base_user_type";
	}

	public static class Role{
		public static final String ROLE_SUPER_ID ="f6817f48af4fb3af11b9e8bf182f618b";
		//前台角色
		public static final String ROLE_FRONT_ID ="1344541341782159362";
	}

	public static class User{
		public static final String SUPER_ID ="a75d45a015c44384a04449ee80dc350";
		public static final String SUPER ="super";

		public static final String ADMIN_ID ="8d23aea157db4e219f7b142eeba6df7a";
		public static final String ADMIN ="admin";

		public static boolean isAdmin(String userId){
			return userId.equals(SUPER_ID) || userId.equals(ADMIN_ID);
		}
	}
}