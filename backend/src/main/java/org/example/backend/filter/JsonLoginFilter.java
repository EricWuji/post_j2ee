package org.example.backend.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.entity.dto.LoginRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public class JsonLoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String contentType = request.getContentType();
        if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            // 处理JSON数据
            try {
                InputStream is = request.getInputStream();
                LoginRequest loginRequest = new ObjectMapper().readValue(is, LoginRequest.class);

                String username = loginRequest.getUsername();
                String password = loginRequest.getPassword();

                if (!StringUtils.hasText(username)) {
                    username = "";
                }
                if (!StringUtils.hasText(password)) {
                    password = "";
                }

                username = username.trim();

                UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);

                setDetails(request, authRequest);

                return this.getAuthenticationManager().authenticate(authRequest);

            } catch (IOException e) {
                throw new AuthenticationServiceException("Invalid JSON format", e);
            }
        } else {
            // 处理表单数据（保持原有功能）
            return super.attemptAuthentication(request, response);
        }
    }
}
