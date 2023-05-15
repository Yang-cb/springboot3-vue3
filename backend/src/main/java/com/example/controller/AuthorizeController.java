package com.example.controller;

import com.example.entity.Result;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * separate-project.com.example.controller
 *
 * @description 验证相关Controller
 */
// @Validated 开启“检验程序代码中参数的有效性”
@Validated
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    //检验邮箱是否合法的正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    @Resource
    private AuthorizeService authorizeService;

    /**
     * 发送验证码到电子邮件
     *
     * @param email       电子邮件地址
     * @param httpSession 以sessionId为根据，限制发送验证码的时间
     */
    @PostMapping("/sendEmail")
    public Result<String> sendEmail(@Pattern(regexp = EMAIL_REGEX) @RequestParam String email, HttpSession httpSession) {
        String flag = authorizeService.sendEmail(email, httpSession);
        if ("y".equals(flag)) {
            return Result.success("邮件发送成功");
        } else {
            return Result.failure(400, flag);
        }
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<String> register(@Pattern(regexp = USERNAME_REGEX) @Length(min = 2, max = 8) @RequestParam String username,
                                   @Length(min = 6, max = 16) @RequestParam String password,
                                   @Pattern(regexp = EMAIL_REGEX) @RequestParam String email,
                                   @Length(min = 6, max = 6) @RequestParam String code,
                                   HttpSession session) {
        String flag = authorizeService.register(username, password, email, code, session.getId());
        if ("y".equals(flag)) {
            return Result.success("注册成功");
        } else {
            return Result.failure(400, flag);
        }
    }
}
