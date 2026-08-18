package com.firefox.center.sys.common.constant;

/**
 * @description: 缓存常量
 */
public interface CacheConstant {

	public static class Base{
		/**
		 * 全部公司信息缓存
		 */
		public static final String BASE_CORP_CACHE = "base:cache:corp:alldata";
		/**
		 * 全部部门信息缓存
		 */
		public static final String BASE_DEPT_CACHE = "base:cache:dept:alldata";
		/**
		 * 全部用户信息缓存
		 */
		public static final String BASE_USER_CACHE = "base:cache:user:alldata";
		/**
		 * 全部微信用户信息缓存
		 */
		public static final String BASE_WX_USER_CACHE = "base:cache:wx:user:alldata";
	}

	public static class Sys{
		/**
		 * 字典信息缓存
		 */
		public static final String SYS_DICT_CACHE = "sys:cache:dict";
		/**
		 * 表字典信息缓存
		 */
		public static final String SYS_DICT_TABLE_CACHE = "sys:cache:dictTable";
		public static final String SYS_DICT_TABLE_BY_KEYS_CACHE = SYS_DICT_TABLE_CACHE + "ByKeys";

		/**
		 * 数据权限配置缓存
		 */
		public static final String SYS_DATA_PERMISSIONS_CACHE = "sys:cache:permission:datarules";

		/**
		 * 缓存用户信息
		 */
		public static final String SYS_USERS_CACHE = "sys:cache:user";

		/**
		 * 全部部门信息缓存
		 */
		public static final String SYS_DEPARTS_CACHE = "sys:cache:depart:alldata";


		/**
		 * 全部部门ids缓存
		 */
		public static final String SYS_DEPART_IDS_CACHE = "sys:cache:depart:allids";


		/**
		 * 测试缓存key
		 */
		public static final String TEST_DEMO_CACHE = "test:demo";

		/**
		 * 字典信息缓存
		 */
		public static final String SYS_DYNAMICDB_CACHE = "sys:cache:dbconnect:dynamic:";
	}



}
