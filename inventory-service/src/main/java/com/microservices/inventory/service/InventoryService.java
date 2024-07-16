package com.microservices.inventory.service;

import com.microservices.inventory.model.Inventory;
import com.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final AmqpTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Transactional(readOnly = true)
    public boolean isInStock(String isbn, int quantity) {
        log.debug("Checking inventory for ISBN: {}", isbn);
        Optional<Inventory> inventoryOptional = inventoryRepository.findByIsbn(isbn);
        boolean inStock = inventoryOptional.isPresent() && inventoryOptional.get().getQuantity() >= quantity;
        log.debug("ISBN: {} is in stock: {}", isbn, inStock);
        return inStock;
    }

    @Transactional
    public void reduceStock(String isbn, int quantity) {
        log.debug("Reducing stock for ISBN: {} by {}", isbn, quantity);
        Optional<Inventory> inventoryOptional = inventoryRepository.findByIsbn(isbn);

        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            int newQuantity = inventory.getQuantity() - quantity;
            if (newQuantity < 0) {
                throw new RuntimeException("Not enough stock for ISBN: " + isbn);
            }
            inventory.setQuantity(newQuantity);
            inventoryRepository.save(inventory);
            log.debug("Reduced stock for ISBN: {}. New quantity: {}", isbn, newQuantity);
        } else {
            throw new RuntimeException("Inventory not found for ISBN: " + isbn);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.stock-check-request}")
    public void receiveStockCheckRequest(String isbn) {
        log.info("Received stock check request for ISBN code: {}", isbn);
        boolean inStock = isInStock(isbn, 1);  // Assuming checking for at least 1 quantity
        // Send response (implement sending logic)
    }

    @RabbitListener(queues = "${rabbitmq.queue.product}")
    public void updateStock(String isbn) {
        log.info("Received new product event for ISBN code: {}", isbn);

        boolean updated = false;
        while (!updated) {
            Optional<Inventory> inventoryOptional = inventoryRepository.findByIsbn(isbn);

            Inventory inventory;
            if (inventoryOptional.isEmpty()) {
                inventory = new Inventory();
                inventory.setIsbn(isbn);
                inventory.setQuantity(1);
                log.info("Initialized stock for ISBN code: {}", isbn);
            } else {
                inventory = inventoryOptional.get();
                log.info("Current quantity for ISBN {}: {}", isbn, inventory.getQuantity());
                inventory.setQuantity(inventory.getQuantity() + 1);
                log.info("Increased stock for ISBN code: {}. New quantity: {}", isbn, inventory.getQuantity());
            }

            try {
                inventoryRepository.save(inventory);
                updated = true;
            } catch (OptimisticLockingFailureException e) {
                log.warn("Optimistic locking failure for ISBN {}. Retrying...", isbn);
            }
        }
    }

}