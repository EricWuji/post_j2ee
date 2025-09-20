package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.Post;
import org.example.backend.entity.vo.request.PostRequestVO;
import org.example.backend.entity.vo.response.PostResponseVO;

import java.util.List;

public interface PostService extends IService<Post> {
    String addPost(PostRequestVO vo, Integer userId);
    List<PostResponseVO> getAllPostsByForumId(Integer forumId);
    List<PostResponseVO> getAllPostsByUserId(Integer userId);
    String deletePostById(Integer postId);
    String topPostById(Integer postId);
}
