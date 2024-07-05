package com.microservices.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckResponse {
    private String skuCode;
    private boolean inStock;
}

