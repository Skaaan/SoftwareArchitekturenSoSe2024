package com.microservices.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.product.dto.ProductRequest;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Testcontainers
public class ProductServiceIntegrationTests {

    @Container
    private static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @Container
    private static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.8-management-alpine")
            .withExposedPorts(5672, 15672);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private Queue productQueue;

    @Autowired
    private Queue productDeleteQueue;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
        System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
        System.setProperty("spring.rabbitmq.host", rabbitMQContainer.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitMQContainer.getAmqpPort().toString());
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest("123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product created"));

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Product product = productRepository.findByIsbn("123").orElseThrow();
            assertEquals("123", product.getIsbn());
            assertEquals("Book Name", product.getName());
        });

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verifyMessageReceived(productQueue.getName(), "123");
        });
    }

    @Test
    void testGetAllProducts() throws Exception {
        Product product = new Product(null, "123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);
        productRepository.save(product);

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn", is("123")));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product = new Product(null, "123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);
        productRepository.save(product);

        mockMvc.perform(delete("/api/product/123"))
                .andExpect(status().isNoContent());

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertFalse(productRepository.findByIsbn("123").isPresent());
        });

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verifyMessageReceived(productDeleteQueue.getName(), "123");
        });
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product product = new Product(null, "123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);
        productRepository.save(product);

        ProductRequest productRequest = new ProductRequest("123", "New Book Name", "New Description",
                BigDecimal.valueOf(39.99), "newImageLink", "New Author", "New Genre", "2022", 2);

        mockMvc.perform(put("/api/product/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Book Name")));

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Product updatedProduct = productRepository.findByIsbn("123").orElseThrow();
            assertEquals("New Book Name", updatedProduct.getName());
            assertEquals(2, updatedProduct.getQuantity());
        });
    }

    private void verifyMessageReceived(String queueName, String expectedMessage) {
        Object message = amqpTemplate.receiveAndConvert(queueName);
        assertNotNull(message);
        assertEquals(expectedMessage, message.toString());
    }
}
