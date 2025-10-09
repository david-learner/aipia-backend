package com.aipiabackend.product.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest(
    @NotBlank(message = "상품명은 필수입니다")
    String name,

    @NotNull(message = "가격은 필수입니다")
    Long price,

    @NotNull(message = "재고 수량은 필수입니다")
    Integer stock,

    @NotBlank(message = "상품 설명은 필수입니다")
    String description
) {
}
