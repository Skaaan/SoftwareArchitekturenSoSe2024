package com.microservices.basket.controller;

import com.microservices.basket.model.Basket;
import com.microservices.basket.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/baskets")
@RequiredArgsConstructor
@Slf4j
public class BasketController {

    private final BasketService basketService;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping
    public Basket getBasket() {
        log.debug("Fetching basket for logged-in user");
        return basketService.getBasket();
    }

    @PostMapping("/add")
    public Basket addItemToBasket(@RequestBody BasketItemRequest basketItemRequest) {
        log.debug("Adding item to basket with ISBN: {} and quantity: {}",
                basketItemRequest.getIsbn(), basketItemRequest.getQuantity());
        return basketService.addItemToBasket(basketItemRequest.getIsbn(), basketItemRequest.getQuantity());
    }

    @DeleteMapping("/remove")
    public Basket removeItemFromBasket(@RequestParam String isbn) {
        log.debug("Removing item from basket with ISBN: {}", isbn);
        return basketService.removeItemFromBasket(isbn);
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public String checkout() {
        String userEmail = getLoggedInUserEmail();
        log.debug("Checkout initiated for user: {} ({})", userEmail);

        Basket basket = basketService.getBasket();
        basketService.clearBasket();

        // Send order details, user email, and user name to the notification service
        String orderDetails = restTemplate.postForObject("http://api-gateway:9001/api/order", basket, String.class);
        rabbitTemplate.convertAndSend("order.confirmation.queue", userEmail + ";"  + ";" + orderDetails);

        return orderDetails;
    }

    private String getLoggedInUserEmail() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getClaimAsString("email");
    }

    private String getLoggedInUserName() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getClaimAsString("name");
    }
}
