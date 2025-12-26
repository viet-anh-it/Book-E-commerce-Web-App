package com.bookommerce.resource_server.dto.response;

import java.util.List;

//@formatter:off
public record GetCartResponseDto(
    long id,
    int itemCount,
    double totalPrice,
    List<CartItem> cartItems
) {
    public record CartItem(
        long id,
        double unitPrice,
        int quantity,
        double subtotal,
        Book book
    ){}

    public record Book(
        long id,
        String title,
        String author,
        String thumbnailUrlPath,
        double rating,
        double price
    ){}
}
