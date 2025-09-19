package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.service.AccountService;
import org.example.backend.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @GetMapping("/ask-code")
    public Result<Void> askCode(@RequestParam String email,
                                @RequestParam String type,
                                HttpServletRequest request) {
        String message = accountService.registerEmailVerifyCode(type, email, request.getRemoteAddr());
        return message == null ? Result.success() : Result.failure(400, message);
    }
}
