package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.entity.dto.Forum;
import org.example.backend.mapper.ForumMapper;
import org.example.backend.service.ForumService;
import org.springframework.stereotype.Service;

@Service
public class ForumServiceImpl extends ServiceImpl<ForumMapper, Forum> implements ForumService {
    @Override
    public Forum findForumById(int id) {
        QueryWrapper<Forum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("forum_id", id);
        return this.getOne(queryWrapper);
    }

    @Override
    public String findForumNameById(int id) {
        Forum forum = this.findForumById(id);
        if (forum != null) {
            return forum.getForumName();
        } else {
            return null;
        }
    }
}
