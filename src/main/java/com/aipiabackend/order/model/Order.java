package com.aipiabackend.order.model;

import static com.aipiabackend.support.model.ErrorCodeMessage.ORDER_CAN_BE_SUCCEEDED_FROM_PENDING;

import com.aipiabackend.support.model.exception.AipiaDomainException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines = new ArrayList<>();

    @Column(nullable = false)
    private Long amount;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    public static Order of(Long memberId, List<OrderLine> orderLines, Long amount) {
        Order order = new Order(null, memberId, OrderStatus.PENDING, new ArrayList<>(), amount, LocalDateTime.now());
        orderLines.forEach(order::addOrderLine);
        return order;
    }

    private void addOrderLine(OrderLine orderLine) {
        this.orderLines.add(orderLine);
        orderLine.setOrder(this);
    }

    public void completePayment() {
        if (!isPending()) {
            throw new AipiaDomainException(ORDER_CAN_BE_SUCCEEDED_FROM_PENDING);
        }

        this.status = OrderStatus.SUCCEEDED;
    }

    public boolean isPending() {
        return this.status == OrderStatus.PENDING;
    }

    public boolean isOrderedBy(Long memberId) {
        return this.memberId.equals(memberId);
    }
}
