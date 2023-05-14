package com.example.config;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * separate-project.com.example.config
 *
 * @description Security的配置类
 */

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Resource
    private AuthorizeService authorizeService;

    //用于持久化存储“记住我”功能的Token
    @Resource
    private DataSource dataSource;

    /**
     * 配置security
     *
     * @param httpSecurity    该bean需要注入HttpSecurity对象（自动注入）
     *                        HttpSecurity定义了认证需要的相关信息
     * @param tokenRepository 持久化token存储库配置：用于实现“记住我”功能
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, PersistentTokenRepository tokenRepository) throws Exception {
        return httpSecurity.authorizeHttpRequests() //进行权限校验
                .anyRequest()   //所有请求
                .authenticated() //所有人

                .and().formLogin() //前端发起的登录表单
                .loginProcessingUrl("/api/auth/login") //登录API地址
                .successHandler(this::onAuthenticationSuccess) //登陆成功响应内容
                .failureHandler(this::onAuthenticationFailure) //登录失败响应内容

                .and().logout()   //退出登录
                .logoutUrl("/api/auth/logout") //退出登录API地址
                .logoutSuccessHandler(this::onAuthenticationSuccess)//退出登陆成功响应内容

                .and().rememberMe() //“记住我”功能
                .rememberMeParameter("remember") //参数名默认为 remember-me，如果前端使用的是remember-me，那么可以不用配置此项
                .tokenRepository(tokenRepository) //指定存储库
                .tokenValiditySeconds(3600 * 24 * 7) //过期时间：7天

                .and().csrf().disable() //关闭csrf（方便测试）

                .cors() //配置允许的跨域请求路径
                .configurationSource(this.corsConfigurationSource())

                .and().exceptionHandling() // 统一处理异常：
                .authenticationEntryPoint(this::onAuthenticationFailure)//如果没有权限（未登录）访问资源抛出异常。页面跳转交给前端处理。

                .and().build(); //配置结束
    }

    /**
     * 持久化token存储库：需要注册为Bean注入到过滤器SecurityFilterChain中
     */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        //设置数据源
        tokenRepository.setDataSource(dataSource);
        //是否自动创建表
        tokenRepository.setCreateTableOnStartup(false);
        return tokenRepository;
    }

    /**
     * 配置允许的跨域请求路径
     * 不配置的错误信息：
     * Access to XMLHttpRequest at '<a href="http://localhost:8080/api/auth/login">...</a>'
     * from origin '<a href="http://localhost:5173">...</a>' has been blocked by CORS policy:
     * No 'Access-Control-Allow-Origin' header is present on the requested resource.
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 参数代表允许的请求路径，* 表示所有
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("*");

        // 允许携带cookie
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


    /**
     * 对用户的未授信凭据进行认证，
     * 认证通过则返回授信状态的凭据，否则将抛出认证异常AuthenticationException。
     *
     * @param httpSecurity 定义了认证需要的相关信息
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(authorizeService).and().build();
    }

    /**
     * SpringSecurity中的加密方法BCryptPasswordEncoder。
     *
     * @return 加密器对象
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @param authentication 存储登录成功的用户信息
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("utf-8");
        if (request.getRequestURI().endsWith("/login")) {
            //登录请求
            response.getWriter().write(JSONObject.toJSONString(Result.success("登录成功")));
        } else if (request.getRequestURI().endsWith("/logout")) {
            //退出登录请求
            response.getWriter().write(JSONObject.toJSONString(Result.success("已退出登录")));
        }
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(Result.failure(401, exception.getMessage())));
    }
}
