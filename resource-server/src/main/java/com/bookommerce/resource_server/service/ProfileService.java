package com.bookommerce.resource_server.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bookommerce.resource_server.dto.mapper.ProfileMapper;
import com.bookommerce.resource_server.dto.request.UpdateMyProfileAvatarRequestDto;
import com.bookommerce.resource_server.dto.request.UpdateMyProfileRequestDto;
import com.bookommerce.resource_server.dto.response.GetMyProfileResponseDto;
import com.bookommerce.resource_server.entity.Profile;
import com.bookommerce.resource_server.repository.ProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

//@formatter:off
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    @NonFinal
    @Value("${upload.image.avatar}")
    String AVATAR_IMAGE_UPLOAD_DIR;

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
    
    @Transactional
    public void createProfile(Profile profile) {
        this.profileRepository.save(profile);
    }

    @Transactional
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

    public String updateMyProfileAvatar(UpdateMyProfileAvatarRequestDto updateMyProfileAvatarRequestDto) throws IOException {
        String currentAuthenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile profile = this.profileRepository.findByEmail(currentAuthenticatedUsername).get();
        MultipartFile avatarImageFile = updateMyProfileAvatarRequestDto.image();
        if (avatarImageFile != null && !avatarImageFile.isEmpty()) {
            String oldAvatarFileName = StringUtils.getFilename(profile.getAvatarUrlPath());
            if (oldAvatarFileName != null) {
                Files.delete(Paths.get(AVATAR_IMAGE_UPLOAD_DIR, oldAvatarFileName));
            }
            String cleanPath = StringUtils.cleanPath(avatarImageFile.getOriginalFilename());
            String fileExtension = StringUtils.getFilenameExtension(cleanPath);
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path destination = Paths.get(AVATAR_IMAGE_UPLOAD_DIR, uniqueFileName);
            avatarImageFile.transferTo(destination);
            profile.setAvatarUrlPath("/images/avatars/" + uniqueFileName);

            try {
                this.profileRepository.save(profile);
            } catch (Exception exception) {
                if (destination != null) {
                    Files.delete(destination);
                }
                throw exception;
            }
        }
        return profile.getAvatarUrlPath();
    }
}
