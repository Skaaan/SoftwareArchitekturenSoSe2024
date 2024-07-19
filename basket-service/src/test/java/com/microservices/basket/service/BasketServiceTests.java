package com.microservices.basket.service;

import com.microservices.basket.model.Basket;
import com.microservices.basket.model.BasketItem;
import com.microservices.basket.repository.BasketRepository;
import com.microservices.common.StockCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BasketServiceTests {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BasketService basketService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("user1");
    }

    @Test
    public void testGetBasket() {
        String userId = "user1";
        Basket basket = new Basket();
        basket.setUserId(userId);

        when(basketRepository.findByUserId(userId)).thenReturn(Optional.of(basket));

        Basket result = basketService.getBasket();
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    public void testAddItemToBasket() {
        String userId = "user1";
        String isbn = "12345";
        int quantity = 2;
        Basket basket = new Basket();
        basket.setUserId(userId);
        basket.setItems(new ArrayList<>());

        when(basketRepository.findByUserId(userId)).thenReturn(Optional.of(basket));
        when(restTemplate.getForObject(anyString(), eq(StockCheckResponse.class))).thenReturn(new StockCheckResponse(true));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        Basket result = basketService.addItemToBasket(isbn, quantity);
        assertNotNull(result);
        assertFalse(result.getItems().isEmpty());
        assertEquals(isbn, result.getItems().get(0).getIsbn());
        assertEquals(quantity, result.getItems().get(0).getQuantity());
    }

    @Test
    public void testRemoveItemFromBasket() {
        String userId = "user1";
        String isbn = "12345";
        BasketItem item = new BasketItem();
        item.setIsbn(isbn);
        item.setQuantity(1);
        Basket basket = new Basket();
        basket.setUserId(userId);
        basket.setItems(new ArrayList<>(Collections.singletonList(item)));

        when(basketRepository.findByUserId(userId)).thenReturn(Optional.of(basket));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        Basket result = basketService.removeItemFromBasket(isbn);
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }
}
