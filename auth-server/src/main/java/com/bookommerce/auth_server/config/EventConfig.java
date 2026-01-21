package com.bookommerce.auth_server.config;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.Subscription;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// @formatter:off
@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventConfig {

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory) {
        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainerOptions.builder()
                        .batchSize(1)
                        .pollTimeout(Duration.ofSeconds(1))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = 
            StreamMessageListenerContainer.create(redisConnectionFactory, options);
            
        log.info("Starting listener container: [stream={}, group={}]", "registration-success-event", "registration-success-event-group");
        listenerContainer.start();

        return listenerContainer;
    }

    @Bean
    public Subscription registrationSuccessEventHandlerSubscription(
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer,
        @Qualifier("registrationSuccessEventHandler")
        StreamListener<String, MapRecord<String, String, String>> registrationSuccessEventHandler,
        RedisConnectionFactory redisConnectionFactory) {
        log.info("Creating consumer group: [stream={}, group={}]", "registration-success-event", "registration-success-event-group");
        createConsumerGroupIfNotExists(redisConnectionFactory, "registration-success-event", "registration-success-event-group");

        Subscription subscription = listenerContainer.register(
                StreamMessageListenerContainer.StreamReadRequest
                        .builder(StreamOffset.create("registration-success-event", ReadOffset.lastConsumed()))
                        .cancelOnError(t -> false)
                        .consumer(Consumer.from("registration-success-event-group", UUID.randomUUID().toString()))
                        .autoAcknowledge(true)
                        .build(), registrationSuccessEventHandler);

        return subscription;
    }

    @Bean
    public Subscription resendAccountActivationEmailEventHandlerSubscription(
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer,
        @Qualifier("resendAccountActivationEmailEventHandler")
        StreamListener<String, MapRecord<String, String, String>> resendAccountActivationEmailEventHandler,
        RedisConnectionFactory redisConnectionFactory) {
        log.info("Creating consumer group: [stream={}, group={}]", "resend-account-activation-email-event", "resend-account-activation-email-event-group");
        createConsumerGroupIfNotExists(redisConnectionFactory, "resend-account-activation-email-event", "resend-account-activation-email-event-group");

        Subscription subscription = listenerContainer.register(
                StreamMessageListenerContainer.StreamReadRequest
                        .builder(StreamOffset.create("resend-account-activation-email-event", ReadOffset.lastConsumed()))
                        .cancelOnError(t -> false)
                        .consumer(Consumer.from("resend-account-activation-email-event-group", UUID.randomUUID().toString()))
                        .autoAcknowledge(true)
                        .build(), resendAccountActivationEmailEventHandler);

        return subscription;
    }

    private void createConsumerGroupIfNotExists(RedisConnectionFactory redisConnectionFactory, String streamKey, String groupName) {
        try {
            createConsumerGroup(redisConnectionFactory, streamKey, groupName);
        } catch (RedisSystemException ex) {
            log.error(ex.getMessage());
        }
    }

    private void createConsumerGroup(RedisConnectionFactory redisConnectionFactory, String streamKey, String groupName) {
        try {
            redisConnectionFactory.getConnection()
                .streamCommands()
                .xGroupCreate(streamKey.getBytes(), groupName, ReadOffset.from("0-0"), true);
        } catch (RedisSystemException exception) {
            log.warn(exception.getCause().getMessage());
        }
    }
}
