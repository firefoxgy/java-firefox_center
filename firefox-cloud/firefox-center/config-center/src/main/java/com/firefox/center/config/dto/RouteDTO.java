package com.firefox.center.config.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.firefox.center.common.R;
import lombok.Data;
import org.springframework.util.RouteMatcher;
import org.springframework.util.StringUtils;

/**
 * @program: java-firefox_center
 * @description: 路由配置信息
 * @author: yungeng
 * @created: 2021/12/02 14:48
 */
@Data
public class RouteDTO {
    public static final String KEY_ID = "id";
    public static final String KEY_PREDICATES = "predicates";
    public static final String KEY_NAME = "name";
    public static final String KEY_ARGS = "args";
    public static final String KEY_PATTERN = "pattern";
    public static final String KEY_STRIP_PREFIX = "StripPrefix";
    public static final String KEY_GENKEY_0 = "_genkey_0";
    public static final String KEY_FILTERS = "filters";
    public static final String KEY_PATH = "Path";

    public static final String ROUTE_CONFIG = " {\n"
        + "        \"id\": \"{id}\",\n"
        + "        \"uri\": \"lb://{id}\",\n"
        + "        \"predicates\":[\n"
        + "            {\n"
        + "                \"name\": \"Path\",\n"
        + "                \"args\": {\n"
        + "                    \"pattern\": \"{path}\"\n"
        + "                }\n" + "            }\n"
        + "        ],\n"
        + "        \"filters\": [\n" + "            {\n"
        + "                \"name\": \"StripPrefix\",\n"
        + "                \"args\": {\n"
        + "                    \"_genkey_0\":\"{stripPrefix}\"\n"
        + "                }\n"
        + "            }\n"
        + "        ]\n"
        + "    }";

    private String id;
    private String uri;
    private String path;
    private String stripPrefix;
    private JSONArray filters;

    public String toString() {
        String routeInfo = ROUTE_CONFIG.replace("{id}",id)
            .replace("{path}", path)
            .replace("{stripPrefix}",stripPrefix);
        JSONObject routeJson = JSONObject.parseObject(routeInfo, Feature.OrderedField);
        JSONArray filtersJson = routeJson.getJSONArray("filters");
        if(filtersJson != null && filters != null)
            filtersJson.addAll(filters);
        return toPrettyJsonString(routeJson);
    }

    public void setId(String id) {
        this.id = id;
        this.uri = "lb:" + id;
    }

    public static RouteDTO parse(JSONObject jsonRoute) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setId(jsonRoute.getString(KEY_ID));
        JSONArray jsonPres = jsonRoute.getJSONArray(KEY_PREDICATES);
        for(int i = 0; jsonPres != null && jsonPres.size()>0 && i < jsonPres.size(); i ++) {
            JSONObject jsonPre = jsonPres.getJSONObject(i);
            if (jsonPre != null && !StringUtils.isEmpty(jsonPre.getString(KEY_NAME)) && KEY_PATH.equalsIgnoreCase(jsonPre.getString(KEY_NAME))) {
                if(jsonPre.getJSONObject(KEY_ARGS) != null)
                    routeDTO.setPath(jsonPre.getJSONObject(KEY_ARGS).getString(KEY_PATTERN));
            }
        }
        JSONArray jsonFilters = jsonRoute.getJSONArray(KEY_FILTERS);

        int stripIndex = -1;
        for(int i = 0; jsonFilters != null && jsonFilters.size()>0 && i < jsonFilters.size(); i ++) {
            JSONObject jsonFilter = jsonFilters.getJSONObject(i);
            if (jsonFilter != null && !StringUtils.isEmpty(jsonFilter.getString(KEY_NAME)) && KEY_STRIP_PREFIX.equalsIgnoreCase(jsonFilter.getString(KEY_NAME))) {
                stripIndex = i;
                if(jsonFilter.getJSONObject(KEY_ARGS) != null)
                    routeDTO.setStripPrefix(jsonFilter.getJSONObject(KEY_ARGS).getString(KEY_GENKEY_0));
            }
        }
        JSONArray filters = (JSONArray)jsonFilters.clone();
        filters.remove(stripIndex);
        routeDTO.setFilters(filters);
        return routeDTO;
    }

    public static String toPrettyJsonString(Object object) {
        return JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteDateUseDateFormat);
    }

    public static void main(String[] args)
    {
        JSONObject test = JSONObject.parseObject(ROUTE_CONFIG, Feature.OrderedField);

        System.out.println(test);

        String pretty = JSON.toJSONString(test, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteDateUseDateFormat);

        System.out.println(pretty);


    }
}
