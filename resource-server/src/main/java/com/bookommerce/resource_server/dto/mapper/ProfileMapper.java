package com.bookommerce.resource_server.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bookommerce.resource_server.dto.request.UpdateMyProfileRequestDto;
import com.bookommerce.resource_server.dto.response.GetMyProfileResponseDto;
import com.bookommerce.resource_server.entity.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    Profile toProfile(UpdateMyProfileRequestDto updateMyProfileRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateProfile(UpdateMyProfileRequestDto updateMyProfileRequestDto, @MappingTarget Profile profile);

    GetMyProfileResponseDto toGetMyProfileResponseDto(Profile profile);
}
