package com.firefox.center.sys.config;

import com.firefox.center.sys.config.property.SwaggerProperties;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.github.xiaoymin.swaggerbootstrapui.filter.ProductionSecurityFilter;
import com.github.xiaoymin.swaggerbootstrapui.filter.SecurityBasicAuthFilter;
import io.swagger.models.MarkdownFiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author scott
 */
@Slf4j
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@ConditionalOnProperty(prefix = SwaggerProperties.PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
@Import({BeanValidatorPluginsConfiguration.class})
public class Swagger2Config implements WebMvcConfigurer {
	/**
	 *
	 * 显示swagger-ui.html文档展示页，还必须注入swagger资源：
	 *
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Slf4j
	public static class SecurityConfiguration {

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnProperty(name = "spring.swagger.production", havingValue = "true")
		public ProductionSecurityFilter swaggerProductionSecurityFilter(SwaggerProperties swaggerProperties) {
			return new ProductionSecurityFilter(swaggerProperties.getProduction());
		}

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnProperty(name = "spring.swagger.basic.enable", havingValue = "true")
		public SecurityBasicAuthFilter swaggerSecurityBasicAuthFilter(SwaggerProperties swaggerProperties) {
			SwaggerProperties.Basic basic = swaggerProperties.getBasic();
			return new SecurityBasicAuthFilter(basic.getEnable(), basic.getUsername(), basic.getPassword());
		}

		@Bean(initMethod = "init")
		@ConditionalOnMissingBean
		@ConditionalOnProperty(name = "spring.swagger.markdown.enable", havingValue = "true")
		public MarkdownFiles swaggerMarkdownFiles(SwaggerProperties swaggerProperties) {
			return new MarkdownFiles(swaggerProperties.getMarkdown().getBasePath());
		}

	}

}
