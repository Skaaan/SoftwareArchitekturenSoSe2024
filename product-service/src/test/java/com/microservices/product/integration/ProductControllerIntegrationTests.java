package com.microservices.product.integration;

import com.microservices.product.dto.ProductRequest;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest("1234567890", "Test Product", "Test Description", BigDecimal.valueOf(8.00), "http://example.com/image.jpg", "Author", "Genre", "2023", 1);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"1234567890\",\"name\":\"Test Product\",\"description\":\"Test Description\",\"price\":8.00,\"imageLink\":\"http://example.com/image.jpg\",\"author\":\"Author\",\"genre\":\"Genre\",\"publishedYear\":\"2023\",\"quantity\":1}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product created"));
    }

    @Test
    void testGetAllProducts() throws Exception {
        Product product = new Product(null, "1234567890", "Test Product", "Test Description", BigDecimal.valueOf(8.00), "http://example.com/image.jpg", "Author", "Genre", "2023", 1);
        productRepository.save(product);

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("1234567890"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product = new Product(null, "1234567890", "Test Product", "Test Description", BigDecimal.valueOf(8.00), "http://example.com/image.jpg", "Author", "Genre", "2023", 1);
        productRepository.save(product);

        mockMvc.perform(delete("/api/product/1234567890"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product product = new Product(null, "1234567890", "Test Product", "Test Description", BigDecimal.valueOf(100.00), "http://example.com/image.jpg", "Author", "Genre", "2023", 1);
        productRepository.save(product);

        ProductRequest productRequest = new ProductRequest("1234567890", "Updated Product", "Updated Description", BigDecimal.valueOf(200.00), "http://example.com/image2.jpg", "New Author", "New Genre", "2024", 2);

        mockMvc.perform(put("/api/product/1234567890")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"1234567890\",\"name\":\"Updated Product\",\"description\":\"Updated Description\",\"price\":200.00,\"imageLink\":\"http://example.com/image2.jpg\",\"author\":\"New Author\",\"genre\":\"New Genre\",\"publishedYear\":\"2024\",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(200.00));
    }
}
