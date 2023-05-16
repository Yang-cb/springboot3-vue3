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
     * @param hasAccount  false 注册账户：要求该账户不存在
     *                    true 重置密码：要求该账户存在
     * @return 是否发送成功
     */
    String sendEmail(String email, HttpSession httpSession, boolean hasAccount);

    /**
     * 注册
     */
    String register(String username, String password, String email, String code, String httpSessionId);

    /**
     * 重置密码
     * 步骤1：验证电子邮件
     */
    String reset1SendEmail(String email, String code, String httpSessionId);

    /**
     * 重置密码
     * 步骤2：重置密码
     */
    boolean reset2Password(String email, String password);
}
