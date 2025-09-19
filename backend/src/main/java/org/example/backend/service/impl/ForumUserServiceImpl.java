package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.entity.dto.ForumUser;
import org.example.backend.mapper.ForumUserMapper;
import org.example.backend.service.ForumUserService;
import org.springframework.stereotype.Service;

@Service
public class ForumUserServiceImpl extends ServiceImpl<ForumUserMapper, ForumUser> implements ForumUserService {
    @Override
    public boolean existsByUserId(Integer userId) {
        QueryWrapper<ForumUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        long count = this.count(queryWrapper);
        return count > 0;
    }
}
