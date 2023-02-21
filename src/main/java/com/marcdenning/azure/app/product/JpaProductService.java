package com.marcdenning.azure.app.product;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ProductService implementation based on Spring Data JPA repository.
 */
@Service
public class JpaProductService implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public JpaProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> getTopProducts() {
        return productRepository.findFirst10ByOrderByNameAsc().stream()
            .map(ProductDto::fromProduct)
            .collect(Collectors.toList());
    }
}
