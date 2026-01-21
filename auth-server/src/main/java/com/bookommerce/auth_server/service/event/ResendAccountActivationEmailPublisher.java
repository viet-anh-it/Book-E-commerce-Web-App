package com.bookommerce.auth_server.service.event;

import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bookommerce.auth_server.dto.event.ResendAccountActivationEmailEvent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResendAccountActivationEmailPublisher {
    
    RedisTemplate<String, String> redisTemplate;

    public void publishResendAccountActivationEmailEvent(ResendAccountActivationEmailEvent event) {
        Map<String, String> message = Map.of("email", event.getEmail());
        MapRecord<String, String, String> rec = 
            StreamRecords.newRecord().ofMap(message).withStreamKey("resend-account-activation-email-event");
        this.redisTemplate.opsForStream().add(rec);
    }
}
