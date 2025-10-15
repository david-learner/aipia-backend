package com.aipiabackend.support.config;

import static com.aipiabackend.support.model.ErrorCodeMessage.*;

import com.aipiabackend.auth.filter.JwtAuthenticationFilter;
import com.aipiabackend.support.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 임시 보안 토큰 비활성화
            .csrf(AbstractHttpConfigurer::disable)

            // 서버에서 세션을 생성하지 않는다
            .sessionManagement(
                sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // security form login 비활성화
            .formLogin(AbstractHttpConfigurer::disable)

            // security가 등록하는 HTTP Basic 인증 필터 비활성화
            .httpBasic(AbstractHttpConfigurer::disable)

            // logout 처리시 no_content로 응답하도록 설정
            .logout(configurer ->
                configurer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))

            .authorizeHttpRequests(authz -> authz
                // 애플리케이션 또는 인프라 경로
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                // 인증되지 않은 사용자에게 허용된 경로
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/members").permitAll()
                // 인증된 사용자에게 허용된 경로
                .requestMatchers(HttpMethod.GET, "/api/members/me").authenticated()
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/payments/**").authenticated()
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
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}