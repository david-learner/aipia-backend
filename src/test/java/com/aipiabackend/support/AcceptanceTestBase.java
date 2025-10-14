package com.aipiabackend.support;

import com.aipiabackend.member.repository.MemberRepository;
import com.aipiabackend.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * 인수 테스트를 위한 추상 베이스 클래스
 *
 * <p>모든 인수 테스트에서 공통으로 사용되는 설정과 필드를 제공합니다.
 * RestAssured 초기화, Spring Boot 테스트 환경 설정, 공통 의존성 주입을 처리합니다.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AcceptanceTestBase {

    @LocalServerPort
    protected int port;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    /**
     * 각 테스트 실행 전 RestAssured 설정을 초기화합니다.
     *
     * <p>테스트 환경에서 사용할 포트와 기본 URI를 RestAssured에 설정합니다.</p>
     */
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterEach
    void tearDown() {
        databaseCleanup.cleanDatabase();
    }
}
