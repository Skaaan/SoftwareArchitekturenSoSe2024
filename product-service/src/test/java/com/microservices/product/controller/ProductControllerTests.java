package com.microservices.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.product.dto.ProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
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

        verify(productService).createProduct(any(ProductRequest.class));
    }

    @Test
    void testGetAllProducts() throws Exception {
        ProductResponse productResponse = new ProductResponse(1L, "123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);

        when(productService.getAllProducts()).thenReturn(Collections.singletonList(productResponse));

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("123"));

        verify(productService).getAllProducts();
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/product/123"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct("123");
    }

    @Test
    void testUpdateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest("123", "New Book Name", "New Description",
                BigDecimal.valueOf(39.99), "newImageLink", "New Author", "New Genre", "2022", 2);
        ProductResponse productResponse = new ProductResponse(1L, "123", "New Book Name", "New Description",
                BigDecimal.valueOf(39.99), "newImageLink", "New Author", "New Genre", "2022", 2);

        when(productService.updateProduct(eq("123"), any(ProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(put("/api/product/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Book Name"));

        verify(productService).updateProduct(eq("123"), any(ProductRequest.class));
    }
}