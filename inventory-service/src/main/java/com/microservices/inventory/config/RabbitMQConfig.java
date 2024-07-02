package com.microservices.inventory.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue inventoryQueue() {
        return new Queue("inventory.queue");
    }

    @Bean
    public Binding binding(Queue inventoryQueue, TopicExchange exchange) {
        return BindingBuilder.bind(inventoryQueue).to(exchange).with("inventory.routing.key");
    }

    @Bean
    public Queue productQueue() {
        return new Queue("product.queue");
    }

    @Bean
    public Binding productBinding(Queue productQueue, TopicExchange exchange) {
        return BindingBuilder.bind(productQueue).to(exchange).with("product.routing.key");
    }

    @Bean
    public Queue stockCheckResponseQueue() {
        return new Queue("stock.check.response.queue");
    }

    @Bean
    public Binding stockCheckResponseBinding(Queue stockCheckResponseQueue, TopicExchange exchange) {
        return BindingBuilder.bind(stockCheckResponseQueue).to(exchange).with("stock.check.response.routing.key");
    }
}