package com.aipiabackend.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines = new ArrayList<>();

    @Column(nullable = false, precision = 19, scale = 2)
    private Long amount;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    public static Order of(Long memberId, List<OrderLine> orderLines, Long amount) {
        Order order = new Order(null, memberId, new ArrayList<>(), amount, LocalDateTime.now());
        orderLines.forEach(order::addOrderLine);
        return order;
    }

    private void addOrderLine(OrderLine orderLine) {
        this.orderLines.add(orderLine);
        orderLine.setOrder(this);
    }
}
