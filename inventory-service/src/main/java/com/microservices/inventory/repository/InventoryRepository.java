package com.microservices.inventory.repository;

import com.microservices.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByIsbn(String isbn); // Use 'findByIsbn' to match the property name in the entity
}
