package com.microservices.basket.integration;

import com.microservices.basket.model.Basket;
import com.microservices.basket.service.BasketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BasketControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BasketService basketService;

    private Basket basket;

    @BeforeEach
    public void setUp() {
        String userId = "test-user";
        basket = new Basket();
        basket.setId(1L);
        basket.setUserId(userId);
        basket.setItems(new ArrayList<>());

        Mockito.when(basketService.getBasket()).thenReturn(basket);
    }

    @Test
    @WithMockUser(username = "test-user")
    public void testGetBasket() throws Exception {
        mockMvc.perform(get("/api/baskets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("test-user"));
    }


}