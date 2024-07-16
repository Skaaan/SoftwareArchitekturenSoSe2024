package com.microservices.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String isbn;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageLink;
    private String author;
    private String genre;
    private String publishedYear;
    private int quantity;
}
