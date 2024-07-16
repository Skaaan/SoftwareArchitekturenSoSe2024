package com.microservices.basket.repository;

import com.microservices.basket.model.Basket;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository extends MongoRepository<Basket, Long> {
    Optional<Basket> findByUserId(String userId);
}
