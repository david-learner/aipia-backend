package com.aipiabackend.product.service;

import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.repository.ProductRepository;
import com.aipiabackend.product.service.dto.ProductCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(ProductCreateCommand command) {
        Product product = Product.of(
            command.name(),
            command.price(),
            command.stock(),
            command.description()
        );
        return productRepository.save(product);
    }
}
