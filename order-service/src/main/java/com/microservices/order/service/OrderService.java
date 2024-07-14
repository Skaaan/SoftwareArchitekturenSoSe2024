package com.microservices.order.service;

import com.microservices.order.dto.OrderLineItemsDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.common.StockCheckResponse;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderLineItems;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final AmqpTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

   // private String noti


    public String placeOrder(OrderRequest orderRequest) {
        boolean allProductsInStock = orderRequest.getOrderLineItemsDtoList().stream()
                .allMatch(this::isProductInStock);

        if (allProductsInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList().stream()
                    .map(this::mapToEntity)
                    .collect(Collectors.toList());
            order.setOrderLineItemsList(orderLineItemsList);

            orderRepository.save(order);
            rabbitTemplate.convertAndSend("notification.request.routing.key", order.getOrderNumber());
            return "Order placed successfully!";
        } else {
            throw new RuntimeException("Cannot place order. This product is not in stock.");
        }
    }

    private boolean isProductInStock(OrderLineItemsDto orderLineItemsDto) {
        try {
            String skuCode = orderLineItemsDto.getSkuCode();
            rabbitTemplate.convertAndSend(exchangeName, "stock.check.request.routing.key", skuCode);
            StockCheckResponse response = (StockCheckResponse) rabbitTemplate.receiveAndConvert("stock.check.response.queue", 5000);
            return response != null && response.isInStock();
        } catch (Exception e) {
            log.error("Error checking stock for SKU code: {}", orderLineItemsDto.getSkuCode(), e);
            return false;
        }
    }

    private OrderLineItems mapToEntity(OrderLineItemsDto dto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(dto.getSkuCode());
        orderLineItems.setPrice(dto.getPrice());
        orderLineItems.setQuantity(dto.getQuantity());
        return orderLineItems;
    }
}
