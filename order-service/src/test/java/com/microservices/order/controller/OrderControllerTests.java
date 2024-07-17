package com.microservices.order.controller;

import com.microservices.order.dto.BasketItemDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(OrderController.class)
public class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private OrderRequest orderRequest;

    @BeforeEach
    public void setUp() {
        BasketItemDto item1 = new BasketItemDto(1L, 2);
        BasketItemDto item2 = new BasketItemDto(2L, 3);

        orderRequest = new OrderRequest();
        orderRequest.setUserId("user123");
        orderRequest.setItems(List.of(item1, item2));
    }

    @Test
    public void testPlaceOrder() throws Exception {
        when(orderService.placeOrder(any(OrderRequest.class))).thenReturn("Order placed successfully!");

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user123\",\"items\":[{\"productId\":1,\"quantity\":2},{\"productId\":2,\"quantity\":3}]}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Order placed successfully!"));
    }
}