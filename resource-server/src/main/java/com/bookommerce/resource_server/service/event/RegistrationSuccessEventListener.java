package com.bookommerce.resource_server.service.event;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import com.bookommerce.resource_server.dto.event.RegistrationSuccessEvent;
import com.bookommerce.resource_server.entity.Profile;
import com.bookommerce.resource_server.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

//@formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationSuccessEventListener implements StreamListener<String, MapRecord<String, String, String>> {

    ProfileService profileService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        RegistrationSuccessEvent event =
            objectMapper.convertValue(message.getValue(), RegistrationSuccessEvent.class);
        Profile profile = new Profile();
        profile.setEmail(event.getUsername());
        this.profileService.createProfile(profile);
    }
}
