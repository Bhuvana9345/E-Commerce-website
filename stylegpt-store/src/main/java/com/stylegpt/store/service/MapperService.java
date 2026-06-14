package com.stylegpt.store.service;

import com.stylegpt.store.dto.ProductDtos.ProductResponse;
import com.stylegpt.store.dto.ShoppingDtos.CartItemResponse;
import com.stylegpt.store.dto.ShoppingDtos.OrderItemResponse;
import com.stylegpt.store.dto.ShoppingDtos.OrderResponse;
import com.stylegpt.store.dto.ShoppingDtos.WishlistResponse;
import com.stylegpt.store.dto.UserResponse;
import com.stylegpt.store.entity.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MapperService {
    public UserResponse user(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole());
    }

    public ProductResponse product(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getCategory(), p.getBrand(),
                p.getColor(), p.getSizeOptions(), p.getPrice(), p.getDiscountPrice(), p.getStock(), p.getTags(), p.getImageUrl(), p.isActive());
    }

    public CartItemResponse cartItem(CartItem item) {
        BigDecimal line = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(item.getId(), product(item.getProduct()), item.getQuantity(), line);
    }

    public WishlistResponse wishlist(WishlistItem item) {
        return new WishlistResponse(item.getId(), product(item.getProduct()));
    }

    public OrderResponse order(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProduct().getId(), i.getProductName(), i.getPrice(), i.getQuantity()))
                .toList();
        return new OrderResponse(order.getId(), order.getStatus().name(), order.getTotalAmount(), order.getCustomerName(),
                order.getPhone(), order.getAddress(), order.getCity(), order.getState(), order.getPincode(),
                order.getPaymentMode(), order.getCreatedAt(), items);
    }
}
