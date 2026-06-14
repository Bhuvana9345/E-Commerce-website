package com.stylegpt.store.controller;

import com.stylegpt.store.dto.ShoppingDtos.*;
import com.stylegpt.store.security.UserPrincipal;
import com.stylegpt.store.service.ShoppingService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShoppingController {
    private final ShoppingService shoppingService;

    public ShoppingController(ShoppingService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @GetMapping("/api/cart")
    public CartResponse cart(@AuthenticationPrincipal UserPrincipal principal) {
        return shoppingService.cart(principal.getUser());
    }

    @PostMapping("/api/cart")
    public CartResponse addCart(@AuthenticationPrincipal UserPrincipal principal, @RequestBody CartAddRequest request) {
        return shoppingService.addToCart(principal.getUser(), request);
    }

    @PutMapping("/api/cart/{itemId}")
    public CartResponse updateCart(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long itemId,
                                   @RequestBody CartUpdateRequest request) {
        return shoppingService.updateCart(principal.getUser(), itemId, request);
    }

    @DeleteMapping("/api/cart/{itemId}")
    public CartResponse removeCart(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long itemId) {
        return shoppingService.removeCart(principal.getUser(), itemId);
    }

    @GetMapping("/api/wishlist")
    public List<WishlistResponse> wishlist(@AuthenticationPrincipal UserPrincipal principal) {
        return shoppingService.wishlist(principal.getUser());
    }

    @PostMapping("/api/wishlist/{productId}")
    public List<WishlistResponse> toggleWishlist(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long productId) {
        return shoppingService.toggleWishlist(principal.getUser(), productId);
    }

    @DeleteMapping("/api/wishlist/{productId}")
    public void removeWishlist(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long productId) {
        shoppingService.removeWishlist(principal.getUser(), productId);
    }

    @PostMapping("/api/orders")
    public OrderResponse checkout(@AuthenticationPrincipal UserPrincipal principal, @RequestBody CheckoutRequest request) {
        return shoppingService.checkout(principal.getUser(), request);
    }

    @GetMapping("/api/orders/my")
    public List<OrderResponse> myOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return shoppingService.myOrders(principal.getUser());
    }
}
