package com.bookommerce.resource_server.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

import com.bookommerce.resource_server.dto.event.UserCreatedEvent;
import com.bookommerce.resource_server.service.event.UserEventListener;

// @formatter:off
@Configuration
public class EventConfig {

    @Bean
    public Subscription subscription(RedisConnectionFactory factory, UserEventListener listener) {
        String streamKey = "user-events";
        StreamMessageListenerContainerOptions<String, ObjectRecord<String, UserCreatedEvent>> options =
            StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofMillis(100))
                .targetType(UserCreatedEvent.class)
                .build();
        StreamMessageListenerContainer<String, ObjectRecord<String, UserCreatedEvent>> container = 
            StreamMessageListenerContainer.create(factory, options);
        Subscription sub = container.receive(StreamOffset.create(streamKey, ReadOffset.latest()), listener);
        container.start();
        return sub;
    }
}
