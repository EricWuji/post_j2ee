package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.backend.entity.dto.Comment;
import org.example.backend.entity.vo.request.CommentRequestVO;
import org.example.backend.entity.vo.response.CommentResponseVO;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.service.AccountService;
import org.example.backend.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    AccountService accountService;

    @Override
    public List<CommentResponseVO> getCommentsByPostId(Integer postId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("post_id", postId)
                .eq("deleted", 0);
        List<Comment> comments = this.list(queryWrapper);
        return comments.stream()
                .map(comment -> {
                    CommentResponseVO responseVO = comment.asViewObject(CommentResponseVO.class);
                    responseVO.setUsername(accountService.getUsernameByUserId(comment.getUserId()));
                    return responseVO;
                }).toList();
    }

    @Override
    public String addComment(CommentRequestVO vo, Integer userId) {
        Comment comment = vo.asViewObject(Comment.class);
        comment.setUserId(userId);
        if (this.save(comment)) {
            return null;
        } else {
            return "Failed to add comment";
        }
    }
}
