package com.microservices.order.service;

import com.microservices.order.dto.BasketItemDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderLineItems;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public String placeOrder(OrderRequest orderRequest) {
        // Create a new order
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        // Map BasketItemDto to OrderLineItems
        List<OrderLineItems> orderLineItemsList = orderRequest.getItems().stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        // Set order items
        order.setOrderLineItemsList(orderLineItemsList);

        // Save the order to the repository
        orderRepository.save(order);

        // Log and return success message
        log.info("Order placed successfully with order number: {}", order.getOrderNumber());
        return "Order placed successfully!";
    }

    private OrderLineItems mapToEntity(BasketItemDto basketItemDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setProductId(basketItemDto.getProductId());
        orderLineItems.setQuantity(basketItemDto.getQuantity());
        return orderLineItems;
    }
}
