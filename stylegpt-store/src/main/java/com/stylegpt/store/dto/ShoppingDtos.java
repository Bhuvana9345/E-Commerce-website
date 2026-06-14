package com.stylegpt.store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ShoppingDtos {
    public record CartAddRequest(Long productId, int quantity) {}
    public record CartUpdateRequest(int quantity) {}
    public record CartItemResponse(Long id, ProductDtos.ProductResponse product, int quantity, BigDecimal lineTotal) {}
    public record CartResponse(List<CartItemResponse> items, BigDecimal total) {}
    public record WishlistResponse(Long id, ProductDtos.ProductResponse product) {}
    public record CheckoutRequest(String customerName, String phone, String address, String city, String state, String pincode) {}
    public record OrderItemResponse(Long productId, String productName, BigDecimal price, int quantity) {}
    public record OrderResponse(Long id, String status, BigDecimal totalAmount, String customerName, String phone,
                                String address, String city, String state, String pincode, String paymentMode,
                                LocalDateTime createdAt, List<OrderItemResponse> items) {}
}
