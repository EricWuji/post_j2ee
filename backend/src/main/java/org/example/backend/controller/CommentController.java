package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.entity.vo.request.CommentRequestVO;
import org.example.backend.entity.vo.response.CommentResponseVO;
import org.example.backend.service.CommentService;
import org.example.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Resource
    CommentService commentService;

    @GetMapping("/details")
    public Result<List<CommentResponseVO>> getCommentDetails(@RequestParam("postId") Integer postId) {
        return Result.success(commentService.getCommentsByPostId(postId));
    }

    @PostMapping("/add")
    public Result<Void> addComment(@RequestBody CommentRequestVO vo,
                                     HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("id");
        return messageHandler(() -> commentService.addComment(vo, userId));
    }

    private Result<Void> messageHandler(Supplier<String> supplier) {
        String message = supplier.get();
        return message == null ? Result.success() : Result.failure(400, message);
    }
}
