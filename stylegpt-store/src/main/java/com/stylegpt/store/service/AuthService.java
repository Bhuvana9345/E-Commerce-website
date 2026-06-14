package com.stylegpt.store.service;

import com.stylegpt.store.dto.AuthDtos.AuthResponse;
import com.stylegpt.store.dto.AuthDtos.LoginRequest;
import com.stylegpt.store.dto.AuthDtos.RegisterRequest;
import com.stylegpt.store.entity.Role;
import com.stylegpt.store.entity.User;
import com.stylegpt.store.exception.AppException;
import com.stylegpt.store.repository.UserRepository;
import com.stylegpt.store.security.JwtService;
import com.stylegpt.store.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MapperService mapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService, MapperService mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException("Email already registered", HttpStatus.BAD_REQUEST);
        }
        String phone = normalizePhone(request.phone());
        if (userRepository.existsByPhone(phone)) {
            throw new AppException("Phone already registered", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        String token = jwtService.generateToken(new UserPrincipal(user));
        return new AuthResponse(token, mapper.user(user));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Invalid login", HttpStatus.UNAUTHORIZED));
        String token = jwtService.generateToken(new UserPrincipal(user));
        return new AuthResponse(token, mapper.user(user));
    }

    private String normalizePhone(String phone) {
        String cleaned = phone == null ? "" : phone.replaceAll("[^0-9]", "");
        if (cleaned.length() < 10) {
            throw new AppException("Enter a valid phone number", HttpStatus.BAD_REQUEST);
        }
        return cleaned.substring(cleaned.length() - 10);
    }
}
