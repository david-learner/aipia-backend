package com.aipiabackend.support.fixture;

import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.repository.ProductRepository;
import java.util.List;
import org.assertj.core.util.Lists;

/**
 * 인수 테스트용 상품(Product) 픽스처를 생성하는 헬퍼 클래스
 */
public class AcceptanceProductFixture {

    /**
     * 노트북 상품을 생성하고 저장한다
     */
    public static Product 노트북_생성(ProductRepository productRepository) {
        Product laptop = Product.of("노트북", 1500000L, 100, "고성능 노트북");
        return productRepository.save(laptop);
    }

    /**
     * 마우스 상품을 생성하고 저장한다
     */
    public static Product 마우스_생성(ProductRepository productRepository) {
        Product mouse = Product.of("마우스", 35000L, 100, "무선 마우스");
        return productRepository.save(mouse);
    }

    /**
     * 기본 상품들(노트북, 마우스)을 생성하고 저장한다
     */
    public static List<Product> 기본_상품들_생성(ProductRepository productRepository) {
        return Lists.list(노트북_생성(productRepository), 마우스_생성(productRepository));
    }

    /**
     * 커스텀 상품을 생성하고 저장한다
     *
     * @param productRepository 상품 저장소
     * @param name              상품명
     * @param price             가격
     * @param stock             재고
     * @param description       설명
     * @return 저장된 상품
     */
    public static Product 상품_생성(ProductRepository productRepository,
                                String name,
                                Long price,
                                Integer stock,
                                String description) {
        Product product = Product.of(name, price, stock, description);
        return productRepository.save(product);
    }
}
