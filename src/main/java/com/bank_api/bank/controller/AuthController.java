package com.bank_api.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank_api.bank.dto.LoginDTO;
import com.bank_api.bank.dto.RegisterDTO;
import com.bank_api.bank.dto.TokenDTO;
import com.bank_api.bank.dto.UpdateTokenDTO;
import com.bank_api.bank.service.UserService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReactiveAuthenticationManager manager;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@Valid @RequestBody RegisterDTO entity) {
        return this.userService.saveUser(entity)
            .map(userId -> ResponseEntity.status(HttpStatus.CREATED).body(String.format("User created with id: %s", userId)))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenDTO>> postMethodName(@Valid @RequestBody LoginDTO entity) {
        return this.userService.loginUser(entity, this.manager)
            .map(tokenDTO -> ResponseEntity.ok().body(tokenDTO));
    }

    @PostMapping("/get-token")
    public Mono<ResponseEntity<TokenDTO>> postMethodName(@RequestBody UpdateTokenDTO updateToken) {
        return this.userService.getNewAccessToken(updateToken.getRefreshToken())
            .map(token -> ResponseEntity.ok().body(token));
    }
    
}
