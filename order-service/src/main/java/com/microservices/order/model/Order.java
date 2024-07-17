package com.microservices.order.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "t_orders")
public class Order {
    @Id
    private String id;

    private String orderNumber;

    private List<OrderLineItems> orderLineItemsList;
}