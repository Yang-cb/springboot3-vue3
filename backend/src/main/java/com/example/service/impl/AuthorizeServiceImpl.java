package com.example.service.impl;

import com.example.entity.Account;
import com.example.mapper.UserMapper;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * separate-project.com.example.service
 *
 * @description SpringSecurity会将前端填写的username传给 UserDetailService.loadByUserName 方法。
 * 我们只需要根据用户名从数据库中查找到用户信息，
 * 然后封装为UserDetails的实现类（User）返回给SpringSecurity即可，
 * 密码比对工作由 SpringSecurity 完成。
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    @Value("${spring.mail.username}")
    private String username;

    @Resource
    private UserMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //电子邮件相关：来自mail
    @Resource
    private MailSender mailSender;

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

    /**
     * 发送验证码到电子邮件
     *
     * @param email       电子邮件地址
     * @param httpSession 以sessionId为根据，限制发送验证码的时间
     * @return 是否发送成功
     */
    @Override
    public boolean sendEmail(String email, HttpSession httpSession) {
        String key = httpSession.getId() + "_" + email;
        //验证码剩余时间不大于2分钟，可以重发验证码
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120) {
                return false;
            }
        }

        //1，生成验证码
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        //2，发送验证码到邮件
        SimpleMailMessage smm = new SimpleMailMessage();
        //由谁发
        smm.setFrom(username);
        //发给谁
        smm.setTo(email);
        //标题
        smm.setSubject("电子邮件验证");
        //内容
        smm.setText(code + " 是你的邮件验证码");
        try {
            mailSender.send(smm);
            //3，发送成功 -- 将 sessionId_邮箱 => 验证码 键值对存入缓存

            stringRedisTemplate.opsForValue().set(key, String.valueOf(code), 3, TimeUnit.MINUTES);
            return true;
        } catch (MailException e) {
            e.printStackTrace();
            return false;
        }
    }
}