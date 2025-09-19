package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.service.AccountService;
import org.example.backend.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    AccountService accountService;

    @GetMapping("/role")
    public Result<String> role(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("id");
        String userRole = accountService.getUserRoleByUserId(id);
        return Result.success(userRole);
    }
}
