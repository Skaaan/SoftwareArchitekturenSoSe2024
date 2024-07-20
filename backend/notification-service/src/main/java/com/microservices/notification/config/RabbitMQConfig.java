package com.microservices.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_EXCHANGE_NAME = "order.exchange";
    public static final String NOTIFICATION_REQUEST_QUEUE_NAME = "order.confirmation.queue";
    public static final String ORDER_RESPONSE_QUEUE_NAME = "order.response.queue";
    public static final String ORDER_REQUEST_ROUTING_KEY = "notification.request.routing.key";
    public static final String ORDER_RESPONSE_ROUTING_KEY = "order.response.routing.key";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationRequestQueue() {
        return new Queue(NOTIFICATION_REQUEST_QUEUE_NAME);
    }

    @Bean
    public Binding bindingsNotificationRequestQueue(Queue notificationRequestQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationRequestQueue).to(notificationExchange).with(ORDER_REQUEST_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
