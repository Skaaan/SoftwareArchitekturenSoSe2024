package com.microservices.order.service;

import com.microservices.order.dto.BasketItemDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.order.model.Order;
import com.microservices.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTests {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private AmqpTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        rabbitTemplate = Mockito.mock(AmqpTemplate.class);
        orderService = new OrderService(orderRepository, rabbitTemplate);
    }

    @Test
    void testPlaceOrder() {
        BasketItemDto item1 = new BasketItemDto(1L, 2);
        BasketItemDto item2 = new BasketItemDto(2L, 1);
        List<BasketItemDto> items = Arrays.asList(item1, item2);
        OrderRequest orderRequest = new OrderRequest("user1", "User1", "user1@gmail.com", items);

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);

        String orderNumber = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());
        Order savedOrder = orderArgumentCaptor.getValue();
        assertNotNull(savedOrder.getOrderNumber());
        assertEquals(2, savedOrder.getOrderLineItemsList().size());
        assertEquals(orderNumber, savedOrder.getOrderNumber());

        verify(rabbitTemplate, times(1)).convertAndSend(eq("order.exchange"), eq("notification.request.routing.key"), anyString());
    }
}
