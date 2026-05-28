package com.bossawebsolutions.apigen.application.dto;

import com.bossawebsolutions.apigen.domain.Plan;

public record RegisterRequest(
        String name,
        String email,
        String password,
        Plan plan,
        String paymentMethodId
) {}