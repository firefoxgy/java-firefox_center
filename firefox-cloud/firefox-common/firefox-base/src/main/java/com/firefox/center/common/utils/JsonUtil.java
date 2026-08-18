package com.firefox.center.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Description: Json工具类
 * @author sujie
 * @since JDK 1.8
 * date: 2020/7/9 14:30
 */
public class JsonUtil {


    /**
     *  对象转json字符串
     * @param obj
     * @Author sujie
     * @return String
     */
    public static String objectToJson(Object obj){
        try {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        StringWriter sw = new StringWriter();
        objectMapper.writeValue(sw,obj);
        return sw.toString();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("IOException from a StringWriter");
        }
    }

    /**
     *  json字符串转map
     * @param data
     * @Author sujie
     * @return String
     */
    public static Map<String,Object> jsonToMap(String data){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(data, Map.class);
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("IOException from a objectMapper.readValue");
        }
    }

    /**
     *  json字符串转对象
     * @param data json数据
     * @param valueClass 对象类型
     * @param fullmap 是否忽略空值 为true忽略
     * @Author sujie
     * @return String
     */
    public static <V> V jsonToObject(String data, Class<V> valueClass, boolean fullmap){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(!fullmap){
                objectMapper.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            }
            return objectMapper.readValue(data,valueClass);
        }catch (JsonParseException e){
            e.printStackTrace();
        }catch (JsonMappingException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  json字符串转对象
     * @param data 接送数据
     * @param valueClass 对象类型
     * @Author sujie
     * @return String
     */
    public static <V> V jsonToObject(String data, Class<V> valueClass){
        return jsonToObject(data,valueClass,false);
    }
}
