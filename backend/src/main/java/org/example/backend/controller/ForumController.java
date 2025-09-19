package org.example.backend.controller;

import jakarta.annotation.Resource;
import org.example.backend.entity.dto.Forum;
import org.example.backend.entity.vo.response.ForumDetailsVO;
import org.example.backend.entity.vo.response.PostResponseVO;
import org.example.backend.service.ForumService;
import org.example.backend.service.PostService;
import org.example.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    ForumService forumService;

    @Resource
    PostService postService;

    @GetMapping("/list")
    public Result<List<Forum>> listForum() {
        return Result.success(forumService.list());
    }

    @GetMapping("/details")
    public Result<ForumDetailsVO> forumDetails(@RequestParam("forum_id") Integer forumId) {
        Forum forum = forumService.findForumById(forumId);
        ForumDetailsVO vo = forum.asViewObject(ForumDetailsVO.class);
        return Result.success(vo);
    }

    @GetMapping("/list_posts")
    public Result<List<PostResponseVO>> listPosts(@RequestParam("forum_id") Integer forumId) {
        return Result.success(postService.getAllPostsByForumId(forumId));
    }
}
