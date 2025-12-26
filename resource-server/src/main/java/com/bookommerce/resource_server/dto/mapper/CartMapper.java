package com.bookommerce.resource_server.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookommerce.resource_server.dto.response.GetCartResponseDto;
import com.bookommerce.resource_server.entity.Cart;

@Mapper(componentModel = "spring")
public interface CartMapper {

    GetCartResponseDto toGetCartResponseDto(Cart cart);

    GetCartResponseDto.CartItem toGetCartResponseDto_CartItem(com.bookommerce.resource_server.entity.CartItem cartItem);

    @Mapping(target = "rating", source = "book.ratingStatistic.averagePoint")
    GetCartResponseDto.Book toGetCartResponseDto_Book(com.bookommerce.resource_server.entity.Book book);
}
