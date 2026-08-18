package com.firefox.center.common.utils;

import java.util.List;


/**
 * List工具
 * @author sujie
 *
 */
public class ListUtil {
	
	/**
	 * 判断List数组是否为空
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List<? extends Object> list) {
		return null == list || list.isEmpty();
	}

	/**
	 * 判断list集合是否有值
	 * @param list
	 * @return
	 */
	public static boolean isVal(List<? extends Object> list) {
		if(list!=null && list.size()>0)
		{
			return true;
		}
		return false;
	}

}
