package com.bookommerce.resource_server.service.event;

import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import com.bookommerce.resource_server.dto.event.UserCreatedEvent;
import com.bookommerce.resource_server.entity.Profile;
import com.bookommerce.resource_server.service.ProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

//@formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserEventListener implements StreamListener<String, ObjectRecord<String, UserCreatedEvent>> {
    
    ProfileService profileService;

    @Override
    public void onMessage(ObjectRecord<String, UserCreatedEvent> rec) {
        UserCreatedEvent event = rec.getValue();
        // Tạo profile
        Profile profile = new Profile();
        profile.setEmail(event.getUsername());
        // ... set các field khác
        profileService.createProfile(profile);
        // Xác nhận (ack) nếu dùng consumer group
    }
}
