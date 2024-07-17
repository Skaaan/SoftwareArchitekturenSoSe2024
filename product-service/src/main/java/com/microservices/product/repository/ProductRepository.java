package com.microservices.product.repository;

import com.microservices.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, Long> {
    Optional<Product> findByIsbn(String isbn); // New method
}
