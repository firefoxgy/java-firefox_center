package com.firefox.center.config.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.firefox.center.common.R;
import com.firefox.center.common.controller.BaseController;
import com.firefox.center.config.config.GatewayRouteProperties;
import com.firefox.center.config.config.NacosGatewayProperties;
import com.firefox.center.config.dto.RouteDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.RouteMatcher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @program: java-firefox_center
 * @description: 网关路由配置接口
 * @author: yungeng
 * @created: 2021/12/02 14:43
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/config/gateway/route")
@Api(tags = "配置中心-网关配置")
@RequiredArgsConstructor
public class GatewayRouteConfigController extends BaseController {

    private static ConfigService configService;

    @Autowired
    private GatewayRouteProperties gatewayRouteProperties;

    @Autowired
    private NacosGatewayProperties nacosGatewayProperties;

    @GetMapping("info")
    @ApiOperation(value="获取路由信息")
    public R<?> info(@RequestParam(value="id", required=false) String id) throws NacosException {
        RouteDTO retRoute = null;
        List<RouteDTO> retList = new ArrayList<>();
        String content = getConfigService().getConfig(gatewayRouteProperties.getDataId(), gatewayRouteProperties.getGroup(), 5000);
        if(!StringUtils.isEmpty(content)) {
            JSONArray routes = JSONArray.parseObject(content, JSONArray.class, Feature.OrderedField);

            if(routes != null) {
                for (int i = 0; i < routes.size(); i ++) {
                    JSONObject route = routes.getJSONObject(i);
                    if(StringUtils.isEmpty(id)) {
                        retList.add(RouteDTO.parse(route));
                    }
                    else if(id.equalsIgnoreCase(route.getString(RouteDTO.KEY_ID))){
                        retRoute = RouteDTO.parse(route);
                        return R.ok(retRoute);
                    }

                }
            }
        }

        if(StringUtils.isEmpty(id))
            return R.ok(retList);
        else
            return R.error("没有对应路由信息");
    }

    @PostMapping("addOrUpdate")
    @ApiOperation(value="修改路由信息")
    public R updateRoute(@RequestBody @Validated RouteDTO routeDTO) throws NacosException {
        if(routeDTO != null ) {
            if(StringUtils.isEmpty(routeDTO.getId())) {
                return R.error("请求参数错误，没有id字段");
            }
            if(StringUtils.isEmpty(routeDTO.getPath())) {
                return R.error("请求参数错误，没有path字段");
            }
            if(StringUtils.isEmpty(routeDTO.getStripPrefix())) {
                return R.error("请求参数错误，没有stripPrefix字段");
            }
        } else {
            return R.error("请求参数错误，请提交id/path/stripPrefix");
        }

        String content = getConfigService().getConfig(gatewayRouteProperties.getDataId(), gatewayRouteProperties.getGroup(), 5000);
        JSONArray routes = new JSONArray();
        boolean isAdd = true;
        if(!StringUtils.isEmpty(content)) {
            routes = JSONArray.parseObject(content, JSONArray.class, Feature.OrderedField);
            if(routes != null) {
                for (int i = 0; i < routes.size(); i ++) {
                    JSONObject route = routes.getJSONObject(i);
                    RouteDTO tmp = RouteDTO.parse(route);

                    if(routeDTO.getId().equalsIgnoreCase(route.getString(RouteDTO.KEY_ID))) {
                        routes.set(i, JSONObject.parseObject(routeDTO.toString(), Feature.OrderedField));
                        isAdd = false;
                    } else if(routeDTO.getPath().equals(tmp.getPath())) //路劲重复，不能添加
                        return R.error(routeDTO, "更新失败，路径与" + tmp.toString() + "重复");

                }
                if(isAdd)
                    routes.add(JSONObject.parseObject(routeDTO.toString(), Feature.OrderedField));
            }
        }
        if(getConfigService().publishConfig(gatewayRouteProperties.getDataId(), gatewayRouteProperties.getGroup(), RouteDTO.toPrettyJsonString(routes))) {
            String message = "更新";
            if(isAdd)
                message = "添加";
            log.info(message + "路由信息成功：{}", routeDTO.toString());
            return R.ok(routeDTO,message + "路由信息成功");
        }
        else
            return R.error("更新路由信息失败");
    }

    @PostMapping("delete")
    @ApiOperation(value="删除路由信息")
    public R delete(@RequestBody @Validated RouteDTO routeDTO) throws NacosException {
        if(routeDTO != null ) {
            if(StringUtils.isEmpty(routeDTO.getId())) {
                return R.error("请求参数错误，没有id字段");
            }
        } else {
            return R.error("请求参数错误，请提交id");
        }

        String content = getConfigService().getConfig(gatewayRouteProperties.getDataId(), gatewayRouteProperties.getGroup(), 5000);
        if(!StringUtils.isEmpty(content)) {
            JSONArray routes = JSONArray.parseObject(content, JSONArray.class, Feature.OrderedField);
            if(routes != null) {
                for (int i = 0; i < routes.size(); i ++) {
                    JSONObject route = routes.getJSONObject(i);
                    if(routeDTO.getId().equalsIgnoreCase(route.getString(RouteDTO.KEY_ID))) {
                        routes.remove(i);
                        if(getConfigService().publishConfig(gatewayRouteProperties.getDataId(), gatewayRouteProperties.getGroup(), RouteDTO.toPrettyJsonString(routes))) {
                            log.info("删除路由信息成功：{}", routeDTO.getId());
                            return R.ok(routeDTO,"删除路由信息成功");
                        }
                        else
                            return R.error("删除路由信息失败");
                    }
                }
            }
        }

        return R.error(routeDTO,"无路由信息");
    }

    public ConfigService getConfigService() throws NacosException {
        if(configService == null) {

            Properties properties =new Properties();
            properties.put(NacosGatewayProperties.KEY_USERNAME,nacosGatewayProperties.getUsername());
            properties.put(NacosGatewayProperties.KEY_PASSWORD,nacosGatewayProperties.getPassword());
            properties.put(NacosGatewayProperties.KEY_NAMESPACE,nacosGatewayProperties.getNamespace());
            properties.put(NacosGatewayProperties.KEY_SERVER_ADDR,nacosGatewayProperties.getServerAddr());
            configService = NacosFactory.createConfigService(properties);
        }

        return configService;
    }

}
