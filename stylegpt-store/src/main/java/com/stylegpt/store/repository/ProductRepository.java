package com.stylegpt.store.repository;

import com.stylegpt.store.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
    List<Product> findByActiveTrueOrderByCreatedAtDesc();
    List<Product> findTop8ByActiveTrueOrderByCreatedAtDesc();
    List<Product> findTop8ByActiveTrueAndCategoryIgnoreCaseAndIdNot(String category, Long id);

    @Query("""
            select distinct p from Product p
            where p.active = true
              and (:search is null or lower(p.name) like lower(concat('%', :search, '%'))
                   or lower(p.description) like lower(concat('%', :search, '%'))
                   or lower(p.category) like lower(concat('%', :search, '%'))
                   or lower(p.brand) like lower(concat('%', :search, '%'))
                   or lower(p.tags) like lower(concat('%', :search, '%'))
                   or lower(p.color) like lower(concat('%', :search, '%')))
              and (:category is null or lower(p.category) = lower(:category))
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            """)
    Page<Product> search(@Param("search") String search,
                         @Param("category") String category,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);

    @Query("select distinct p.category from Product p where p.active = true order by p.category")
    List<String> findCategories();
}
