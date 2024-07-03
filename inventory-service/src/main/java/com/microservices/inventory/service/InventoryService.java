package com.microservices.inventory.service;


import com.microservices.inventory.dto.StockCheckResponse;
import com.microservices.inventory.model.Inventory;
import com.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode){
        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }

    public void sendStockCheckMessage(String skuCode) {
        rabbitTemplate.convertAndSend(exchangeName, "stock.routing.key", skuCode);
        log.info("Stock check message published to RabbitMQ for SKU code: {}", skuCode);
    }

    @RabbitListener(queues = "inventory.queue")
    public void receiveStockCheck(String skuCode) {
        log.info("Received stock check request for SKU code: {}", skuCode);
        boolean inStock = isInStock(skuCode);
        log.info("SKU code: {} is in stock: {}", skuCode, inStock);
        rabbitTemplate.convertAndSend(exchangeName, "stock.check.response.routing.key", new StockCheckResponse(skuCode, inStock));
    }

    @RabbitListener(queues = "product.queue")
    public void updateStock(String skuCode) {
        log.info("Received new product event for SKU code: {}", skuCode);
        Inventory inventory = new Inventory();
        inventory.setSkuCode(skuCode);
        inventory.setQuantity(0);
        inventoryRepository.save(inventory);
        log.info("Initialized stock for SKU code: {}", skuCode);
    }
}