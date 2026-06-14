package com.stylegpt.store.repository;

import com.stylegpt.store.entity.Product;
import com.stylegpt.store.entity.User;
import com.stylegpt.store.entity.WishlistItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserOrderByCreatedAtDesc(User user);
    Optional<WishlistItem> findByUserAndProduct(User user, Product product);
    boolean existsByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);
}
