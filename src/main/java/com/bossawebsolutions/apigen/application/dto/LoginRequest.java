package com.bossawebsolutions.apigen.application.dto;

public record LoginRequest(
        String email,
        String password,
        String machineHash
) {}