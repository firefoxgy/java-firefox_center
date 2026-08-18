package com.firefox.center.common.utils;

import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * map工具类
 */
public class MapUtil {
    /**
     * map转换DO对象
     *
     * @param clazz
     * @param <V>
     * @return
     */
    public static <V> V mapToDO(Map<String, Object> map, Class<V> clazz) throws Exception {
        String string = JsonUtil.objectToJson(map);
        V result = JsonUtil.jsonToObject(string, clazz, false);
        return result;
    }


    /**
     * map转换DO对象
     *
     * @param <V>
     * @param valueClass
     * @return
     */
    public static <V> List mapToDO(List<Map<String, Object>> list, Class<V> valueClass) throws Exception {
        List result = new ArrayList();
        if (!ListUtil.isEmpty(list)) {
            for (Map<String, Object> map : list) {
                result.add(mapToDO(map, valueClass));
            }
        }
        return result;
    }

    /**
     * 对象转map
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }


    /**
     * list转map
     *
     * @param list
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> objectToMap(List<?> list ) throws Exception {
       List res = Lists.newArrayList();
       if(!ListUtil.isEmpty(list)){
           for (Object o : list) {
               res.add(objectToMap(o));
           }
       }
       return res;
    }


}
