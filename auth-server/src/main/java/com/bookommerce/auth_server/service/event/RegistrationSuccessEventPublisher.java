package com.bookommerce.auth_server.service.event;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bookommerce.auth_server.dto.event.RegistrationSuccessEvent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

// @formatter:off
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationSuccessEventPublisher {

    @NonFinal
    @Value("${stream.key}")
    String streamKey;
    
    RedisTemplate<String, String> redisTemplate;

    public void publishRegistrationSuccessEvent(RegistrationSuccessEvent event) {
        Map<String, String> message = Map.of("username", event.getUsername());
        MapRecord<String, String, String> rec = 
            StreamRecords.newRecord().ofMap(message).withStreamKey(this.streamKey);
        this.redisTemplate.opsForStream().add(rec);
    }
}