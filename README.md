# java-firefox_center_open_platform

[TOC]

## 1. Project Overview

* An enterprise-grade microservices architecture with separate frontend and backend applications
* Built with `Spring Boot 2.0.X`, `Hoxton.RELEASE`, and `Spring Cloud Alibaba`
* Deeply customized `Spring Security` to provide stateless, unified authentication and authorization based on RBAC, JWT, and OAuth 2.0
* Provides application management for easy integration with third-party systems
* Uses a component-based design to achieve high cohesion and low coupling, with concise, well-documented code that is easy to understand
* Emphasizes coding standards and strictly controls package dependencies, keeping each module's dependencies to a minimum
&nbsp;

## 2. Features

* **Unified Authentication**
  * Supports all four OAuth 2.0 authorization grant types
  * Supports username and password login with a graphical CAPTCHA
  * Supports mobile number and password login
  * Supports OpenID login
  * Supports single sign-on for third-party systems

* **Distributed System Infrastructure**
  * Service registration and discovery, routing, and load balancing
  * Service degradation and circuit breaking
  * Rate limiting at the URL and method levels
  * Unified configuration center
  * Unified logging center
  * Unified distributed cache utilities and extended CacheManager configuration
  * Distributed locking
  * Distributed job scheduling
  * CI/CD support for both frontend and backend applications
  * Distributed high-performance ID generation
* **System Monitoring**
  * Distributed tracing for service calls
  * Application topology visualization
  * Slow-service detection
  * Service metrics monitoring
  * Application monitoring, including health, JVM, memory, and threads
  * Error log search
  * Slow SQL query monitoring
  * Application throughput monitoring, including QPS and RT
  * Service degradation and circuit-breaker monitoring
  * Rate-limiting monitoring
* **Core Business Capabilities**
  * High-performance, method-level idempotency support
  * RBAC permission management with fine-grained control at the method and URL levels
  * Rapid implementation of data import and export
  * Automatic CRUD operations in the database access layer
  * Code generator
  * Convenient development utilities based on Hutool
  * Gateway aggregation of Swagger API documentation from all services
  * Unified cross-origin resource sharing handling
  * Unified exception handling

&nbsp;

## 3. Module Structure

```lua
java-firefox_center -- Parent project and shared dependency management
│  ├─firefox-api -- Feign interfaces
│  ├─firefox-auth -- Spring Security authentication center [8000]
│  ├─firefox-center -- Parent project for business modules
│  │  ├─app-center -- Application center [7001]
│  │  ├─config-center -- Configuration center [7000]
│  │  ├─file-center -- Resource center [5000]
│  │  ├─log-center -- Logging center [7099]
│  │  ├─sys-center -- Administration center [7100]
│  │  ├─user-center -- User center [7002]
│  │  ├─code-generator -- Code generator [7300]
│  │─firefox-commons -- Parent project for shared utilities
│  │  ├─firefox-auth-client -- Shared Spring Security client logic
│  │  ├─firefox-base -- Shared foundational logic
│  │  ├─firefox-db -- Shared database access logic
│  │  ├─firefox-log -- Shared logging logic
│  │  ├─firefox-mq -- Shared message queue logic
│  │  ├─firefox-redis -- Shared Redis logic
│  │  ├─firefox-ribbon -- Shared Ribbon and Feign logic
│  │  ├─firefox-sentinel -- Shared Sentinel logic
│  │  ├─firefox-swagger2 -- Shared Swagger logic
│  ├─firefox-config -- Configuration center
│  ├─firefox-doc -- Project documentation
│  ├─firefox-gateway -- Gateway
│  ├─firefox-job -- Parent project for distributed job scheduling
│  │  ├─firefox-job-admin -- Job administration service [8081]
│  │  ├─firefox-job-core -- Core job scheduling logic
│  │  ├─firefox-job-executor-samples -- Sample job executor [8082]
│  ├─firefox-monitor -- Parent project for monitoring services
│  │  ├─firefox-monitor-sc-admin -- Application monitoring [6500]
│  │  ├─firefox-monitor-log-center -- Logging center [6200]
│  ├─firefox-register -- Nacos service registry [8848]
│  ├─firefox-search -- Parent project for search services
│  ├─firefox-sidecar -- Sidecar service
│  ├─firefox-web -- Parent project for frontend applications
│  │  ├─back-web -- Administration frontend [8066]
```

&nbsp;
