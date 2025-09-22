package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.backend.entity.vo.request.EmailRegisterVO;
import org.example.backend.entity.vo.request.EmailResetVO;
import org.example.backend.service.AccountService;
import org.example.backend.utils.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @GetMapping("/ask-code")
    public Result<Void> askCode(@RequestParam @Email String email,
                                @RequestParam @Pattern(regexp = "(register|resetPassword)") String type,
                                HttpServletRequest request) {
        return messageHandler(() -> accountService.emailVerifyCode(type, email, request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid EmailRegisterVO vo) {
        return messageHandler(() -> accountService.registerEmailAccount(vo));
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid EmailResetVO vo) {
        return messageHandler(() -> accountService.resetPassword(vo));
    }

    private Result<Void> messageHandler(Supplier<String> supplier) {
        String message = supplier.get();
        return message == null ? Result.success() : Result.failure(400, message);
    }
}
