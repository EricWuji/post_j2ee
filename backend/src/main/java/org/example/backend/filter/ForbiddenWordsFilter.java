package org.example.backend.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.service.ForbiddenWordService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.List;

@Component
@Order(2)
public class ForbiddenWordsFilter extends OncePerRequestFilter {

    @Resource
    ForbiddenWordService forbiddenWordService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if ((uri.startsWith("/api/post/add") || uri.startsWith("/api/comment/add")) && "POST".equals(request.getMethod())) {
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            String body = new String(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());

            List<String> forbiddenWords = forbiddenWordService.getAllForbiddenWords();
            if (containsForbiddenWord(body, forbiddenWords)) {
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":400,\"message\":\"Content contains forbidden words.\",\"data\":null}");
                return ;
            }
            filterChain.doFilter(requestWrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean containsForbiddenWord(String body, List<String> forbiddenWords) {
        if (body == null || body.isEmpty()) return false;
        for (String word : forbiddenWords) {
            if (body.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
