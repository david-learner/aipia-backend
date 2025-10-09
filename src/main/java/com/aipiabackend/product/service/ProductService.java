package com.aipiabackend.product.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.PRODUCT_NOT_FOUND;

import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.repository.ProductRepository;
import com.aipiabackend.product.service.dto.ProductCreateCommand;
import com.aipiabackend.support.model.exception.AipiaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product create(ProductCreateCommand command) {
        Product product = Product.of(
            command.name(),
            command.price(),
            command.stock(),
            command.description()
        );

        return productRepository.save(product);
    }

    @Transactional
    public void save(Product product) {
        productRepository.save(product);
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new AipiaException(
                PRODUCT_NOT_FOUND, "productId='%s'".formatted(productId)));
    }
}
