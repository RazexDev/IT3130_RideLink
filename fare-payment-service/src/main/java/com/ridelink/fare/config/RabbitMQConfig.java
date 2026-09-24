package com.ridelink.fare.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.completed}")
    private String completedRoutingKey;

    @Value("${rabbitmq.queue}")
    private String queueName;

    @Bean
    public TopicExchange rideExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue farePaymentQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding farePaymentBinding(Queue farePaymentQueue, TopicExchange rideExchange) {
        return BindingBuilder.bind(farePaymentQueue)
                .to(rideExchange)
                .with(completedRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
