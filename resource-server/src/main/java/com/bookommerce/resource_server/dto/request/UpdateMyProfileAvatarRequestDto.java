package com.bookommerce.resource_server.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.bookommerce.resource_server.validation.annotation.ValidUpdateBookImage;

// @formatter:off
@ValidUpdateBookImage
public record UpdateMyProfileAvatarRequestDto(
    String imageUrlPath,
    MultipartFile image
) {}
