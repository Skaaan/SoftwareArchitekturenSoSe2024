package com.microservices.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "t_inventory")
public class Inventory {

    @Id
    private String id;

    @Setter
    @Getter
    private String isbn;
    @Setter
    @Getter
    private int quantity;

}
