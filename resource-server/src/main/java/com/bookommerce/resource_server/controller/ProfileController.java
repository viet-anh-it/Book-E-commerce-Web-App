package com.bookommerce.resource_server.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.resource_server.dto.request.UpdateMyProfileAvatarRequestDto;
import com.bookommerce.resource_server.dto.request.UpdateMyProfileRequestDto;
import com.bookommerce.resource_server.dto.response.ApiSuccessResponse;
import com.bookommerce.resource_server.dto.response.GetMyProfileResponseDto;
import com.bookommerce.resource_server.service.ProfileService;
import com.bookommerce.resource_server.validation.annotation.ValidationOrder;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {

    ProfileService profileService;

    @GetMapping("/me/profile")
    public ResponseEntity<ApiSuccessResponse<GetMyProfileResponseDto>> getMyProfile() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiSuccessResponse.<GetMyProfileResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("Profile retrieved successfully")
                .data(this.profileService.getMyProfile())
                .build()); 
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<ApiSuccessResponse<Void>> updateMyProfile(
        @RequestBody @Validated(ValidationOrder.class)
        UpdateMyProfileRequestDto updateMyProfileRequestDto) {
        this.profileService.updateMyProfile(updateMyProfileRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Profile updated successfully")
                .build());    
    }

    @PatchMapping(path = "/me/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiSuccessResponse<String>> updateMyProfileAvatar(
        @ModelAttribute @Valid
        UpdateMyProfileAvatarRequestDto updateMyProfileAvatarRequestDto) throws IOException {
        String avatarUrlPath = this.profileService.updateMyProfileAvatar(updateMyProfileAvatarRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiSuccessResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Profile avatar updated successfully")
                .data(avatarUrlPath)
                .build());    
    }
}
