package com.bookommerce.resource_server.service;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bookommerce.resource_server.dto.mapper.ProfileMapper;
import com.bookommerce.resource_server.dto.request.UpdateMyProfileRequestDto;
import com.bookommerce.resource_server.dto.response.GetMyProfileResponseDto;
import com.bookommerce.resource_server.entity.Profile;
import com.bookommerce.resource_server.repository.ProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

//@formatter:off
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    ProfileRepository profileRepository;
    ProfileMapper profileMapper;

    public GetMyProfileResponseDto getMyProfile() {
        String currentAuthenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Profile> optionalProfile = this.profileRepository.findByEmail(currentAuthenticatedUsername);
        Profile profile = null;
        if (optionalProfile.isEmpty()) {
            profile = new Profile();
            profile.setEmail(currentAuthenticatedUsername);
            this.profileRepository.save(profile);
            return this.profileMapper.toGetMyProfileResponseDto(profile);
        }
        profile = optionalProfile.get();
        return this.profileMapper.toGetMyProfileResponseDto(profile);
    }
    
    public void createProfile(Profile profile) {
        this.profileRepository.save(profile);
    }

    public void updateMyProfile(UpdateMyProfileRequestDto updateMyProfileRequestDto) {
        String currentAuthenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Profile> optionalProfile = this.profileRepository.findByEmail(currentAuthenticatedUsername);

        Profile profile = null;
        if (optionalProfile.isEmpty()) {
            profile = this.profileMapper.toProfile(updateMyProfileRequestDto);
            profile.setEmail(currentAuthenticatedUsername);
        } else {
            profile = optionalProfile.get();
            this.profileMapper.updateProfile(updateMyProfileRequestDto, profile);
        }
        this.profileRepository.save(profile);
    }
}
