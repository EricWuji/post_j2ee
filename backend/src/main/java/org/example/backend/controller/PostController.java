package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.entity.vo.request.PostRequestVO;
import org.example.backend.service.PostService;
import org.example.backend.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private Result<Void> messageHandler(Supplier<String> supplier) {
        String message = supplier.get();
        return message == null ? Result.success() : Result.failure(400, message);
    }
}
