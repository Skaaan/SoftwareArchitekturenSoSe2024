package com.microservices.product.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    private String id;

    private String isbn;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageLink;
    private String author;
    private String genre;
    private String publishedYear;
    private int quantity; // New field
}
