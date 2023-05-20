package com.example.interceptor;

import com.example.entity.AccountUser;
import com.example.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * separate-project.com.example.interceptor
 *
 * @description 拦截请求
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        User user = (User) authentication.getPrincipal();
        //获取用户名/邮箱查询详细信息
        String username = user.getUsername();
        AccountUser accountUser = userMapper.findAccountUserByUserOrEmail(username);
        request.getSession().setAttribute("account", accountUser);
        return true;
    }
}
