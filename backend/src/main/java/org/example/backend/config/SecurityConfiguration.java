package org.example.backend.config;

import com.auth0.jwt.JWT;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.entity.dto.Account;
import org.example.backend.entity.vo.response.AuthorizeVO;
import org.example.backend.filter.JsonLoginFilter;
import org.example.backend.filter.JwtAuthorizeFilter;
import org.example.backend.service.AccountService;
import org.example.backend.utils.JwtUtils;
import org.example.backend.utils.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {
    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    AccountService accountService;

    @Resource
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public JsonLoginFilter jsonLoginFilter() throws Exception {
        JsonLoginFilter filter = new JsonLoginFilter();
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        filter.setFilterProcessesUrl("/api/auth/login");
        filter.setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
        filter.setAuthenticationFailureHandler(this::onUnauthorized);
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> {
                    conf.requestMatchers("/api/auth/**").permitAll();
                    conf.anyRequest().authenticated();
                })
                .logout(conf -> {
                    conf.logoutUrl("/api/auth/logout");
                    conf.logoutSuccessHandler(this::onLogoutSuccess);
                })
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDenied))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf ->
                        conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(jsonLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    public void onAccessDenied(HttpServletRequest request,
                               HttpServletResponse response,
                               AccessDeniedException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(Result.forbidden(exception.getMessage()).asJsonString());
    }

    public void onUnauthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.unAuthorized(exception.getMessage()).asJsonString());
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Account account = accountService.findUserByUsername(user.getUsername());
        String token = jwtUtils.createJwt(user, account.getId(), account.getUsername());
        AuthorizeVO vo = account.asViewObject(AuthorizeVO.class, v -> {
            v.setExpiration(JWT.decode(token).getExpiresAt());
            v.setToken(token);
        });
        response.getWriter().write(Result.success(vo).asJsonString());
    }

    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (jwtUtils.invalidToken(authorization)) {
            printWriter.write(Result.success().asJsonString());
        } else {
            printWriter.write(Result.failure(400, "failed").asJsonString());
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
