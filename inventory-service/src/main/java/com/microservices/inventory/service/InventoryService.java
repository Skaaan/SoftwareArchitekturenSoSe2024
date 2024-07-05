package com.microservices.inventory.service;


import com.microservices.common.StockCheckResponse;
import com.microservices.inventory.model.Inventory;
import com.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
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
    public boolean isInStock(String skuCode) {
        log.debug("Checking inventory for SKU code: {}", skuCode);
        Optional<Inventory> inventoryOptional = inventoryRepository.findBySkuCode(skuCode);
        boolean inStock = inventoryOptional.isPresent();
        log.debug("SKU code: {} is in stock: {}", skuCode, inStock);
        return inStock;
    }

    @RabbitListener(queues = "${rabbitmq.queue.stock-check-request}")
    public void receiveStockCheckRequest(String skuCode) {
        log.info("Received stock check request for SKU code: {}", skuCode);
        boolean inStock = isInStock(skuCode);
        StockCheckResponse response = new StockCheckResponse(skuCode, inStock);
        sendStockCheckResponse(response);
        log.info("Sent stock check response for SKU code: {} - In stock: {}", skuCode, inStock);
    }

    private void sendStockCheckResponse(StockCheckResponse response) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, "stock.check.response.routing.key", response);
        } catch (Exception e) {
            log.error("Error sending stock check response: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.product}")
    public void updateStock(String skuCode) {
        log.info("Received new product event for SKU code: {}", skuCode);
        Inventory inventory = new Inventory();
        inventory.setSkuCode(skuCode);
        inventory.setQuantity(0);
        inventoryRepository.save(inventory);
        log.info("Initialized stock for SKU code: {}", skuCode);
    }
}
