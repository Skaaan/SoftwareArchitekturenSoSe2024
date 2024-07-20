package com.microservices.basket.controller;

import lombok.Data;

@Data
public class BasketItemRequest {
    private String isbn;
    private int quantity;
}
