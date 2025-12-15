package com.vivek.commons.config;

import com.vivek.commons.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * RabbitMQ Configuration for Phase 3: Asynchronous Messaging
 * Sets up exchanges, queues, bindings, and dead letter queues with comprehensive retry policies
 */
@Configuration
@Slf4j
public class RabbitMQConfig {
    
    // ============= MESSAGE CONVERTER =============
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        
        // Enable publisher confirms and returns
        template.setMandatory(true);
        template.setRetryTemplate(retryTemplate());
        
        return template;
    }
    
    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setDefaultRequeueRejected(false); // Send to DLQ on failure
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        // Retry policy
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(AppConstants.Messaging.MAX_RETRY_COUNT);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        // Backoff policy
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(Long.parseLong(AppConstants.Messaging.RETRY_DELAY));
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        return retryTemplate;
    }
    
    // ============= EXCHANGES =============
    
    @Bean
    public TopicExchange bookExchange() {
        return ExchangeBuilder
                .topicExchange(AppConstants.Messaging.BOOK_EXCHANGE)
                .durable(true)
                .build();
    }
    
    @Bean
    public TopicExchange userExchange() {
        return ExchangeBuilder
                .topicExchange(AppConstants.Messaging.USER_EXCHANGE)
                .durable(true)
                .build();
    }
    
    @Bean
    public TopicExchange notificationExchange() {
        return ExchangeBuilder
                .topicExchange(AppConstants.Messaging.NOTIFICATION_EXCHANGE)
                .durable(true)
                .build();
    }
    
    @Bean
    public DirectExchange dlqExchange() {
        return ExchangeBuilder
                .directExchange(AppConstants.Messaging.DLQ_EXCHANGE)
                .durable(true)
                .build();
    }
    
    // ============= BOOK EVENT QUEUES =============
    
    @Bean
    public Queue bookCreatedQueue() {
        return QueueBuilder
                .durable(AppConstants.Messaging.BOOK_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", AppConstants.Messaging.DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", AppConstants.Messaging.BOOK_CREATED_DLQ)
                .withArgument("x-message-ttl", Long.parseLong(AppConstants.Messaging.MESSAGE_TTL))
                .build();
    }
    
    @Bean
    public Queue bookUpdatedQueue() {
        return QueueBuilder
                .durable(AppConstants.Messaging.BOOK_UPDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", AppConstants.Messaging.DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", AppConstants.Messaging.BOOK_UPDATED_DLQ)
                .withArgument("x-message-ttl", Long.parseLong(AppConstants.Messaging.MESSAGE_TTL))
                .build();
    }
    
    @Bean
    public Queue bookDeletedQueue() {
        return QueueBuilder
                .durable(AppConstants.Messaging.BOOK_DELETED_QUEUE)
                .withArgument("x-dead-letter-exchange", AppConstants.Messaging.DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", AppConstants.Messaging.BOOK_DELETED_DLQ)
                .withArgument("x-message-ttl", Long.parseLong(AppConstants.Messaging.MESSAGE_TTL))
                .build();
    }
    
    // ============= NOTIFICATION QUEUES =============
    
    @Bean
    public Queue notificationSendQueue() {
        return QueueBuilder
                .durable(AppConstants.Messaging.NOTIFICATION_SEND_QUEUE)
                .withArgument("x-dead-letter-exchange", AppConstants.Messaging.DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", AppConstants.Messaging.NOTIFICATION_SEND_DLQ)
                .withArgument("x-message-ttl", Long.parseLong(AppConstants.Messaging.MESSAGE_TTL))
                .build();
    }
    
    // ============= DEAD LETTER QUEUES =============
    
    @Bean
    public Queue bookCreatedDlq() {
        return QueueBuilder
                .durable(AppConstants.Messaging.BOOK_CREATED_DLQ)
                .build();
    }
    
    @Bean
    public Queue bookUpdatedDlq() {
        return QueueBuilder
                .durable(AppConstants.Messaging.BOOK_UPDATED_DLQ)
                .build();
    }
    
    @Bean
    public Queue bookDeletedDlq() {
        return QueueBuilder
                .durable(AppConstants.Messaging.BOOK_DELETED_DLQ)
                .build();
    }
    
    @Bean
    public Queue notificationSendDlq() {
        return QueueBuilder
                .durable(AppConstants.Messaging.NOTIFICATION_SEND_DLQ)
                .build();
    }
    
    // ============= BINDINGS =============
    
    @Bean
    public Binding bookCreatedBinding() {
        return BindingBuilder
                .bind(bookCreatedQueue())
                .to(bookExchange())
                .with(AppConstants.Messaging.BOOK_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding bookUpdatedBinding() {
        return BindingBuilder
                .bind(bookUpdatedQueue())
                .to(bookExchange())
                .with(AppConstants.Messaging.BOOK_UPDATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding bookDeletedBinding() {
        return BindingBuilder
                .bind(bookDeletedQueue())
                .to(bookExchange())
                .with(AppConstants.Messaging.BOOK_DELETED_ROUTING_KEY);
    }
    
    @Bean
    public Binding notificationSendBinding() {
        return BindingBuilder
                .bind(notificationSendQueue())
                .to(notificationExchange())
                .with(AppConstants.Messaging.NOTIFICATION_SEND_ROUTING_KEY);
    }
    
    // ============= DLQ BINDINGS =============
    
    @Bean
    public Binding bookCreatedDlqBinding() {
        return BindingBuilder
                .bind(bookCreatedDlq())
                .to(dlqExchange())
                .with(AppConstants.Messaging.BOOK_CREATED_DLQ);
    }
    
    @Bean
    public Binding bookUpdatedDlqBinding() {
        return BindingBuilder
                .bind(bookUpdatedDlq())
                .to(dlqExchange())
                .with(AppConstants.Messaging.BOOK_UPDATED_DLQ);
    }
    
    @Bean
    public Binding bookDeletedDlqBinding() {
        return BindingBuilder
                .bind(bookDeletedDlq())
                .to(dlqExchange())
                .with(AppConstants.Messaging.BOOK_DELETED_DLQ);
    }
    
    @Bean
    public Binding notificationSendDlqBinding() {
        return BindingBuilder
                .bind(notificationSendDlq())
                .to(dlqExchange())
                .with(AppConstants.Messaging.NOTIFICATION_SEND_DLQ);
    }
}