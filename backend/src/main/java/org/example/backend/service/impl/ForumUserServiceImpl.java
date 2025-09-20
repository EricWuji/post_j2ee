package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.backend.entity.dto.ForumUser;
import org.example.backend.entity.vo.response.ForumAvailableResponseVO;
import org.example.backend.mapper.ForumUserMapper;
import org.example.backend.service.ForumService;
import org.example.backend.service.ForumUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumUserServiceImpl extends ServiceImpl<ForumUserMapper, ForumUser> implements ForumUserService {

    @Resource
    ForumService forumService;

    @Override
    public boolean existsByUserId(Integer userId) {
        QueryWrapper<ForumUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        long count = this.count(queryWrapper);
        return count > 0;
    }

    @Override
    public List<Integer> getForumIdsByUserId(Integer userId) {
        QueryWrapper<ForumUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.list(queryWrapper)
                .stream()
                .map(ForumUser::getForumId)
                .toList();
    }

    @Override
    public String addForumUser(Integer userId, Integer forumId) {
        QueryWrapper<ForumUser> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userId)
                .eq("forum_id", forumId);
        if (this.count(queryWrapper) > 0) {
            return "User is already a member of this forum";
        } else {
            ForumUser forumUser = new ForumUser();
            forumUser.setForumId(forumId);
            forumUser.setUserId(userId);
            if (this.save(forumUser)) {
                return null;
            } else {
                return "Failed to add user to this forum";
            }
        }

    }

    private List<ForumAvailableResponseVO> convertToResponseVO(List<Integer> forumIds) {
        return forumIds.stream()
                .map(forumId -> {
                    ForumAvailableResponseVO vo = new ForumAvailableResponseVO();
                    vo.setForumId(forumId);
                    vo.setForumName(forumService.findForumNameById(forumId));
                    return vo;
                })
                .toList();
    }

}
