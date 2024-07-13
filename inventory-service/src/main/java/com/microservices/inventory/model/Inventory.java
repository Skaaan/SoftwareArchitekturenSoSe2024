package com.microservices.inventory.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
public class Inventory {

    @Id
    private String id;

    @Setter
    @Getter
    private String skuCode;
    @Setter
    @Getter
    private int quantity;

}
