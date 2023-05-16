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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
     * @param hasAccount  false 注册账户：要求该账户不存在
     *                    true 重置密码：要求该账户存在
     * @return 是否发送成功
     */
    @Override
    public String sendEmail(String email, HttpSession httpSession, boolean hasAccount) {
        String key = httpSession.getId() + "_" + hasAccount + "_" + email;
        //验证码剩余时间不大于2分钟，可以重发验证码
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120) {
                return "请求频繁，请稍后重试";
            }
        }

        Account account = userMapper.findAccountByUserOrEmail(email);
        //false 注册账户：要求该账户不存在
        if (!hasAccount && account != null) {
            return "该邮箱已注册";
        }
        //true 重置密码：要求该账户存在
        if (hasAccount && account == null) {
            return "该邮箱还未注册";
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
            return "y";
        } catch (MailException e) {
            e.printStackTrace();
            return "邮件发送失败，请检查邮箱地址是否有效";
        }
    }

    /**
     * 注册
     */
    @Override
    public String register(String username, String password, String email, String code, String httpSessionId) {
        String key = httpSessionId + "_false_" + email;
        //验证 验证码
        String flag = this.codeIsRight(key, code);
        if (!"y".equals(flag)) {
            //验证失败
            return flag;
        }
        //密码加密
        password = encoder.encode(password);
        if (userMapper.findAccountByUserOrEmail(username) != null) {
            return "该用户名已被注册";
        }
        int line = userMapper.createAccount(email, username, password);
        if (line > 0) {
            stringRedisTemplate.delete(key);
            return "y";
        }
        return "系统繁忙，请稍后重试";
    }

    /**
     * 重置密码
     * 步骤1：验证电子邮件
     */
    @Override
    public String reset1SendEmail(String email, String code, String httpSessionId) {
        String key = httpSessionId + "_true_" + email;
        String flag = this.codeIsRight(key, code);
        if ("y".equals(flag)) {
            stringRedisTemplate.delete(key);
        }
        return flag;
    }

    /**
     * 验证验证码
     *
     * @param key      Rides存储的key
     * @param userCode 用户输入的code
     */
    private String codeIsRight(String key, String userCode) {
        //没有key对应的value
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return "请获取验证码";
        }
        String rightCode = stringRedisTemplate.opsForValue().get(key);
        //value没值
        if (!StringUtils.hasText(rightCode)) {
            return "验证码失效，请重新获取";
        }
        //用户填写错误
        if (!rightCode.equals(userCode)) {
            return "验证码错误";
        }
        return "y";
    }

    /**
     * 重置密码
     * 步骤2：重置密码
     */
    @Override
    public boolean reset2Password(String email, String password) {
        password = encoder.encode(password);
        return userMapper.resetPasswordByEmail(password, email) > 0;
    }
}