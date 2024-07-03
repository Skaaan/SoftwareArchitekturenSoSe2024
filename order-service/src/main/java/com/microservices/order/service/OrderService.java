package com.microservices.order.service;


import com.microservices.order.dto.OrderLineItemsDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderLineItems;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import com.example.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitMQConfig rabbitMQConfig;
    private final RabbitTemplate rabbitTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

       orderRepository.save(order);
       String exchangeName = "orderExchange";
       String queueName = "orderQueue";
       String routingKey = "orderRoutingKey";
       org.springframework.amqp.core.Queue queue=rabbitMQConfig.queue(queueName);
       org.springframework.amqp.core.TopicExchange exchange= rabbitMQConfig.exchange(exchangeName);
           // Create binding (implicitly creates queue and exchange if they do not exist)
        rabbitMQConfig.binding(
            queue, 
            exchange, 
            routingKey
         );

    // Publish the event to the exchange
        rabbitTemplate.convertAndSend(exchangeName, routingKey, order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}