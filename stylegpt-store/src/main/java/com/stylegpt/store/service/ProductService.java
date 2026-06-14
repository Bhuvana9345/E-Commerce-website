package com.stylegpt.store.service;

import com.stylegpt.store.dto.ProductDtos.ProductRequest;
import com.stylegpt.store.dto.ProductDtos.ProductResponse;
import com.stylegpt.store.entity.Product;
import com.stylegpt.store.exception.AppException;
import com.stylegpt.store.repository.ProductRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final MapperService mapper;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public ProductService(ProductRepository productRepository, MapperService mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    public Page<ProductResponse> search(String search, String category, BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        String s = blankToNull(search);
        String c = blankToNull(category);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.search(s, c, minPrice, maxPrice, pageRequest).map(mapper::product);
    }

    public List<String> categories() {
        return productRepository.findCategories();
    }

    public Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
    }

    public ProductResponse get(Long id) {
        Product product = getEntity(id);
        if (!product.isActive()) {
            throw new AppException("Product not available", HttpStatus.NOT_FOUND);
        }
        return mapper.product(product);
    }

    public List<ProductResponse> related(Long id) {
        Product product = getEntity(id);
        return productRepository.findTop8ByActiveTrueAndCategoryIgnoreCaseAndIdNot(product.getCategory(), id)
                .stream().map(mapper::product).toList();
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return mapper.product(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getEntity(id);
        apply(product, request);
        return mapper.product(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = getEntity(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public ProductResponse uploadImage(Long id, MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException("Image file is required", HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new AppException("Only image files are allowed", HttpStatus.BAD_REQUEST);
        }
        Product product = getEntity(id);
        try {
            Path dir = Path.of(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String original = file.getOriginalFilename() == null ? "product.jpg" : file.getOriginalFilename();
            String extension = original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".jpg";
            String filename = UUID.randomUUID() + extension;
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            product.setImageUrl("/uploads/products/" + filename);
            return mapper.product(productRepository.save(product));
        } catch (IOException ex) {
            throw new AppException("Image upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setBrand(request.brand());
        product.setColor(request.color());
        product.setSizeOptions(request.sizeOptions());
        product.setPrice(request.price());
        product.setDiscountPrice(request.discountPrice());
        product.setStock(request.stock());
        product.setTags(request.tags());
        product.setImageUrl(request.imageUrl());
        product.setActive(request.active());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
