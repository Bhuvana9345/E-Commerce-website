package com.stylegpt.store.dto;

import com.stylegpt.store.entity.Role;

public record UserResponse(Long id, String name, String email, String phone, Role role) {}
