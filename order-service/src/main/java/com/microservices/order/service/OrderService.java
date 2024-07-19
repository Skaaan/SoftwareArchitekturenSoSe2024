package com.microservices.order.service;

import com.microservices.order.dto.BasketItemDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderLineItems;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
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

        // Assuming the orderRequest has user email
        String userEmail = orderRequest.getUserEmail();

        // Prepare the message with user's email and order number only
        String message = userEmail + ";" + order.getOrderNumber();

        // Log the message
        log.info("Sending message to RabbitMQ: {}", message);

        // Send the message to the notification service queue
        rabbitTemplate.convertAndSend("order.exchange", "notification.request.routing.key", message);

        // Log order placement success
        log.info("Order placed successfully with order number: {}", order.getOrderNumber());

        // Return success message with order number
        return order.getOrderNumber();
    }

    private OrderLineItems mapToEntity(BasketItemDto basketItemDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setProductId(basketItemDto.getProductId());
        orderLineItems.setQuantity(basketItemDto.getQuantity());
        return orderLineItems;
    }
}