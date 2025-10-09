package com.aipiabackend.product.service.dto;

public record ProductCreateCommand(
    String name,
    Long price,
    Integer stock,
    String description
) {
}
