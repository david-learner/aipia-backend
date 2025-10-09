package com.aipiabackend.support.config;

import static com.aipiabackend.support.model.ErrorCodeMessage.*;

import com.aipiabackend.auth.filter.JwtAuthenticationFilter;
import com.aipiabackend.support.dto.ErrorResponse;
import com.aipiabackend.support.model.ErrorCodeMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // 애플리케이션 또는 인프라 경로
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error/**").permitAll()
                // 서비스를 위한 허용된 경로
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/members").permitAll()
                // 회원에게 허용된 경로
                .requestMatchers(HttpMethod.GET, "/api/members/me").authenticated()
                // 관리자에게만 허용된 경로
                .requestMatchers("/api/members/**").hasRole("ADMIN")
                .requestMatchers("/api/products/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                // 인증되지 않은 사용자가 요청시 에러 처리
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.of(UNAUTHORIZED_USER)));
                })
                // 권한이 없는 사용자가 요청시 에러 처리
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.of(ACCESS_DENIED)));
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}