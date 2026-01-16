package com.bookommerce.resource_server.service.event;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookommerce.resource_server.dto.event.AccountActivationSuccessEvent;
import com.bookommerce.resource_server.entity.Profile;
import com.bookommerce.resource_server.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// @formatter:off
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountActivationSuccessEventHandler implements StreamListener<String, MapRecord<String, String, String>> {

    ProfileService profileService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void onMessage(MapRecord<String, String, String> message) {
        AccountActivationSuccessEvent event =
            objectMapper.convertValue(message.getValue(), AccountActivationSuccessEvent.class);
        Profile profile = new Profile();
        profile.setEmail(event.getEmail());
        this.profileService.createProfile(profile);
        log.info(">>>>>>>>>> Profile created for user: {}", event.getEmail());
    }
}
