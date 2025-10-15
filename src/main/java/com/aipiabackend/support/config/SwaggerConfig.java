package com.aipiabackend.support.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@OpenAPIDefinition(
    info = @Info(
        title = "AIPIA 백엔드 API 명세",
        version = "v1",
        description = """
                - 테스트 가능한 로그인 데이터
                    - 일반 회원
                      - 이메일: mspark@example.com
                      - 비밀번호: password123
                    - 관리자 회원
                      - 이메일: admin@example.com
                      - 비밀번호: adminPassword123
            """)
)
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER,
    paramName = "Authorization",
    description = "Access Token ex) Bearer ${token}"
)
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
            .components(new Components())
            // 모든 엔드포인트에 자동으로 토큰 헤더 추가하도록 설정
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
