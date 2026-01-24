package com.bookommerce.resource_server.config;

import java.time.Duration;

import org.springframework.boot.ApplicationRunner;
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

    StreamListener<String, MapRecord<String, String, String>> streamListener;

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

        log.info("Starting listener container: [stream={}, group={}]", "account-activation-success-event", "account-activation-success-event-group");
        listenerContainer.start();

        return listenerContainer;
    }

    @Bean
    public Subscription subscription(StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer) {
        Subscription subscription = listenerContainer.register(
            StreamMessageListenerContainer.StreamReadRequest
                .builder(StreamOffset.create("account-activation-success-event", ReadOffset.lastConsumed()))
                .cancelOnError(t -> false)
                .consumer(Consumer.from("account-activation-success-event-group", "resource-server"))
                .autoAcknowledge(true)
                .build(), streamListener);
        return subscription;
    }

    @Bean
    public ApplicationRunner createConsumerGroupIfNotExists(RedisConnectionFactory redisConnectionFactory) {
        return args -> {
            log.info("Creating consumer group: [stream={}, group={}]", "account-activation-success-event", "account-activation-success-event-group");
            createConsumerGroupIfNotExists(redisConnectionFactory, "account-activation-success-event", "account-activation-success-event-group");
        };
    }

    private void createConsumerGroupIfNotExists(RedisConnectionFactory redisConnectionFactory, String streamKey, String groupName) {
        try {
            createConsumerGroup(redisConnectionFactory, streamKey, groupName);
        } catch (RedisSystemException redisSystemException) {
            log.error(redisSystemException.getMessage());
        }
    }

    private void createConsumerGroup(RedisConnectionFactory redisConnectionFactory, String streamKey, String groupName) {
        try {
            redisConnectionFactory.getConnection()
                .streamCommands()
                .xGroupCreate(streamKey.getBytes(), groupName, ReadOffset.from("0-0"), true);
        } catch (RedisSystemException redisSystemException) {
            log.warn(redisSystemException.getCause().getMessage());
        }
    }
}
