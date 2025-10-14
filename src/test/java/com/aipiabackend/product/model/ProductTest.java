package com.aipiabackend.product.model;

import static com.aipiabackend.support.model.ErrorCodeMessage.NOT_ENOUGH_STOCK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aipiabackend.support.model.exception.AipiaException;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void 재고가_충분하면_재고를_감소시킨다() {
        Product product = Product.of("상품명", 10000L, 10, "상품 설명");

        product.decreaseStock(3);

        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    void 재고가_부족하면_예외를_발생시킨다() {
        Product product = Product.of("상품명", 10000L, 5, "상품 설명");

        assertThatThrownBy(() -> product.decreaseStock(10))
            .isInstanceOf(AipiaException.class)
            .hasMessageContaining(NOT_ENOUGH_STOCK.message());
    }

    @Test
    void 재고와_요청_수량이_같으면_재고를_0으로_만든다() {
        Product product = Product.of("상품명", 10000L, 5, "상품 설명");

        product.decreaseStock(5);

        assertThat(product.getStock()).isEqualTo(0);
    }
}