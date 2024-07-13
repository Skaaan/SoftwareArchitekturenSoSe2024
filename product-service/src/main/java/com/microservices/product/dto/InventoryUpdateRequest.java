// File: src/main/java/com/microservices/product/dto/InventoryUpdateRequest.java
package com.microservices.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdateRequest {
    private String isbn;
    private int quantity;
}
