package com.bank_api.bank.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank_api.bank.dto.LoginDTO;
import com.bank_api.bank.dto.RegisterDTO;
import com.bank_api.bank.dto.TokenDTO;
import com.bank_api.bank.models.User;
import com.bank_api.bank.models.enums.UserRole;
import com.bank_api.bank.repository.UserRepository;
import com.bank_api.bank.utils.JwtGenerator;

import io.micrometer.common.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@Primary
public class UserService implements ReactiveUserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("reactiveRedisTemplate")
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Value("${token.refreshTokenExpirationDays}")
    private int expDays;

    public Mono<Object> saveUser(RegisterDTO registerDTO) {
        return this.userRepository.findUserByPhoneNumber(registerDTO.getPhoneNumber())
            .flatMap(candidate -> Mono.error(new IllegalArgumentException("User with such phone number is already exist")))
            .switchIfEmpty(Mono.defer(() -> {
                User user = new User();

                user.setId(ObjectId.get());
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

                return this.userRepository.save(user).thenReturn(user.getId().toHexString());
            }));
    }

    public Mono<TokenDTO> loginUser(LoginDTO loginDTO, ReactiveAuthenticationManager manager) {
        return manager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getPhoneNumber(), loginDTO.getPassword()))
            .flatMap(authentication -> {
                User user = (User) authentication.getPrincipal();

                String accessToken = this.jwtGenerator.generateAccessToken(user);

                String refreshToken = this.jwtGenerator.generateRefreshToken(user);

                TokenDTO token = new TokenDTO(user.getId().toHexString(), accessToken, refreshToken);

                return this.redisTemplate.opsForValue()
                    .set(user.getId().toHexString(), refreshToken, Duration.ofDays(expDays))
                    .thenReturn(token);
            });
    }

    public Mono<TokenDTO> getNewAccessToken(String refreshToken) {
        if (!this.jwtGenerator.validateToken(refreshToken, true)) {
            return Mono.error(new IllegalArgumentException("Invalid refresh token"));
        }

        String userId = this.jwtGenerator.getUserIdFromToken(refreshToken, true);

        return this.redisTemplate.opsForValue().get(userId)
            .flatMap(storedToken -> {
                if (!storedToken.equals(refreshToken) || StringUtils.isEmpty(storedToken)) {
                    return Mono.error(new IllegalArgumentException("Token is expired"));
                }

                return this.findById(userId)
                    .flatMap(user -> {
                            String newAccessToken = this.jwtGenerator.generateAccessToken(user);
                            return Mono.just(new TokenDTO(userId, newAccessToken, refreshToken));
                        })
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
            });
    }

    public Mono<User> loadUserByPhoneNumber(String phoneNumber) {
        return this.userRepository.findUserByPhoneNumber(phoneNumber)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User is not found")));
    }

    public Mono<User> findById(String id) {
        return this.userRepository.findById(new ObjectId(id))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User is not found")));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Mono findByUsername(String username) {
        return this.userRepository.findUserByPhoneNumber(username)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User is not found")));
    }

}
