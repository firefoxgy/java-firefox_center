# java-firefox_center_open_platform

[TOC]

## 1. 项目介绍

* 前后端分离的企业级微服务架构
* 基于`Spring Boot 2.0.X`、`Hoxton.RELEASE`和`Spring Cloud Alibaba`
* 深度定制`Spring Security`真正实现了基于`RBAC`、`jwt`和`oauth2`的无状态统一权限认证的解决方案
* 提供应用管理，方便第三方系统接入
* 引入组件化的思想实现高内聚低耦合，项目代码简洁注释丰富上手容易
* 注重代码规范，严格控制包依赖，每个工程基本都是最小依赖
&nbsp;

## 2. 功能介绍

* **统一认证功能**
  * 支持oauth2的四种模式登录
  * 支持用户名、密码加图形验证码登录
  * 支持手机号加密码登录
  * 支持openId登录
  * 支持第三方系统单点登录

* **分布式系统基础支撑**
  * 服务注册发现、路由与负载均衡
  * 服务降级与熔断
  * 服务限流(url/方法级别)
  * 统一配置中心
  * 统一日志中心
  * 统一分布式缓存操作类、cacheManager配置扩展
  * 分布式锁
  * 分布式任务调度器
  * 支持CI/CD持续集成(包括前端和后端)
  * 分布式高性能Id生成器
* **系统监控功能**
  * 服务调用链监控
  * 应用拓扑图
  * 慢服务检测
  * 服务Metric监控
  * 应用监控(应用健康、JVM、内存、线程)
  * 错误日志查询
  * 慢查询SQL监控
  * 应用吞吐量监控(qps、rt)
  * 服务降级、熔断监控
  * 服务限流监控
* **业务基础功能支撑**
  * 高性能方法级幂等性支持
  * RBAC权限管理，实现细粒度控制(方法、url级别)
  * 快速实现导入、导出功能
  * 数据库访问层自动实现crud操作
  * 代码生成器
  * 基于Hutool的各种便利开发工具
  * 网关聚合所有服务的Swagger接口文档
  * 统一跨域处理
  * 统一异常处理

&nbsp;

## 3. 模块说明

```lua
java-firefox_center -- 父项目，公共依赖
│  ├─firefox-api -- feign接口
│  ├─firefox-auth -- spring-security认证中心[8000]
│  ├─firefox-center -- 业务模块一级工程
│  │  ├─app-center -- 应用中心[7001]
│  │  ├─config-center -- 配置中心[7000]
│  │  ├─file-center -- 资源中心[5000]
│  │  ├─log-center -- 日志中心[7099]
│  │  ├─sys-center -- 管理中心[7100]
│  │  ├─user-center -- 用户中心[7002]
│  │  ├─code-generator -- 代码生成器[7300]
│  │─firefox-commons -- 通用工具一级工程
│  │  ├─firefox-auth-client -- 封装spring security client端的通用操作逻辑
│  │  ├─firefox-base -- 封装通用操作逻辑
│  │  ├─firefox-db -- 封装数据库通用操作逻辑
│  │  ├─firefox-log -- 封装log通用操作逻辑
│  │  ├─firefox-mq -- 封装mq通用操作逻辑
│  │  ├─firefox-redis -- 封装Redis通用操作逻辑
│  │  ├─firefox-ribbon -- 封装Ribbon和Feign的通用操作逻辑
│  │  ├─firefox-sentinel -- 封装Sentinel的通用操作逻辑
│  │  ├─firefox-swagger2 -- 封装Swagger通用操作逻辑
│  ├─firefox-config -- 配置中心
│  ├─firefox-doc -- 项目文档
│  ├─firefox-gateway -- gateway网关
│  ├─firefox-job -- 分布式任务调度一级工程
│  │  ├─firefox-job-admin -- 任务管理器[8081]
│  │  ├─firefox-job-core -- 任务调度核心代码
│  │  ├─firefox-job-executor-samples -- 任务执行者executor样例[8082]
│  ├─firefox-monitor -- 监控一级工程
│  │  ├─firefox-monitor-sc-admin -- 应用监控[6500]
│  │  ├─firefox-monitor-log-center -- 日志中心[6200]
│  ├─firefox-register -- 注册中心Nacos[8848]
│  ├─firefox-search -- 搜索引擎一级工程
│  ├─firefox-sidecar -- sidecar
│  ├─firefox-web -- 前端一级工程
│  │  ├─back-web -- 后台前端[8066]
```

&nbsp;