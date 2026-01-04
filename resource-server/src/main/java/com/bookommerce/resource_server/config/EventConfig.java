package com.bookommerce.resource_server.config;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

// @formatter:off
@Slf4j
@Configuration
@SuppressWarnings("null")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventConfig {
    
    @NonFinal
    @Value("${stream.group}")
    String group;

    @NonFinal
    @Value("${stream.key}")
    String streamKey;

    StreamListener<String, MapRecord<String, String, String>> streamListener;

    @Bean
    public Subscription subscription(RedisConnectionFactory redisConnectionFactory) {
        log.info("Creating consumer group: [stream={}, group={}]", streamKey, group);
        createConsumerGroupIfNotExists(redisConnectionFactory, streamKey, group);

        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainerOptions.builder()
                        .batchSize(1)
                        .pollTimeout(Duration.ofSeconds(1))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer =
            StreamMessageListenerContainer.create(redisConnectionFactory, options);

        Subscription subscription = listenerContainer.register(
                StreamMessageListenerContainer.StreamReadRequest
                        .builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                        .cancelOnError(t -> false)
                        .consumer(Consumer.from(group, UUID.randomUUID().toString()))
                        .autoAcknowledge(true)
                        .build(), streamListener);

        log.info("Starting listener container: [stream={}, group={}]", streamKey, group);
        listenerContainer.start();

        return subscription;
    }

    private void createConsumerGroupIfNotExists(RedisConnectionFactory redisConnectionFactory, String streamKey, String groupName) {
        try {
            try {
                redisConnectionFactory.getConnection()
                    .streamCommands()
                    .xGroupCreate(streamKey.getBytes(), groupName, ReadOffset.from("0-0"), true);
            } catch (RedisSystemException exception) {
                log.warn(exception.getCause().getMessage());
            }
        } catch (RedisSystemException ex) {
            log.error(ex.getMessage());
        }
    }
}
