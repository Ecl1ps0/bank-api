package com.bank_api.bank.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank_api.bank.dto.LoginDTO;
import com.bank_api.bank.dto.RegisterDTO;
import com.bank_api.bank.dto.TokenDTO;
import com.bank_api.bank.models.User;
import com.bank_api.bank.models.enums.UserRole;
import com.bank_api.bank.repository.UserRepository;
import com.bank_api.bank.utils.JwtGenerator;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Value("${token.refreshTokenExpirationDays}")
    private int expDays;

    public String saveUser(RegisterDTO registerDTO) {

        Optional<User> candidate = this.userRepository.findByPhoneNumber(registerDTO.getPhoneNumber());
        if (candidate.isPresent()) {
            throw new IllegalArgumentException("User with such phone number already exists");
        }

        User user = new User();
        user.setIin(registerDTO.getIin());
        user.setName(registerDTO.getName());
        user.setSurname(registerDTO.getSurname());
        user.setPatronymic(registerDTO.getPatronymic());
        user.setEmail(registerDTO.getEmail());
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        user.setDateOfBirth(registerDTO.getDateOfBirth());
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        User newUser = this.userRepository.save(user);
        return newUser.getId().toString();

    }

    public TokenDTO loginUser(LoginDTO loginDTO, AuthenticationManager manager) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getPhoneNumber(), loginDTO.getPassword());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(manager.authenticate(authenticationToken));

        User user = (User) context.getAuthentication().getPrincipal();

        String accessToken = jwtGenerator.generateAccessToken(user);
        String refreshToken = jwtGenerator.generateRefreshToken(user);

        redisTemplate.opsForValue()
                .set(user.getId().toString(), refreshToken, Duration.ofDays(expDays));

        return new TokenDTO(user.getId().toString(), accessToken, refreshToken);
    }

    public TokenDTO getNewAccessToken(String refreshToken) {
        if (!jwtGenerator.validateToken(refreshToken, true)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String userId = jwtGenerator.getUserIdFromToken(refreshToken, true);
        String storedToken = redisTemplate.opsForValue().get(userId);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Token is expired or invalid");
        }

        Optional<User> userOpt = userRepository.findById(UUID.fromString(userId));
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        String newAccessToken = jwtGenerator.generateAccessToken(user);

        return new TokenDTO(userId, newAccessToken, refreshToken);
    }
}
