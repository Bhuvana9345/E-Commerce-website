package com.stylegpt.store.controller;

import com.stylegpt.store.dto.ProductDtos.ProductRequest;
import com.stylegpt.store.dto.ProductDtos.ProductResponse;
import com.stylegpt.store.dto.ShoppingDtos.OrderResponse;
import com.stylegpt.store.dto.UserResponse;
import com.stylegpt.store.repository.UserRepository;
import com.stylegpt.store.service.MapperService;
import com.stylegpt.store.service.ProductService;
import com.stylegpt.store.service.ShoppingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ProductService productService;
    private final ShoppingService shoppingService;
    private final UserRepository userRepository;
    private final MapperService mapper;

    public AdminController(ProductService productService, ShoppingService shoppingService,
                           UserRepository userRepository, MapperService mapper) {
        this.productService = productService;
        this.shoppingService = shoppingService;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @PostMapping("/products")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    @PostMapping("/products/{id}/image")
    public ProductResponse uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        return productService.uploadImage(id, image);
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders() {
        return shoppingService.allOrders();
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return userRepository.findAll().stream().map(mapper::user).toList();
    }
}
