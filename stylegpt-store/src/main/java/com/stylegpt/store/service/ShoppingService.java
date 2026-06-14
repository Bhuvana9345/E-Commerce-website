package com.stylegpt.store.service;

import com.stylegpt.store.dto.ShoppingDtos.*;
import com.stylegpt.store.entity.*;
import com.stylegpt.store.exception.AppException;
import com.stylegpt.store.repository.*;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ShoppingService {
    private final CartItemRepository cartRepository;
    private final WishlistItemRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final MapperService mapper;

    public ShoppingService(CartItemRepository cartRepository, WishlistItemRepository wishlistRepository,
                           ProductRepository productRepository, OrderRepository orderRepository, MapperService mapper) {
        this.cartRepository = cartRepository;
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    public CartResponse cart(User user) {
        List<CartItemResponse> items = cartRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(mapper::cartItem).toList();
        BigDecimal total = items.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(items, total);
    }

    public CartResponse addToCart(User user, CartAddRequest request) {
        Product product = activeProduct(request.productId());
        int qty = Math.max(1, request.quantity());
        if (product.getStock() < qty) {
            throw new AppException("Only " + product.getStock() + " items available", HttpStatus.BAD_REQUEST);
        }
        CartItem item = cartRepository.findByUserAndProduct(user, product).orElseGet(CartItem::new);
        item.setUser(user);
        item.setProduct(product);
        int newQty = item.getId() == null ? qty : item.getQuantity() + qty;
        if (newQty > product.getStock()) {
            throw new AppException("Cart quantity exceeds available stock", HttpStatus.BAD_REQUEST);
        }
        item.setQuantity(newQty);
        cartRepository.save(item);
        return cart(user);
    }

    public CartResponse updateCart(User user, Long itemId, CartUpdateRequest request) {
        CartItem item = cartRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new AppException("Cart item not found", HttpStatus.NOT_FOUND));
        if (request.quantity() < 1) {
            cartRepository.delete(item);
            return cart(user);
        }
        if (request.quantity() > item.getProduct().getStock()) {
            throw new AppException("Quantity exceeds stock", HttpStatus.BAD_REQUEST);
        }
        item.setQuantity(request.quantity());
        cartRepository.save(item);
        return cart(user);
    }

    public CartResponse removeCart(User user, Long itemId) {
        CartItem item = cartRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new AppException("Cart item not found", HttpStatus.NOT_FOUND));
        cartRepository.delete(item);
        return cart(user);
    }

    public List<WishlistResponse> wishlist(User user) {
        return wishlistRepository.findByUserOrderByCreatedAtDesc(user).stream().map(mapper::wishlist).toList();
    }

    public List<WishlistResponse> toggleWishlist(User user, Long productId) {
        Product product = activeProduct(productId);
        wishlistRepository.findByUserAndProduct(user, product).ifPresentOrElse(
                wishlistRepository::delete,
                () -> {
                    WishlistItem item = new WishlistItem();
                    item.setUser(user);
                    item.setProduct(product);
                    wishlistRepository.save(item);
                });
        return wishlist(user);
    }

    @Transactional
    public void removeWishlist(User user, Long productId) {
        Product product = activeProduct(productId);
        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    @Transactional
    public OrderResponse checkout(User user, CheckoutRequest request) {
        List<CartItem> items = cartRepository.findByUserOrderByCreatedAtDesc(user);
        if (items.isEmpty()) {
            throw new AppException("Cart is empty", HttpStatus.BAD_REQUEST);
        }
        for (CartItem item : items) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                throw new AppException(item.getProduct().getName() + " has insufficient stock", HttpStatus.BAD_REQUEST);
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(request.customerName());
        order.setPhone(request.phone());
        order.setAddress(request.address());
        order.setCity(request.city());
        order.setState(request.state());
        order.setPincode(request.pincode());

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : items) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            order.getItems().add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        cartRepository.deleteByUser(user);
        return mapper.order(saved);
    }

    public List<OrderResponse> myOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream().map(mapper::order).toList();
    }

    public List<OrderResponse> allOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(mapper::order).toList();
    }

    private Product activeProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        if (!product.isActive()) {
            throw new AppException("Product not available", HttpStatus.BAD_REQUEST);
        }
        return product;
    }
}
