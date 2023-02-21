package com.marcdenning.azure.app.product;

import java.util.List;

/**
 * Product service provides Product data access and manipulation methods.
 */
public interface ProductService {

    List<ProductDto> getTopProducts();
}
