package com.microservices.product;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.microservices.product.dto.ProductRequest;
import com.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;

import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.microservices.product.model.Product;
import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        // First, create a product
        ProductRequest productRequest = getProductRequest();
        Product product = createProduct(productRequest);

        // Perform delete request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Assert the product is deleted
        Optional<Product> deletedProduct = productRepository.findById(product.getId());
        Assertions.assertFalse(deletedProduct.isPresent());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        // First, create a product
        ProductRequest productRequest = getProductRequest();
        Product product = createProduct(productRequest);

        // Create an update request
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Name")
                .description("Updated Description")
                .price(BigDecimal.valueOf(20))
                .imageLink("updated-link")
                .build();
        String updateRequestString = objectMapper.writeValueAsString(updateRequest);

        // Perform update request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/product/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestString))
                .andExpect(status().isOk());

        // Assert the product is updated
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        Assertions.assertEquals("Updated Name", updatedProduct.getName());
        Assertions.assertEquals("Updated Description", updatedProduct.getDescription());
        Assertions.assertEquals(BigDecimal.valueOf(20), updatedProduct.getPrice());
        Assertions.assertEquals("updated-link", updatedProduct.getImageLink());
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("Clean Code")
                .description("Clean Code introduction")
                .price(BigDecimal.valueOf(15))
                .imageLink("internet")
                .build();
    }

    private Product createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setImageLink(productRequest.getImageLink());
        return productRepository.save(product);
    }
}
