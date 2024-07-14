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
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getItems().stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItemsList);

        orderRepository.save(order);

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
