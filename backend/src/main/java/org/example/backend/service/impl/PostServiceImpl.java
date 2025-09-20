package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.backend.entity.dto.Post;
import org.example.backend.entity.vo.request.PostRequestVO;
import org.example.backend.entity.vo.response.PostResponseVO;
import org.example.backend.mapper.PostMapper;
import org.example.backend.service.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    AccountForumBanService accountForumBanService;

    @Resource
    AccountService accountService;

    @Resource
    ForumUserService forumUserService;

    @Resource
    ForumService forumService;

    @Override
    public String addPost(PostRequestVO vo, Integer userId) {
        if (accountForumBanService.existsAccountIdAndForumId(userId, vo.getForumId())) {
            return "User is banned from this forum";
        }
        Post post = vo.asViewObject(Post.class);
        post.setUserId(userId);
        if (this.save(post)) {
            return null;
        } else {
            return "Failed to add post";
        }
    }

    @Override
    public List<PostResponseVO> getAllPostsByForumId(Integer forumId) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("forum_id", forumId)
                .eq("deleted", 0)
                .eq("topped", 1);
        List<Post> toppedPosts = this.list(queryWrapper);
        List<PostResponseVO> toppedPostVOs = convertPostsToVOs(toppedPosts);
        queryWrapper.clear();
        queryWrapper
                .eq("forum_id", forumId)
                .eq("deleted", 0)
                .eq("topped", 0);
        List<Post> normalPosts = this.list(queryWrapper);
        List<PostResponseVO> normalPostVOs = convertPostsToVOs(normalPosts);
        return Stream.concat(
                toppedPostVOs.stream(),
                normalPostVOs.stream()
        ).toList();
    }

    @Override
    public List<PostResponseVO> getAllPostsByUserId(Integer userId) {
        String role = accountService.getUserRoleByUserId(userId);
        if ("admin".equals(role)) {
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .eq("user_id", userId)
                    .eq("deleted", 0);
            return convertPostsToVOs(this.list(queryWrapper));
        } else {
            List<Integer> forumIds = forumUserService.getForumIdsByUserId(userId);
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .eq("user_id", userId)
                    .eq("deleted", 0);
            Set<PostResponseVO> voSet = new LinkedHashSet<>(this.convertPostsToVOs(this.list(queryWrapper)));
            for (Integer forumId : forumIds) {
                queryWrapper.clear();
                queryWrapper
                        .eq("forum_id", forumId);
                List<PostResponseVO> additionalVOs = this.convertPostsToVOs(this.list(queryWrapper));
                voSet.addAll(additionalVOs);
            }
            return new ArrayList<>(voSet);
        }
    }

    private List<PostResponseVO> convertPostsToVOs(List<Post> posts) {
        return posts.stream()
                .map(post -> {
                    PostResponseVO vo = post.asViewObject(PostResponseVO.class);
                    vo.setUsername(accountService.getUsernameByUserId(post.getUserId()));
                    vo.setForumName(forumService.findForumNameById(post.getForumId()));
                    return vo;
                })
                .toList();
    }

    @Override
    public String deletePostById(Integer postId) {
        Post post = this.getById(postId);
        if (post == null) return "Post not found";
        post.setDeleted(true);
        if (this.updateById(post)) {
            return null;
        } else {
            return "Failed to update post";
        }
    }

    @Override
    public String topPostById(Integer postId) {
        Post post = this.getById(postId);
        if (post == null) return "Post not found";
        post.setTopped(true);
        if (this.save(post)) {
            return null;
        } else {
            return "Failed to update post";
        }
    }
}
