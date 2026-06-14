package com.stylegpt.store.controller;

import com.stylegpt.store.dto.AuthDtos.*;
import com.stylegpt.store.dto.UserResponse;
import com.stylegpt.store.security.UserPrincipal;
import com.stylegpt.store.service.AuthService;
import com.stylegpt.store.service.MapperService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final MapperService mapper;

    public AuthController(AuthService authService, MapperService mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return mapper.user(principal.getUser());
    }
}
