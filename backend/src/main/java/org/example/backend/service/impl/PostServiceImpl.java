package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.backend.entity.dto.Post;
import org.example.backend.entity.vo.request.PostRequestVO;
import org.example.backend.entity.vo.response.PostResponseVO;
import org.example.backend.mapper.PostMapper;
import org.example.backend.service.AccountForumBanService;
import org.example.backend.service.AccountService;
import org.example.backend.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    AccountForumBanService accountForumBanService;

    @Resource
    AccountService accountService;

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
        List<PostResponseVO> toppedPostVOs = toppedPosts.stream()
                .map(post -> {
                    PostResponseVO viewObject = post.asViewObject(PostResponseVO.class);
                    viewObject.setUsername(accountService.getUsernameByUserId(post.getUserId()));
                    return viewObject;
                })
                .toList();
        queryWrapper.clear();
        queryWrapper
                .eq("forum_id", forumId)
                .eq("deleted", 0)
                .eq("topped", 0);
        List<Post> normalPosts = this.list(queryWrapper);
        List<PostResponseVO> normalPostVOs = normalPosts.stream()
                .map(post -> {
                    PostResponseVO viewObject = post.asViewObject(PostResponseVO.class);
                    viewObject.setUsername(accountService.getUsernameByUserId(post.getUserId()));
                    return viewObject;
                })
                .toList();
        return Stream.concat(
                toppedPostVOs.stream(),
                normalPostVOs.stream()
        ).toList();
    }
}
