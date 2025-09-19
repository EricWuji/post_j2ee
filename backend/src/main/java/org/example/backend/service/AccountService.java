package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.Account;
import org.example.backend.entity.vo.request.EmailRegisterVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findUserByUsername(String username);
    String registerEmailVerifyCode(String type, String email, String ip);
    String registerEmailAccount(EmailRegisterVO emailRegisterVO);
    String getUserRoleByUserId(Integer userId);
    String getUsernameByUserId(Integer userId);
}
