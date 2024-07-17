package com.microservices.order.service;


import com.microservices.order.dto.BasketItemDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.order.model.Order;
import com.microservices.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPlaceOrder() {
        BasketItemDto basketItemDto1 = new BasketItemDto(1L, 2);
        BasketItemDto basketItemDto2 = new BasketItemDto(2L, 3);
        OrderRequest orderRequest = new OrderRequest("user1", List.of(basketItemDto1, basketItemDto2));

        String result = orderService.placeOrder(orderRequest);

        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order savedOrder = orderArgumentCaptor.getValue();

        assertEquals("Order placed successfully!", result);
        assertEquals(2, savedOrder.getOrderLineItemsList().size());
        assertEquals(basketItemDto1.getProductId(), savedOrder.getOrderLineItemsList().get(0).getProductId());
        assertEquals(basketItemDto1.getQuantity(), savedOrder.getOrderLineItemsList().get(0).getQuantity());
        assertEquals(basketItemDto2.getProductId(), savedOrder.getOrderLineItemsList().get(1).getProductId());
        assertEquals(basketItemDto2.getQuantity(), savedOrder.getOrderLineItemsList().get(1).getQuantity());
    }
}