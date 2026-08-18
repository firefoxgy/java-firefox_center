package com.firefox.center.sys.common.util;

import org.springframework.util.DigestUtils;

public class MD5Util {
	//盐，用于混交md5
	private static final String slat = "&%5123***&&%%$$#@";

	public static String getMD5(String str) {
		//String base = str +"/"+slat;
		String md5 = DigestUtils.md5DigestAsHex(str.getBytes());
		return md5;
	}

}
