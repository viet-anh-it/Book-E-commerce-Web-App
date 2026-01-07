package com.bookommerce.resource_server.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.resource_server.dto.request.AddToCartRequestDto;
import com.bookommerce.resource_server.dto.request.CartItemIdRequestDto;
import com.bookommerce.resource_server.dto.request.UpdateCartItemQuantityRequestDto;
import com.bookommerce.resource_server.dto.response.ApiSuccessResponse;
import com.bookommerce.resource_server.dto.response.GetCartResponseDto;
import com.bookommerce.resource_server.service.CartService;

import jakarta.validation.Valid;

// @formatter:off
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    @GetMapping("/carts")
    public ResponseEntity<ApiSuccessResponse<GetCartResponseDto>> getCart() {
        return ResponseEntity.ok(ApiSuccessResponse.<GetCartResponseDto>builder()
            .status(HttpStatus.OK.value())
            .message("Cart retrieved successfully")
            .data(this.cartService.getCart())
            .build());
    }

    
    @PostMapping("/carts/items")
    public ResponseEntity<ApiSuccessResponse<Void>> addToCart(
        @RequestBody @Valid AddToCartRequestDto addToCartRequestDto) {
        this.cartService.addToCart(addToCartRequestDto);
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .message("Added to cart successfully")
            .build());
    }

    @PatchMapping("/carts/items/{cartItemId}")
    public ResponseEntity<ApiSuccessResponse<Void>> updateCartItemQuantity(
        @Valid CartItemIdRequestDto cartItemIdRequestDto,
        @RequestBody @Valid UpdateCartItemQuantityRequestDto updateCartItemQuantityRequestDto) {
        this.cartService.updateCartItemQuantity(cartItemIdRequestDto, updateCartItemQuantityRequestDto);
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .message("Cart item quantity updated successfully")
            .build());
    }

    @DeleteMapping("/carts/items/{cartItemId}")
    public ResponseEntity<ApiSuccessResponse<Void>> removeItemFromCart(
        @Valid CartItemIdRequestDto cartItemIdRequestDto) {
        this.cartService.removeItemFromCart(cartItemIdRequestDto);
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .message("Removed from cart successfully")
            .build());
    }
}
