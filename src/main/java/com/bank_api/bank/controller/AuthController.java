package com.bank_api.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank_api.bank.dto.LoginDTO;
import com.bank_api.bank.dto.RegisterDTO;
import com.bank_api.bank.dto.TokenDTO;
import com.bank_api.bank.dto.UpdateTokenDTO;
import com.bank_api.bank.service.AuthService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager manager;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO entity) {
        String userId = this.authService.saveUser(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO entity) {
        TokenDTO tokenDTO = this.authService.loginUser(entity, this.manager);
        return ResponseEntity.ok().body(tokenDTO);
    }

    @PostMapping("/token")
    public ResponseEntity<TokenDTO> getNewToken(@RequestBody UpdateTokenDTO updateToken) {
        TokenDTO token = this.authService.getNewAccessToken(updateToken.getRefreshToken());
        return ResponseEntity.ok().body(token);

    }
    
}
