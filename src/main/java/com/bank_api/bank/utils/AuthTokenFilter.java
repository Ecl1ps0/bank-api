package com.bank_api.bank.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.bank_api.bank.service.UserService;

import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class AuthTokenFilter implements WebFilter {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (StringUtils.isEmpty(authHeader)) {
            return chain.filter(exchange);
        }
        
        String token = authHeader.substring(7);
        if (StringUtils.isEmpty(token) || token.isBlank()) {
            log.error("TOKEN IS EMPTY");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }

        String userId = jwtGenerator.getUserIdFromToken(token, false);
        if (StringUtils.isEmpty(userId)) {
            log.warn("Token validation failed: unable to extract user ID");
            if (!exchange.getResponse().isCommitted()) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                }
            return exchange.getResponse().setComplete();
        }

        return userService.findById(userId)
            .flatMap(user -> {
                log.info("User successfully authenticated: {}", userId);
                return chain.filter(exchange);
            })
            .onErrorResume(e -> {
                log.error("Error during user lookup", e);
                if (!exchange.getResponse().isCommitted()) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                }
                return exchange.getResponse().setComplete();
            });
    }

}
