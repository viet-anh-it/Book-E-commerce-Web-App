package com.bookommerce.auth_server.service.event;

import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bookommerce.auth_server.dto.event.RegistrationSuccessEvent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// @formatter:off
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationSuccessEventPublisher {
    
    RedisTemplate<String, String> redisTemplate;

    public void publishRegistrationSuccessEvent(RegistrationSuccessEvent event) {
        Map<String, String> message = Map.of("email", event.getEmail());
        MapRecord<String, String, String> rec = 
            StreamRecords.newRecord().ofMap(message).withStreamKey("registration-success-event");
        this.redisTemplate.opsForStream().add(rec);
        log.info(">>>>>>>>>> Published registration success event. Registered email: {}", event.getEmail());
    }
}