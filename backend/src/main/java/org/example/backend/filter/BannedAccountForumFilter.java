package org.example.backend.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.service.AccountForumBanService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class BannedAccountForumFilter extends OncePerRequestFilter {

    @Resource
    AccountForumBanService accountForumBanService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/forum")) {
            if (uri.equals("/api/forum/list")) {
                filterChain.doFilter(request, response);
            } else {
                Integer userId = (Integer) request.getAttribute("id");
                Integer forumId = request.getParameter("forum_id") != null ? Integer.parseInt(request.getParameter("forum_id")) : null;
                if (accountForumBanService.existsAccountIdAndForumId(userId, forumId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"You are banned from this forum.\",\"data\":null}");
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        } else {
            filterChain.doFilter(request,response);
        }
    }
}
