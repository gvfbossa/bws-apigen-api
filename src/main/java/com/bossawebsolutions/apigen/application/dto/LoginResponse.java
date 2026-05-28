package com.bossawebsolutions.apigen.application.dto;

public record LoginResponse(
        String token,
        long expiresAt,
        String email
) {}