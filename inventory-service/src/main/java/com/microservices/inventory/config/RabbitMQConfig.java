package com.microservices.inventory.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "stock.check.exchange";
    public static final String REQUEST_QUEUE_NAME = "stock.check.request.queue";
    public static final String RESPONSE_QUEUE_NAME = "stock.check.response.queue";
    public static final String REQUEST_ROUTING_KEY = "stock.check.request.routing.key";
    public static final String RESPONSE_ROUTING_KEY = "stock.check.response.routing.key";
    public static final String PRODUCT_QUEUE_NAME = "stock.check.product.queue";


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue productQueue() {
        return new Queue(PRODUCT_QUEUE_NAME);
    }

    @Bean
    public Queue stockCheckRequestQueue() {
        return new Queue(REQUEST_QUEUE_NAME);
    }

    @Bean
    public Queue stockCheckResponseQueue() {
        return new Queue(RESPONSE_QUEUE_NAME);
    }

    @Bean
    public Binding bindingStockCheckRequestQueue(Queue stockCheckRequestQueue, TopicExchange exchange) {
        return BindingBuilder.bind(stockCheckRequestQueue).to(exchange).with(REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding bindingStockCheckResponseQueue(Queue stockCheckResponseQueue, TopicExchange exchange) {
        return BindingBuilder.bind(stockCheckResponseQueue).to(exchange).with(RESPONSE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}