package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.Account;
import org.example.backend.entity.vo.request.EmailRegisterVO;
import org.example.backend.entity.vo.request.EmailResetVO;
import org.example.backend.entity.vo.response.AccountResponseVO;
import org.example.backend.entity.vo.response.ForumAvailableResponseVO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findUserByUsername(String username);
    String emailVerifyCode(String type, String email, String ip);
    String registerEmailAccount(EmailRegisterVO emailRegisterVO);
    String resetPassword(EmailResetVO emailResetVO);
    String getUserRoleByUserId(Integer userId);
    String getUsernameByUserId(Integer userId);
    List<AccountResponseVO> getUsersByUsername(String username);
    List<ForumAvailableResponseVO> getAvailableForumsByUserId(Integer userId, Integer targetUserId);
    List<ForumAvailableResponseVO> getBannedForumsByUserId(Integer targetUserId);
}
