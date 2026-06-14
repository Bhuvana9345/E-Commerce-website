package com.stylegpt.store.controller;

import com.stylegpt.store.dto.ProductDtos.ProductResponse;
import com.stylegpt.store.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductResponse> products(@RequestParam(required = false) String search,
                                          @RequestParam(required = false) String category,
                                          @RequestParam(required = false) BigDecimal minPrice,
                                          @RequestParam(required = false) BigDecimal maxPrice,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "12") int size) {
        return productService.search(search, category, minPrice, maxPrice, page, size);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return productService.categories();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }

    @GetMapping("/{id}/related")
    public List<ProductResponse> related(@PathVariable Long id) {
        return productService.related(id);
    }
}
