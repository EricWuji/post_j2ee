package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.backend.entity.dto.Account;
import org.example.backend.entity.dto.ForumUser;
import org.example.backend.entity.vo.request.EmailRegisterVO;
import org.example.backend.mapper.AccountMapper;
import org.example.backend.service.AccountService;
import org.example.backend.service.ForumUserService;
import org.example.backend.utils.Const;
import org.example.backend.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    FlowUtils flowUtils;

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder encoder;

    @Resource
    ForumUserService forumUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findUserByUsername(username);
        if (account == null) throw new UsernameNotFoundException("用户名或者密码错误");
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .build();
    }

    public Account findUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {
            if (!verifyLimit(ip)) {
                return "请求过于频繁，请稍后再试";
            }
            Random random = new Random();
            int code = random.nextInt(89999) + 10000;
            Map<String, Object> map = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("mail", map);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_CODE + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }

    }

    @Override
    public String registerEmailAccount(EmailRegisterVO emailRegisterVO) {
        String email = emailRegisterVO.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_CODE + email);
        String username = emailRegisterVO.getUsername();
        if (code == null) return "请先获取邮箱验证码";
        if (!code.equals(emailRegisterVO.getCode())) return "验证码错误或已过期";
        if (existsAccountByEmail(email)) return "该邮箱已被注册";
        if (existsAccountByUsername(username)) return "该用户名已被注册";
        String password = encoder.encode(emailRegisterVO.getPassword());
        Account account = new Account(null, username, password, email, true);
        if (this.save(account)) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_CODE + email);
            return null;
        } else {
            return "注册失败，请稍后再试";
        }
    }

    @Override
    public String getUserRoleByUserId(Integer userId) {
        boolean exists = forumUserService.existsByUserId(userId);
        return exists ? "admin" : "normal user";
    }

    @Override
    public String getUsernameByUserId(Integer userId) {
        Account account = this.getById(userId);
        return account == null ? null : account.getUsername();
    }

    private boolean existsAccountByEmail(String email) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return this.count(queryWrapper) > 0;
    }

    private boolean existsAccountByUsername(String username) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.count(queryWrapper) > 0;
    }

    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flowUtils.limitOnceRequest(key, 60);
    }
}
