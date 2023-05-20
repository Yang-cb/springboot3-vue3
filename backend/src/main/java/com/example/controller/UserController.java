package com.example.controller;

import com.example.entity.AccountUser;
import com.example.entity.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * separate-project.com.example.controller
 *
 * @description 获取用户信息
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    /**
     * @param accountUser @SessionAttribute可以拿到保存在session中的数据
     * @return 将其封装为AccountUser返回给前端
     */
    @GetMapping("/detail")
    public Result<AccountUser> detail(@SessionAttribute("account") AccountUser accountUser) {
        return Result.success(accountUser);
    }
}
