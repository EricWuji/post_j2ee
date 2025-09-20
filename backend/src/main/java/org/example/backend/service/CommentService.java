package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.Comment;
import org.example.backend.entity.vo.request.CommentRequestVO;
import org.example.backend.entity.vo.response.CommentResponseVO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    List<CommentResponseVO> getCommentsByPostId(Integer postId);
    String addComment(CommentRequestVO vo, Integer userId);
}
