package com.firefox.center.credit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/4/27 17:49
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //允许任何请求访问
        //http.authorizeRequests().antMatchers("/**").permitAll();
//        http
//                .authorizeRequests()
//                .antMatchers("/index.html","login.html")
//                .permitAll();
        // 禁用掉csrf
//        http.csrf().disable();
        http.requestMatchers().anyRequest();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>
                .ExpressionInterceptUrlRegistry registry = http
                .authorizeRequests();
        registry.anyRequest().permitAll().and().csrf().disable();
    }
}
