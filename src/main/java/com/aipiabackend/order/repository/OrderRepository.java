package com.aipiabackend.order.repository;

import com.aipiabackend.order.model.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderLines WHERE o.id = :orderId")
    Optional<Order> findByIdWithOrderLines(@Param("orderId") Long orderId);
}
