package com.aipiabackend.support;

import com.aipiabackend.member.repository.MemberRepository;
import com.aipiabackend.order.repository.OrderRepository;
import com.aipiabackend.payment.repository.PaymentRepository;
import com.aipiabackend.product.repository.ProductRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 테스트 데이터베이스 정리 유틸리티
 */
@RequiredArgsConstructor
@Component
public class DatabaseCleanup {

    private final DataSource dataSource;

    /**
     * 모든 테이블의 데이터를 삭제합니다.
     */
    public void cleanDatabase() {
        truncateH2("product", "member", "orders", "order_line", "payment");
    }

    public void truncateH2(String... tables) {
        execute("SET REFERENTIAL_INTEGRITY FALSE");
        try {
            for (String t : tables) {
                execute("TRUNCATE TABLE " + t);
            }
        } finally {
            execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    private void execute(String sql) {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
