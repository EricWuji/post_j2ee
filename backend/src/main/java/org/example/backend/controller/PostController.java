package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.entity.vo.request.PostRequestVO;
import org.example.backend.entity.vo.response.PostResponseVO;
import org.example.backend.service.PostService;
import org.example.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Resource
    PostService postService;

    @PostMapping("/add")
    public Result<Void> addPost(@RequestBody PostRequestVO vo,
                                  HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("id");
        System.out.println(userId);
        return messageHandler(() -> postService.addPost(vo, userId));
    }

    @GetMapping("/details")
    public Result<PostResponseVO> postDetails(@RequestParam("post_id") Integer postId) {
        return Result.success(postService.getById(postId).asViewObject(PostResponseVO.class));
    }

    private Result<Void> messageHandler(Supplier<String> supplier) {
        String message = supplier.get();
        return message == null ? Result.success() : Result.failure(400, message);
    }

    @GetMapping("/lists")
    public Result<List<PostResponseVO>> listPosts(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("id");
        return Result.success(postService.getAllPostsByUserId(userId));
    }

    @DeleteMapping("/delete")
    public Result<Void> deletePostById(@RequestParam("post_id") Integer postId) {
        return messageHandler(() -> postService.deletePostById(postId));
    }

    @GetMapping("/top")
    public Result<Void> topPost(@RequestParam("post_id") Integer postId) {
        return messageHandler(() -> postService.topPostById(postId));
    }
}
