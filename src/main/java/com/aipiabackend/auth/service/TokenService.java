package com.aipiabackend.auth.service;

import com.aipiabackend.auth.config.JwtProperties;
import com.aipiabackend.member.model.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Member member) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.expiration());

        return Jwts.builder()
            .subject(String.valueOf(member.getId()))
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }
}