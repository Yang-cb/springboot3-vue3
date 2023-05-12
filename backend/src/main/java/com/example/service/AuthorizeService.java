package com.example.service;

import com.example.entity.Account;
import com.example.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * separate-project.com.example.service
 *
 * @description SpringSecurity会将前端填写的username传给 UserDetailService.loadByUserName 方法。
 * 我们只需要根据用户名从数据库中查找到用户信息，
 * 然后封装为UserDetails的实现类（User）返回给SpringSecurity即可，
 * 密码比对工作由 SpringSecurity 完成。
 */
@Service
public class AuthorizeService implements UserDetailsService {
    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account account = userMapper.findAccountByUserOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user") //角色（权限）
                .build();
    }
}