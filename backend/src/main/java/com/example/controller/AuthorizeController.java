package com.example.controller;

import com.example.entity.Result;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * separate-project.com.example.controller
 *
 * @description 验证相关Controller
 */
// @Validated 开启“检验程序代码中参数的有效性”
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    //检验邮箱是否合法的正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";

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
        boolean flag = authorizeService.sendEmail(email, httpSession);
        if (flag) {
            return Result.success("邮件发送成功");
        } else {
            return Result.failure(400, "系统繁忙，请稍后重试");
        }
    }
}
