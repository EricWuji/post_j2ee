package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.backend.entity.dto.Account;
import org.example.backend.mapper.UserMapper;
import org.example.backend.service.AccountService;
import org.example.backend.utils.Const;
import org.example.backend.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<UserMapper, Account> implements AccountService {

    @Resource
    FlowUtils flowUtils;

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

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

    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flowUtils.limitOnceRequest(key, 60);
    }
}
