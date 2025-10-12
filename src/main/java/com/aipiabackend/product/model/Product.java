package com.aipiabackend.product.model;

import static com.aipiabackend.support.model.ErrorCodeMessage.*;

import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.exception.AipiaException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean deleted;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    public static Product of(String name, Long price, Integer stock, String description) {
        return new Product(null, name, price, stock, description, false, LocalDateTime.now());
    }

    public void decreaseStock(Integer count) {
        if (this.stock < count) {
            throw new AipiaException(NOT_ENOUGH_STOCK);
        }
        this.stock = this.stock - count;
    }
}
