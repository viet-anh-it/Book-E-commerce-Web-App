package com.bookommerce.resource_server.service;

import org.springframework.stereotype.Service;

import com.bookommerce.resource_server.entity.Profile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

//@formatter:off
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    
    public void createProfile(Profile profile) {
        log.debug("User profile created: {}", profile.getEmail());
    }
}
