package com.microservices.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    @Bean
    public Queue queue(String QUEUE_NAME) {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public TopicExchange exchange(String EXCHANGE_NAME) {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange, String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with("orderRoutingKey");
    }
}
