package com.bookommerce.auth_server.service.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bookommerce.auth_server.dto.event.UserCreatedEvent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

// @formatter:off
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UserEventProducer {

    @NonFinal
    @Value("${stream.key}")
    String streamKey;
    
    RedisTemplate<String, Object> redisTemplate;

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        ObjectRecord<String, UserCreatedEvent> rec = StreamRecords.newRecord()
            .ofObject(event)
            .withStreamKey(streamKey);
        redisTemplate.opsForStream().add(rec);
    }
}
