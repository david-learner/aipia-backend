package com.aipiabackend.product.service.dto;

import java.math.BigDecimal;

public record ProductCreateCommand(
    String name,
    BigDecimal price,
    Integer stock,
    String description
) {
}
