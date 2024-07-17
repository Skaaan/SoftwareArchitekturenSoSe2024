package com.microservices.inventory.controller;


import com.microservices.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsInStock() throws Exception {
        when(inventoryService.isInStock("123", 5)).thenReturn(true);

        mockMvc.perform(get("/api/inventory/123")
                        .param("quantity", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(inventoryService).isInStock("123", 5);
    }

    @Test
    void testReduceStock() throws Exception {
        doNothing().when(inventoryService).reduceStock("123", 5);

        mockMvc.perform(post("/api/inventory/reduce/123")
                        .param("quantity", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(inventoryService).reduceStock("123", 5);
    }
}