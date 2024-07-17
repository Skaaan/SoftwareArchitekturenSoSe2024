package com.microservices.order.integration;

import com.microservices.order.dto.BasketItemDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.order.model.Order;
import com.microservices.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@ImportTestcontainers
public class OrderServiceIntegrationTests {

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26")
            .withDatabaseName("order-service")
            .withUsername("root")
            .withPassword("mysql");

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.8-management-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    public void testPlaceOrder() throws Exception {
        BasketItemDto item = new BasketItemDto(1L, 2);
        List<BasketItemDto> items = Collections.singletonList(item);
        OrderRequest orderRequest = new OrderRequest("user1", items);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"user1\", \"items\": [{\"productId\": 1, \"quantity\": 2}]}"))
                .andExpect(status().isCreated());

        List<Order> orders = orderRepository.findAll();
        assert !orders.isEmpty();
    }
}