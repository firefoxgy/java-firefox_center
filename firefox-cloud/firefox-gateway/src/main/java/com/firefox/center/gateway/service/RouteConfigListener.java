package com.firefox.center.gateway.service;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.firefox.center.gateway.config.GatewayRouteProperties;
import com.firefox.center.gateway.config.NacosGatewayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @program: java-firefox_center
 * @description: 监听配置
 * @author: yungeng
 * @created: 2021/12/01 17:51
 */
@Component
@Slf4j
public class RouteConfigListener {

    @Autowired
    private GatewayRouteProperties gatewayRouteProperties;

    @Autowired
    private NacosGatewayProperties nacosGatewayProperties;

    @Autowired
    RouteOperator routeOperator;

    @PostConstruct
    public void dynamicRouteByNacosListener() throws NacosException {

        Properties properties =new Properties();
        properties.put(NacosGatewayProperties.KEY_USERNAME,nacosGatewayProperties.getUsername());
        properties.put(NacosGatewayProperties.KEY_PASSWORD,nacosGatewayProperties.getPassword());
        properties.put(NacosGatewayProperties.KEY_NAMESPACE,nacosGatewayProperties.getNamespace());
        properties.put(NacosGatewayProperties.KEY_SERVER_ADDR,nacosGatewayProperties.getServerAddr());

        ConfigService configService = NacosFactory.createConfigService(properties);

        // 添加监听，nacos上的配置变更后会执行
        configService.addListener(gatewayRouteProperties.getDataId(), gatewayRouteProperties.getGroup(), new Listener() {

            public void receiveConfigInfo(String configInfo) {
                // 解析和处理都交给RouteOperator完成
                routeOperator.refreshAll(configInfo);
            }

            public Executor getExecutor() {
                return null;
            }
        });

        // 获取当前的配置
        String initConfig = configService.getConfig(gatewayRouteProperties.getDataId(), gatewayRouteProperties.getGroup(), 5000);

        // 立即更新
        routeOperator.refreshAll(initConfig);
    }
}
