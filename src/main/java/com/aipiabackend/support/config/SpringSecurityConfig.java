package com.aipiabackend.support.config;

import static com.aipiabackend.support.model.ErrorCodeMessage.*;

import com.aipiabackend.auth.filter.JwtAuthenticationFilter;
import com.aipiabackend.support.dto.ErrorResponse;
import com.aipiabackend.support.model.ErrorCodeMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/members").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error/**").permitAll()
                .anyRequest().authenticated()
            )
            // 인증되지 않은 사용자가 요청시 에러 처리
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.of(UNAUTHORIZED_USER)));
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}