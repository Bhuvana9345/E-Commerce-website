package com.stylegpt.store.repository;

import com.stylegpt.store.entity.CartItem;
import com.stylegpt.store.entity.Product;
import com.stylegpt.store.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserOrderByCreatedAtDesc(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    Optional<CartItem> findByIdAndUser(Long id, User user);
    void deleteByUser(User user);
}
