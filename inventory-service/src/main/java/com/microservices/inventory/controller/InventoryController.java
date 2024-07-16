package com.microservices.inventory.controller;

import com.microservices.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{ISBN}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("ISBN") String ISBN, @RequestParam int quantity) {
        return inventoryService.isInStock(ISBN, quantity);
    }

    @PostMapping("/reduce/{ISBN}")
    @ResponseStatus(HttpStatus.OK)
    public void reduceStock(@PathVariable("ISBN") String ISBN, @RequestParam int quantity) {
        inventoryService.reduceStock(ISBN, quantity);
    }
}