package com.microservices.inventory.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.inventory.controller.InventoryController;
import com.microservices.inventory.model.Inventory;
import com.microservices.inventory.repository.InventoryRepository;
import com.microservices.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
public class InventoryServiceIntegrationTests {

    @Container
    private static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @Container
    private static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.8-management-alpine")
            .withExposedPorts(5672, 15672);

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InventoryRepository inventoryRepository;

    @SpyBean
    private InventoryService inventoryService;

    @SpyBean
    private AmqpTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
        System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
        System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
        System.setProperty("spring.rabbitmq.host", rabbitMQContainer.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitMQContainer.getAmqpPort().toString());

        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryController(inventoryService)).build();
    }

    @Test
    void testIsInStock() throws Exception {
        Inventory inventory = new Inventory(null, "123", 10);
        inventoryRepository.save(inventory);

        mockMvc.perform(get("/api/inventory/123")
                        .param("quantity", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testReduceStock() throws Exception {
        Inventory inventory = new Inventory(null, "123", 10);
        inventoryRepository.save(inventory);

        mockMvc.perform(post("/api/inventory/reduce/123")
                        .param("quantity", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Inventory updatedInventory = inventoryRepository.findByIsbn("123").orElseThrow();
            assertEquals(5, updatedInventory.getQuantity());
        });
    }

    @Test
    @Transactional
    void testHandleProductDelete() {
        Inventory inventory = new Inventory(null, "123", 10);
        inventoryRepository.save(inventory);

        inventoryService.handleProductDelete("123");

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(inventoryRepository.findByIsbn("123").isEmpty());
        });
    }
}