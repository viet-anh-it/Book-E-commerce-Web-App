package com.bookommerce.auth_server.service.event;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import com.bookommerce.auth_server.dto.event.RegistrationSuccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

//@formatter:off
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationSuccessEventListener implements StreamListener<String, MapRecord<String, String, String>> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        RegistrationSuccessEvent event =
            objectMapper.convertValue(message.getValue(), RegistrationSuccessEvent.class);
        log.info(">>>>>>>>>> Sent email to: {}", event.getEmail());
    }
}
