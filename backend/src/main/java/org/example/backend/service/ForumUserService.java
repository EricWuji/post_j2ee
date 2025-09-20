package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.ForumUser;

import java.util.List;

public interface ForumUserService extends IService<ForumUser> {
    boolean existsByUserId(Integer userId);
    List<Integer> getForumIdsByUserId(Integer userId);
    String addForumUser(Integer userId, Integer forumId);
}
