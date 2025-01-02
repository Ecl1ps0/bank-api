package com.bank_api.bank.utils;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bank_api.bank.models.User;
import com.bank_api.bank.service.UserService;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authHeader)) {
            writeErrorResponse(response, "Authorization header is empty");
        };

        String token = authHeader.substring(7);
        if (StringUtils.isEmpty(token) || token.isBlank()) {
            writeErrorResponse(response, "Token is empty");
        }

        if (!jwtGenerator.validateToken(token, false)) {
            writeErrorResponse(response, "Invalid token");
        }

        String userId = jwtGenerator.getUserIdFromToken(token, false);
        if (StringUtils.isEmpty(userId)) {
            writeErrorResponse(response, "Fail to get user id from token");
        }

        Optional<User> user = this.userService.findById(userId);
        if (!user.isPresent()) {
            writeErrorResponse(response, "User does not exist");
        }

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.get(), null, user.get().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            writeErrorResponse(response, e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }

    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException, ServletException {
        log.error("Authentication error: {}", message);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

}
