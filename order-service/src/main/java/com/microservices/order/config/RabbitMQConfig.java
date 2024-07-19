package com.microservices.order.config;

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

    public static final String NOTIFICATION_EXCHANGE_NAME = "order.exchange";
    public static final String NOTIFICATION_REQUEST_QUEUE_NAME = "order.confirmation.queue";
    public static final String ORDER_RESPONSE_QUEUE_NAME = "order.response.queue";
    public static final String ORDER_REQUEST_ROUTING_KEY = "notification.request.routing.key";
    public static final String ORDER_RESPONSE_ROUTING_KEY = "order.response.routing.key";


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }


    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationRequestQueue() {
        return new Queue(NOTIFICATION_REQUEST_QUEUE_NAME);
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
    public Binding bindingNotificationRequestQueue(Queue notificationRequestQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationRequestQueue).to(notificationExchange).with(ORDER_REQUEST_ROUTING_KEY);
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