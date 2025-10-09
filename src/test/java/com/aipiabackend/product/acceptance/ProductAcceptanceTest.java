package com.aipiabackend.product.acceptance;

import static io.restassured.RestAssured.given;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void 관리자가_유효한_상품_정보로_상품을_생성한다() {
        // 관리자 회원 생성
        Member admin = Member.ofAdmin(
            "테스트관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-8888-8888"
        );
        memberRepository.save(admin);

        // 관리자로 로그인
        String adminLoginRequestBody = """
            {
                "email": "test-admin@example.com",
                "password": "adminSecret123"
            }
            """;

        String adminAccessToken = given()
            .contentType(ContentType.JSON)
            .body(adminLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // 상품 생성
        String productRequestBody = """
            {
                "name": "맥북 프로",
                "price": 2500000,
                "stock": 10,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """;

        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 일반_회원이_상품_생성을_시도하면_403_Forbidden을_응답한다() {
        // 일반 회원 가입
        String memberRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(memberRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        // 일반 회원으로 로그인
        String memberLoginRequestBody = """
            {
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123"
            }
            """;

        String memberAccessToken = given()
            .contentType(ContentType.JSON)
            .body(memberLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // 상품 생성 시도
        String productRequestBody = """
            {
                "name": "맥북 프로",
                "price": 2500000,
                "stock": 10,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """;

        given()
            .header("Authorization", "Bearer " + memberAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void Authorization_헤더_없이_상품_생성_시_401_Unauthorized를_응답한다() {
        String productRequestBody = """
            {
                "name": "맥북 프로",
                "price": 2500000,
                "stock": 10,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @ParameterizedTest
    @NullSource
    void 필수_필드_name_누락_시_400_Bad_Request를_응답한다(String nullName) {
        // 관리자 회원 생성 및 로그인
        Member admin = Member.ofAdmin(
            "테스트관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-8888-8888"
        );
        memberRepository.save(admin);

        String adminLoginRequestBody = """
            {
                "email": "test-admin@example.com",
                "password": "adminSecret123"
            }
            """;

        String adminAccessToken = given()
            .contentType(ContentType.JSON)
            .body(adminLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // name 필드 누락
        String productRequestBody = String.format("""
            {
                "name": %s,
                "price": 2500000,
                "stock": 10,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """, nullName == null ? "null" : "\"" + nullName + "\"");

        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @NullSource
    void 필수_필드_price_누락_시_400_Bad_Request를_응답한다(Integer nullPrice) {
        // 관리자 회원 생성 및 로그인
        Member admin = Member.ofAdmin(
            "테스트관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-8888-8888"
        );
        memberRepository.save(admin);

        String adminLoginRequestBody = """
            {
                "email": "test-admin@example.com",
                "password": "adminSecret123"
            }
            """;

        String adminAccessToken = given()
            .contentType(ContentType.JSON)
            .body(adminLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // price 필드 누락
        String productRequestBody = String.format("""
            {
                "name": "맥북 프로",
                "price": %s,
                "stock": 10,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """, nullPrice == null ? "null" : nullPrice);

        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @NullSource
    void 필수_필드_stock_누락_시_400_Bad_Request를_응답한다(Integer nullStock) {
        // 관리자 회원 생성 및 로그인
        Member admin = Member.ofAdmin(
            "테스트관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-8888-8888"
        );
        memberRepository.save(admin);

        String adminLoginRequestBody = """
            {
                "email": "test-admin@example.com",
                "password": "adminSecret123"
            }
            """;

        String adminAccessToken = given()
            .contentType(ContentType.JSON)
            .body(adminLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // stock 필드 누락
        String productRequestBody = String.format("""
            {
                "name": "맥북 프로",
                "price": 2500000,
                "stock": %s,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """, nullStock == null ? "null" : nullStock);

        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @NullSource
    void 필수_필드_description_누락_시_400_Bad_Request를_응답한다(String nullDescription) {
        // 관리자 회원 생성 및 로그인
        Member admin = Member.ofAdmin(
            "테스트관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-8888-8888"
        );
        memberRepository.save(admin);

        String adminLoginRequestBody = """
            {
                "email": "test-admin@example.com",
                "password": "adminSecret123"
            }
            """;

        String adminAccessToken = given()
            .contentType(ContentType.JSON)
            .body(adminLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // description 필드 누락
        String productRequestBody = String.format("""
            {
                "name": "맥북 프로",
                "price": 2500000,
                "stock": 10,
                "description": %s
            }
            """, nullDescription == null ? "null" : "\"" + nullDescription + "\"");

        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
