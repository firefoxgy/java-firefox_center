package com.firefox.center.gateway.component;//package com.firefox.gateway.component;
//
//import com.alibaba.nacos.client.naming.utils.CollectionUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
//import org.springframework.cloud.gateway.route.RouteDefinition;
//import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
//import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
///**
// * 动态更新路由网关service
// * 1）实现一个Spring提供的事件推送接口ApplicationEventPublisherAware
// * 2）提供动态路由的基础方法，可通过获取bean操作该类的方法。该类提供新增路由、更新路由、删除路由，然后实现发布的功能。
// *
// * @Author 苏杰
// * @CreateTime 2021/2/24 16:53
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {
//    private final RouteDefinitionWriter routeDefinitionWriter;
//    private final RouteDefinitionLocator routeDefinitionLocator;
//    private ApplicationEventPublisher publisher;
//
//    @Override
//    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//        this.publisher = applicationEventPublisher;
//    }
//
//    /**
//     * 删除路由
//     * @param id
//     * @return
//     */
//    public String delete(String id) {
//        try {
//            log.info("gateway delete route id {}",id);
//            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
//            this.publisher.publishEvent(new RefreshRoutesEvent(this));
//            return "delete success";
//        } catch (Exception e) {
//            return "delete fail";
//        }
//    }
//
//    /**
//     * 更新路由
//     * @param definitions
//     * @return
//     */
//    public String updateList(List<RouteDefinition> definitions) {
//        log.info("gateway update route {}",definitions);
//        // 删除缓存routerDefinition
//        List<RouteDefinition> routeDefinitionsExits =  routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
//        if (!CollectionUtils.isEmpty(routeDefinitionsExits)) {
//            routeDefinitionsExits.forEach(routeDefinition -> {
//                log.info("delete routeDefinition:{}", routeDefinition);
//                delete(routeDefinition.getId());
//            });
//        }
//        definitions.forEach(definition -> {
//            updateById(definition);
//        });
//        return "success";
//    }
//
//    /**
//     * 更新路由
//     * @param definition
//     * @return
//     */
//    public String updateById(RouteDefinition definition) {
//        try {
//            log.info("gateway update route {}",definition);
//            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
//        } catch (Exception e) {
//            return "update fail,not find route  routeId: "+definition.getId();
//        }
//        try {
//            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
//            this.publisher.publishEvent(new RefreshRoutesEvent(this));
//            return "success";
//        } catch (Exception e) {
//            return "update route fail";
//        }
//    }
//
//    /**
//     * 增加路由
//     * @param definition
//     * @return
//     */
//    public String add(RouteDefinition definition) {
//        log.info("gateway add route {}",definition);
//        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
//        this.publisher.publishEvent(new RefreshRoutesEvent(this));
//        return "success";
//    }
//}
