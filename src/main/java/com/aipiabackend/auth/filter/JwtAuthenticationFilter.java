package com.aipiabackend.auth.filter;

import com.aipiabackend.auth.model.MemberPrincipal;
import com.aipiabackend.auth.service.AuthService;
import com.aipiabackend.auth.service.TokenService;
import com.aipiabackend.member.model.MemberGrade;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 필터
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            Optional<String> maybeToken = extractTokenFromRequest(request);

            if (maybeToken.isPresent()) {
                String token = maybeToken.get();
                MemberPrincipal principal = authService.createMemberPrincipal(token);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("JWT 인증 실패", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 인증 헤더에서 JWT를 추출한다
     *
     * @return JWT 문자열, 없으면 null
     */
    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return Optional.of(bearerToken.substring(BEARER_PREFIX.length()));
        }

        return Optional.empty();
    }
}
