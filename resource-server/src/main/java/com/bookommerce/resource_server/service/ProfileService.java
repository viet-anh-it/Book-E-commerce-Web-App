package com.bookommerce.resource_server.service;

import org.springframework.stereotype.Service;

import com.bookommerce.resource_server.entity.Profile;
import com.bookommerce.resource_server.repository.ProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

//@formatter:off
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    ProfileRepository profileRepository;
    
    public void createProfile(Profile profile) {
        this.profileRepository.save(profile);
    }
}
