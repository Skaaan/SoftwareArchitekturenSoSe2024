package com.microservices.basket.controller;

import com.microservices.basket.model.Basket;
import com.microservices.basket.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/baskets")
@RequiredArgsConstructor
@Slf4j
public class BasketController {

    private final BasketService basketService;
    private final RestTemplate restTemplate;

    @GetMapping
    public Basket getBasket(@RequestParam(value = "userId", defaultValue = "default-user") String userId) {
        log.debug("Fetching basket for userId: {}", userId);
        return basketService.getBasket(userId);
    }

    @PostMapping("/add")
    public Basket addItemToBasket(@RequestParam(value = "userId", defaultValue = "default-user") String userId,
                                  @RequestBody BasketItemRequest basketItemRequest) {
        log.debug("Adding item to basket for userId: {} with ISBN: {} and quantity: {}",
                userId, basketItemRequest.getIsbn(), basketItemRequest.getQuantity());
        return basketService.addItemToBasket(userId, basketItemRequest.getIsbn(), basketItemRequest.getQuantity());
    }
    @DeleteMapping("/remove")
    public Basket removeItemFromBasket(@RequestParam(value = "userId", defaultValue = "default-user") String userId,
                                       @RequestParam String isbn) {
        log.debug("Removing item from basket for userId: {} with ISBN: {}", userId, isbn);
        return basketService.removeItemFromBasket(userId, isbn);
    }

    @PostMapping("/checkout/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String checkout(@PathVariable String userId) {
        log.debug("Checkout initiated for userId: {}", userId);
        Basket basket = basketService.getBasket(userId);
        basketService.clearBasket();
        return restTemplate.postForObject("http://localhost:9001/api/order", basket, String.class);
    }
}
