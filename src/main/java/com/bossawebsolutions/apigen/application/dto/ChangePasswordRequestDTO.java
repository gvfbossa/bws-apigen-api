package com.bossawebsolutions.apigen.application.dto;

public record ChangePasswordRequestDTO(
        String currentPassword,
        String newPassword
) {}