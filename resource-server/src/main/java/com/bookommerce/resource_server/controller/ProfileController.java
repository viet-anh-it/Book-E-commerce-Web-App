package com.bookommerce.resource_server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.resource_server.dto.request.UpdateMyProfileRequestDto;
import com.bookommerce.resource_server.dto.response.ApiSuccessResponse;
import com.bookommerce.resource_server.dto.response.GetMyProfileResponseDto;
import com.bookommerce.resource_server.service.ProfileService;
import com.bookommerce.resource_server.validation.annotation.ValidationOrder;

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

    @GetMapping("/me/profile/avatar")
    public ResponseEntity<String> getMyProfileAvatar() {
        return ResponseEntity.status(HttpStatus.OK)
            .body("/images/avatars/default/default-avatar.svg");
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<ApiSuccessResponse<Void>> updateProfile(
        @RequestBody @Validated(ValidationOrder.class)
        UpdateMyProfileRequestDto updateMyProfileRequestDto) {
        this.profileService.updateMyProfile(updateMyProfileRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Profile updated successfully")
                .build());    
    }
}
