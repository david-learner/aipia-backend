package com.aipiabackend.product.controller;

import com.aipiabackend.product.controller.dto.ProductCreateRequest;
import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.service.ProductService;
import com.aipiabackend.product.service.dto.ProductCreateCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class ProductRestController {

    private final ProductService productService;

    /**
     * 상품을 생성한다 (관리자 전용)
     */
    @PostMapping
    public ResponseEntity<Void> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductCreateCommand command = new ProductCreateCommand(
            request.name(),
            request.price(),
            request.stock(),
            request.description()
        );

        Product savedProduct = productService.create(command);

        UriComponents productUriComponents = UriComponentsBuilder
            .fromUriString("/api/products/{productId}")
            .buildAndExpand(savedProduct.getId());

        return ResponseEntity.created(productUriComponents.toUri()).build();
    }
}
