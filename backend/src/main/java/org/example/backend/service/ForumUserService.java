package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.ForumUser;

public interface ForumUserService extends IService<ForumUser> {
    boolean existsByUserId(Integer userId);
}
