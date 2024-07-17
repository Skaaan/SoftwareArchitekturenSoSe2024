package com.microservices.basket.service;

import com.microservices.basket.model.Basket;
import com.microservices.basket.model.BasketItem;
import com.microservices.basket.repository.BasketRepository;
import com.microservices.common.StockCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final BasketRepository basketRepository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    private static final String INVENTORY_SERVICE_URL = "http://localhost:9001/api/inventory";

    public Basket getBasket() {
        String userId = getLoggedInUserId();
        log.debug("Getting basket for userId: {}", userId);
        return basketRepository.findByUserId(userId).orElseGet(() -> createBasket(userId));
    }

    public Basket addItemToBasket(String isbn, int quantity) {
        String userId = getLoggedInUserId();
        Basket basket = basketRepository.findByUserId(userId).orElseGet(() -> createBasket(userId));

        log.debug("Adding item to basket: userId={}, ISBN={}, Quantity={}", userId, isbn, quantity);

        if (isProductInStock(isbn, quantity)) {
            boolean itemExists = false;

            for (BasketItem item : basket.getItems()) {
                if (item.getIsbn().equals(isbn)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                BasketItem newItem = new BasketItem();
                newItem.setIsbn(isbn);
                newItem.setQuantity(quantity);
                basket.getItems().add(newItem);
            }

            Basket savedBasket = basketRepository.save(basket);
            log.debug("Item added to basket successfully: {}", savedBasket);

            // Decrease inventory quantity
            decreaseInventory(isbn, quantity);

            return savedBasket;
        } else {
            log.warn("Product is not in stock: ISBN={}, Quantity={}", isbn, quantity);
            throw new RuntimeException("Product is not in stock");
        }
    }

    public Basket removeItemFromBasket(String isbn) {
        String userId = getLoggedInUserId();
        Basket basket = basketRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Basket not found"));

        log.debug("Removing item from basket: userId={}, ISBN={}", userId, isbn);

        Iterator<BasketItem> iterator = basket.getItems().iterator();
        while (iterator.hasNext()) {
            BasketItem item = iterator.next();
            if (item.getIsbn().equals(isbn)) {
                int quantityToRemove = item.getQuantity();
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    quantityToRemove = 1;
                } else {
                    iterator.remove();
                }

                Basket savedBasket = basketRepository.save(basket);
                log.debug("Item removed from basket successfully: {}", savedBasket);

                // Decrease inventory quantity
                increaseInventory(isbn, item.getQuantity());
                return savedBasket;
            }
        }

        throw new RuntimeException("Item not found in basket");
    }

    private boolean isProductInStock(String isbn, int quantity) {
        try {
            String url = INVENTORY_SERVICE_URL + "/" + isbn + "?quantity=" + quantity;
            log.debug("Checking stock for URL: {}", url);
            StockCheckResponse response = restTemplate.getForObject(url, StockCheckResponse.class);
            boolean inStock = response != null && response.isInStock();
            log.debug("Stock check result for ISBN={}, Quantity={} : {}", isbn, quantity, inStock);
            return inStock;
        } catch (Exception e) {
            log.error("Failed to check product stock for ISBN={}, Quantity={}", isbn, quantity, e);
            throw new RuntimeException("Failed to check product stock", e);
        }
    }

    private void decreaseInventory(String isbn, int quantity) {
        try {
            String url = INVENTORY_SERVICE_URL + "/reduce/" + isbn + "?quantity=" + quantity;
            log.debug("Decreasing inventory for URL: {}", url);
            restTemplate.postForObject(url, null, Void.class);
            log.debug("Inventory decreased for ISBN={}, Quantity={}", isbn, quantity);
        } catch (Exception e) {
            log.error("Failed to decrease inventory for ISBN={}, Quantity={}", isbn, quantity, e);
            throw new RuntimeException("Failed to decrease inventory", e);
        }
    }

    private void increaseInventory(String isbn, int quantity) {
        try {
            String url = INVENTORY_SERVICE_URL + "/reduce/" + isbn + "?quantity=" + (-quantity);
            log.debug("Decreasing inventory for URL: {}", url);
            restTemplate.postForObject(url, null, Void.class);
            log.debug("Inventory decreased for ISBN={}, Quantity={}", isbn, quantity);
        } catch (Exception e) {
            log.error("Failed to decrease inventory for ISBN={}, Quantity={}", isbn, quantity, e);
            throw new RuntimeException("Failed to decrease inventory", e);
        }
    }

    private Basket createBasket(String userId) {
        Basket basket = new Basket();
        basket.setUserId(userId);
        basket.setItems(new ArrayList<>());
        return basket;
    }

    private String getLoggedInUserId() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getSubject();
    }

    private String getLoggedInUserEmail() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getClaimAsString("email");
    }

    private String getLoggedInUserName() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getClaimAsString("name");
    }

    public void clearBasket() {
        String userId = getLoggedInUserId();
        basketRepository.findByUserId(userId).ifPresent(basket -> {
            basket.getItems().clear();
            basketRepository.save(basket);
        });
    }

    public void checkout() {
        String userId = getLoggedInUserId();
        String userEmail = getLoggedInUserEmail();
        String userName = getLoggedInUserName();
        String orderNumber = UUID.randomUUID().toString();
        Basket basket = getBasket();
        clearBasket();
        // Send a message to the notification service with the user's email, name, order number, and order details
        rabbitTemplate.convertAndSend("order.exchange", "orderRoutingKey", userEmail + ";" + userName + ";" + orderNumber + ";" + basket.toString());
    }
}
