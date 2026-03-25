package com.omnicharge.paymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name:omnicharge.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue.payment.success.name:payment.success.queue}")
    private String paymentSuccessQueue;

    @Value("${rabbitmq.queue.payment.failed.name:payment.failed.queue}")
    private String paymentFailedQueue;

    @Value("${rabbitmq.routing.payment.success.key:payment.success}")
    private String paymentSuccessRoutingKey;

    @Value("${rabbitmq.routing.payment.failed.key:payment.failed}")
    private String paymentFailedRoutingKey;

    @Bean
    public Queue paymentSuccessQueue() {
        return new Queue(paymentSuccessQueue);
    }

    @Bean
    public Queue paymentFailedQueue() {
        return new Queue(paymentFailedQueue);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding successBinding() {
        return BindingBuilder
                .bind(paymentSuccessQueue())
                .to(exchange())
                .with(paymentSuccessRoutingKey);
    }

    @Bean
    public Binding failedBinding() {
        return BindingBuilder
                .bind(paymentFailedQueue())
                .to(exchange())
                .with(paymentFailedRoutingKey);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
