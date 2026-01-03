package com.bookommerce.resource_server.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.bookommerce.resource_server.dto.mapper.CartMapper;
import com.bookommerce.resource_server.dto.request.AddToCartRequestDto;
import com.bookommerce.resource_server.dto.request.CartItemIdRequestDto;
import com.bookommerce.resource_server.dto.request.UpdateCartItemQuantityRequestDto;
import com.bookommerce.resource_server.dto.response.GetCartResponseDto;
import com.bookommerce.resource_server.entity.Book;
import com.bookommerce.resource_server.entity.Cart;
import com.bookommerce.resource_server.entity.CartItem;
import com.bookommerce.resource_server.exception.BookNotFoundException;
import com.bookommerce.resource_server.exception.CartItemNotFoundException;
import com.bookommerce.resource_server.exception.EmptyCartException;
import com.bookommerce.resource_server.exception.StockNotEnoughException;
import com.bookommerce.resource_server.repository.BookRepository;
import com.bookommerce.resource_server.repository.CartItemRepository;
import com.bookommerce.resource_server.repository.CartRepository;
import com.bookommerce.resource_server.utils.ValidationUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    BookRepository bookRepository;
    CartItemRepository cartItemRepository;
    CartMapper cartMapper;

    @Nullable
    public GetCartResponseDto getCart() {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = this.cartRepository.findByUserEmail(authenticatedUserEmail).orElse(null);
        return this.cartMapper.toGetCartResponseDto(cart);
    }

    //@formatter:off
    @Transactional
    public void addToCart(AddToCartRequestDto addToCartRequestDto) {
        Optional<Book> optionalBook = this.bookRepository.findById(addToCartRequestDto.bookId());
        if (optionalBook.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(addToCartRequestDto, "addToCartRequestDto", "bookId", "2:Book not found");
            throw new BookNotFoundException(bindingResult);
        } else if (addToCartRequestDto.quantity() > optionalBook.get().getStock()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(addToCartRequestDto, "addToCartRequestDto", "quantity", "2:Stock not enough");
            throw new StockNotEnoughException(bindingResult);
        }

        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = null;
        Optional<Cart> optionalCart = this.cartRepository.findByUserEmail(authenticatedUserEmail);
        if (optionalCart.isEmpty()) {
            cart = new Cart();
            cart.setUserEmail(authenticatedUserEmail);
            this.cartRepository.save(cart);
        } else {
            cart = optionalCart.get();
        }

        Book book = optionalBook.get();
        CartItem cartItem = null;
        Optional<CartItem> optionalCartItem = this.cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId());
        if (optionalCartItem.isEmpty()) {
            cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setCart(cart);
            cartItem.setUnitPrice(book.getPrice());
        } else {
            cartItem = optionalCartItem.get();
        }

        cartItem.setQuantity(cartItem.getQuantity() + addToCartRequestDto.quantity());
        cartItem.setSubtotal(cartItem.getSubtotal() + (cartItem.getUnitPrice() * addToCartRequestDto.quantity()));

        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getUnitPrice() * addToCartRequestDto.quantity()));
        cart.setItemCount(cart.getItemCount() + addToCartRequestDto.quantity());

        this.cartRepository.save(cart);
        this.cartItemRepository.save(cartItem);

        book.setStock(book.getStock() - addToCartRequestDto.quantity());
        this.bookRepository.save(book);
    }

    @Transactional
    public void removeItemFromCart(CartItemIdRequestDto cartItemIdRequestDto) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Cart> optionalCart = this.cartRepository.findByUserEmail(authenticatedUserEmail);
        if(optionalCart.isEmpty() // the authenticated user has no cart
            || optionalCart.get().getCartItems() == null // the authenticated user's cart has no items
            || optionalCart.get().getCartItems().isEmpty()) { // the authenticated user's cart has no items
            BindingResult bindingResult =
                ValidationUtils.createBindingResult(cartItemIdRequestDto, "cartItemIdRequestDto", "cartItemId", "2:Found no item with ID: " + cartItemIdRequestDto.cartItemId() + " in cart");
            throw new EmptyCartException(bindingResult);
        }
        Cart cart = optionalCart.get();
        Set<CartItem> cartItems = cart.getCartItems();
        CartItem cartItem = cartItems.stream()
            .filter(item -> item.getId() == cartItemIdRequestDto.cartItemId())
            .findFirst()
            .orElseThrow(() -> {
                BindingResult bindingResult =
                    ValidationUtils.createBindingResult(cartItemIdRequestDto, "cartItemIdRequestDto", "cartItemId", "3:Found no item with ID: " + cartItemIdRequestDto.cartItemId() + " in cart");
                throw new CartItemNotFoundException(bindingResult);
            });
        Book book = cartItem.getBook();
        
        book.setStock(book.getStock() + cartItem.getQuantity());
        this.bookRepository.save(book);

        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getSubtotal());
        cart.setItemCount(cart.getItemCount() - cartItem.getQuantity());
        this.cartRepository.save(cart);
        
        this.cartItemRepository.delete(cartItem);
    }

    public void updateCartItemQuantity(
        CartItemIdRequestDto cartItemIdRequestDto, 
        UpdateCartItemQuantityRequestDto updateCartItemQuantityRequestDto) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Cart> optionalCart = this.cartRepository.findByUserEmail(authenticatedUserEmail);
        if(optionalCart.isEmpty() // the authenticated user has no cart
            || optionalCart.get().getCartItems() == null // the authenticated user's cart has no items
            || optionalCart.get().getCartItems().isEmpty()) { // the authenticated user's cart has no items
            BindingResult bindingResult =
                ValidationUtils.createBindingResult(cartItemIdRequestDto, "cartItemIdRequestDto", "cartItemId", "2:Found no item with ID: " + cartItemIdRequestDto.cartItemId() + " in cart");
            throw new EmptyCartException(bindingResult);
        }
        Cart cart = optionalCart.get();
        Set<CartItem> cartItems = cart.getCartItems();
        CartItem cartItem = cartItems.stream()
            .filter(item -> item.getId() == cartItemIdRequestDto.cartItemId())
            .findFirst()
            .orElseThrow(() -> {
                BindingResult bindingResult =
                    ValidationUtils.createBindingResult(cartItemIdRequestDto, "cartItemIdRequestDto", "cartItemId", "3:Found no item with ID: " + cartItemIdRequestDto.cartItemId() + " in cart");
                throw new CartItemNotFoundException(bindingResult);
            });
        int currentQuantity = cartItem.getQuantity();
        int newQuantity = updateCartItemQuantityRequestDto.quantity();
        if(newQuantity < currentQuantity) {
            int quantityDifference = currentQuantity - newQuantity;
            Book book = cartItem.getBook();
            book.setStock(book.getStock() + quantityDifference);
            this.bookRepository.save(book);
            cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getUnitPrice() * quantityDifference));
            cart.setItemCount(cart.getItemCount() - quantityDifference);
            this.cartRepository.save(cart);
            cartItem.setQuantity(newQuantity);
            cartItem.setSubtotal(cartItem.getSubtotal() - (cartItem.getUnitPrice() * quantityDifference));
            this.cartItemRepository.save(cartItem);
        } else if(newQuantity > currentQuantity) {
            int quantityDifference = newQuantity - currentQuantity;
            Book book = cartItem.getBook();
            if(book.getStock() < quantityDifference) {
                BindingResult bindingResult =
                    ValidationUtils.createBindingResult(updateCartItemQuantityRequestDto, "updateCartItemQuantityRequestDto", "quantity", "1:Stock not enough");
                throw new StockNotEnoughException(bindingResult);
            }
            book.setStock(book.getStock() - quantityDifference);
            this.bookRepository.save(book);
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getUnitPrice() * quantityDifference));
            cart.setItemCount(cart.getItemCount() + quantityDifference);
            this.cartRepository.save(cart);
            cartItem.setQuantity(newQuantity);
            cartItem.setSubtotal(cartItem.getSubtotal() + (cartItem.getUnitPrice() * quantityDifference));
            this.cartItemRepository.save(cartItem);
        }
    }
}
