package com.marcdenning.azure.app.product;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Product entity.
 */
@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

    List<Product> findFirst10ByOrderByNameAsc();
}
