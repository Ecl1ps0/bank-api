package com.bank_api.bank.utils;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bank_api.bank.models.User;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class JwtGenerator {

    private final String issuer;

    private final long accessTokenExp;
    private final long refreshTokenExp;

    private final Algorithm accessTokenAlgorithm;
    private final Algorithm refreshTokenAlgorithm;

    private final JWTVerifier accessTokenVerifier;
    private final JWTVerifier refreshTokenVerifier;

    public JwtGenerator(@Value("${token.accesTokenSecret}") String accessTokenSecret, @Value("${token.refreshTokenSecret}") String refreshTokenSecret, 
                    @Value("${token.issuer}") String issuer, @Value("${token.accessTokenExpirationMinutes}") int accessTokenExpirationMinutes, 
                    @Value("${token.refreshTokenExpirationDays}") int refreshTokenExpirationDays)
    {
        
        this.issuer = issuer;

        this.accessTokenExp = accessTokenExpirationMinutes * 60 * 1000;
        this.refreshTokenExp = refreshTokenExpirationDays * 24 * 60 * 60 * 1000;

        accessTokenAlgorithm = Algorithm.HMAC256(accessTokenSecret);
        refreshTokenAlgorithm = Algorithm.HMAC256(refreshTokenSecret);

        accessTokenVerifier = JWT.require(accessTokenAlgorithm)
            .withIssuer(issuer)
            .build();

        refreshTokenVerifier = JWT.require(refreshTokenAlgorithm)
            .withIssuer(issuer)
            .build();

    }

    public String generateAccessToken(User user) {
        return JWT.create()
            .withIssuer(this.issuer)
            .withSubject(user.getId().toString())
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(new Date().getTime() + this.accessTokenExp))
            .sign(this.accessTokenAlgorithm);
    }

    public String generateRefreshToken(User user) {
        return JWT.create()
            .withIssuer(this.issuer)
            .withSubject(user.getId().toString())
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(new Date().getTime() + this.refreshTokenExp))
            .sign(this.refreshTokenAlgorithm);
    }


    private Optional<DecodedJWT> decodeToken(String token, boolean isRefresh) {
        try {
            if (isRefresh) {
                return Optional.of(this.refreshTokenVerifier.verify(token));
            }
            return Optional.of(this.accessTokenVerifier.verify(token));
        } catch (JWTVerificationException e) {
            log.error("INVALID TOKEN", e.toString());
        }

        return Optional.empty();

    }

    public boolean validateToken(String token, boolean isRefresh) {
        return decodeToken(token, isRefresh).isPresent();
    }

    public String getUserIdFromToken(String token, boolean isRefresh) {
        return decodeToken(token, isRefresh).get().getSubject();
    }
}