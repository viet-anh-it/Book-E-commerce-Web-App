package com.bookommerce.auth_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookommerce.auth_server.dto.request.RegistrationRequestDto;
import com.bookommerce.auth_server.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(RegistrationRequestDto registrationRequestDto);
}
