package com.aipiabackend.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_line")
@Entity
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // OrderLine의 경우 주문 시점의 상품에 대한 스냅샷이기 때문에 간접참조를 유지한다
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_quantity", nullable = false)
    private Integer productQuantity;

    @Column(name = "product_price", nullable = false, precision = 19, scale = 2)
    private Long productPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private Long amount;

    public static OrderLine of(Long productId, Integer productQuantity, Long productPrice, Long amount) {
        return new OrderLine(null, null, productId, productQuantity, productPrice, amount);
    }

    void setOrder(Order order) {
        this.order = order;
    }
}
