package com.microservices.inventory.integration;

import com.microservices.inventory.model.Inventory;
import com.microservices.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class InventoryControllerIntegrationTests {

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
    }

    @Test
    void testIsInStock() throws Exception {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        inventoryRepository.save(inventory);

        mockMvc.perform(get("/api/inventory/1234567890")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testReduceStock() throws Exception {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        inventoryRepository.save(inventory);

        mockMvc.perform(post("/api/inventory/reduce/1234567890")
                        .param("quantity", "5"))
                .andExpect(status().isOk());

        Optional<Inventory> updatedInventory = inventoryRepository.findByIsbn("1234567890");
        assertTrue(updatedInventory.isPresent());
        assertEquals(5, updatedInventory.get().getQuantity());
    }

    @Test
    void testUpdateStock_existingInventory() throws Exception {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        inventoryRepository.save(inventory);

        rabbitTemplate.convertAndSend("stock.check.exchange", "stock.check.product.routing.key", "1234567890");

        Thread.sleep(1000);

        Optional<Inventory> updatedInventory = inventoryRepository.findByIsbn("1234567890");
        assertTrue(updatedInventory.isPresent());
        assertEquals(11, updatedInventory.get().getQuantity());
    }
}