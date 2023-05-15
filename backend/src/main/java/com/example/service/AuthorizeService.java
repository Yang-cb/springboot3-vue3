package com.example.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * separate-project.com.example.service
 *
 * @description 描述
 */
public interface AuthorizeService extends UserDetailsService {
    /**
     * 发送验证码到电子邮件
     *
     * @param email       电子邮件地址
     * @param httpSession 以sessionId为根据，限制发送验证码的时间
     * @return 是否发送成功
     */
    String sendEmail(String email, HttpSession httpSession);

    /**
     * 注册
     */
    String register(String username, String password, String email, String code, String httpSessionId);
}
