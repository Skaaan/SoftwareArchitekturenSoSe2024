package com.microservices.order.repository;

import com.microservices.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderLineItemsList WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}