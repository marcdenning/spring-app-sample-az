package com.marcdenning.azure.app.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Integer id;

    private String name;

    private String productNumber;

    private String color;

    public static ProductDto fromProduct(final Product product) {
        return new ProductDto(
            product.getId(),
            product.getName(),
            product.getProductNumber(),
            product.getColor()
        );
    }
}
