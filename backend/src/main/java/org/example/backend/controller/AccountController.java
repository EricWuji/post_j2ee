package org.example.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.entity.vo.request.ForumBanRequestVO;
import org.example.backend.entity.vo.response.AccountResponseVO;
import org.example.backend.entity.vo.response.ForumAvailableResponseVO;
import org.example.backend.service.AccountForumBanService;
import org.example.backend.service.AccountService;
import org.example.backend.service.ForumUserService;
import org.example.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    AccountService accountService;

    @Resource
    AccountForumBanService accountForumBanService;

    @Resource
    ForumUserService forumUserService;

    @GetMapping("/role")
    public Result<String> role(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("id");
        String userRole = accountService.getUserRoleByUserId(id);
        return Result.success(userRole);
    }

    @GetMapping("/find")
    public Result<List<AccountResponseVO>> find(@RequestParam("username") String username) {
        List<AccountResponseVO> accountResponseVOS = accountService.getUsersByUsername(username);
        return Result.success(accountResponseVOS);
    }

    @GetMapping("/available")
    public Result<List<ForumAvailableResponseVO>> available(HttpServletRequest request,
                                                            @RequestParam("user_id") Integer targetUserId) {
        Integer id = (Integer) request.getAttribute("id");
        List<ForumAvailableResponseVO> forumAvailableResponseVOS = accountService.getAvailableForumsByUserId(id, targetUserId);
        return Result.success(forumAvailableResponseVOS);
    }

    @PostMapping("/ban")
    public Result<String> ban(@RequestBody ForumBanRequestVO vo) {
        for (Integer forumId : vo.getForumId()) {
            String message = accountForumBanService.addAccountIdAndForumId(vo.getUserId(), forumId);
            if (message != null) {
                return Result.failure(400, message);
            }
        }
        return Result.success();
    }

    @PostMapping("/unban")
    public Result<String> unban(@RequestBody ForumBanRequestVO vo) {
        for (Integer forumId : vo.getForumId()) {
            String message = accountForumBanService.removeAccountIdAndForumId(vo.getUserId(), forumId);
            if (message != null) {
                return Result.failure(400, message);
            }
        }
        return Result.success();
    }

    @GetMapping("/ban_forums")
    public Result<List<ForumAvailableResponseVO>> banForums(@RequestParam("user_id") Integer targetUserId) {
        return Result.success(accountService.getBannedForumsByUserId(targetUserId));
    }

    @PostMapping("/set_moderator")
    public Result<String> setModerator(@RequestBody ForumBanRequestVO vo) {
        Integer userId = vo.getUserId();
        for (Integer forumId : vo.getForumId()) {
            String message = forumUserService.addForumUser(userId, forumId);
            if (message != null) {
                return Result.failure(400, message);
            }
        }
        return Result.success();
    }

    @GetMapping("/available-moderator")
    public Result<List<ForumAvailableResponseVO>> askRole(@RequestParam("user_id") Integer targetUserId,
                                  HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("id");
        List<ForumAvailableResponseVO> vos = accountService.getAvailableForumsByUserId(userId, targetUserId);
        return Result.success(vos);
    }
}
