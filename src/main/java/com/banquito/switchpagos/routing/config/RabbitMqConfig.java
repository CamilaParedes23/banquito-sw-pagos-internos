package com.banquito.switchpagos.onussettlement.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange routingExchange(@Value("${rabbit.exchange.routing}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public TopicExchange settlementExchange(@Value("${rabbit.exchange.settlement}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue settlementOnUsQueue(@Value("${rabbit.queue.settlement.on-us}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue settlementCompletedQueue(@Value("${rabbit.queue.settlement.completed}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding routedOnUsBinding(
            TopicExchange routingExchange,
            Queue settlementOnUsQueue,
            @Value("${rabbit.routing-key.routed-on-us}") String routingKey) {
        return BindingBuilder.bind(settlementOnUsQueue).to(routingExchange).with(routingKey);
    }

    @Bean
    public Binding settlementCompletedBinding(
            TopicExchange settlementExchange,
            Queue settlementCompletedQueue,
            @Value("${rabbit.routing-key.on-us-completed}") String routingKey) {
        return BindingBuilder.bind(settlementCompletedQueue).to(settlementExchange).with(routingKey);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        JacksonJsonMessageConverter messageConverter = new JacksonJsonMessageConverter();
        messageConverter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        return messageConverter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
