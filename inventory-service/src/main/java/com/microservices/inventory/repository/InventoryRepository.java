package com.microservices.inventory.repository;

import com.microservices.inventory.model.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
    Optional<Inventory> findByIsbn(String isbn); // Use 'findByIsbn' to match the property name in the entity
}
