package com.example.config;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

/**
 * separate-project.com.example.config
 *
 * @description Security的配置类
 */

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    /**
     * 配置security
     *
     * @param httpSecurity 该bean需要注入HttpSecurity对象（自动注入）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests() //进行权限校验
                .anyRequest()   //所有请求
                .authenticated() //所有人
                .and()
                .formLogin() //前端发起的登录表单
                .loginProcessingUrl("/api/auth/login") //登录API地址
                .successHandler(this::onAuthenticationSuccess) //登陆成功响应内容
                .failureHandler(this::onAuthenticationFailure) //登录失败响应内容
                .and()
                .logout()   //退出登录
                .logoutUrl("api/auth/logout") //退出登录API地址
                .and()
                .csrf().disable() //关闭csrf（方便测试）
                .exceptionHandling() // 统一处理异常：
                .authenticationEntryPoint(this::onAuthenticationFailure)//如果没有权限（未登录）访问资源抛出异常。页面跳转交给前端处理。
                .and()
                .build(); //配置结束
    }

    /**
     * @param authentication 存储登录成功的用户信息
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(Result.success("登录成功")));
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(Result.failure(401, exception.getMessage())));
    }
}
