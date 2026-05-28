package com.bossawebsolutions.apigen.application.dto;

import java.time.LocalDate;

public record UserDashboardResponseDTO(
        Long id,
        String name,
        String email,
        String plan, // SOLO, SMALL, FULL
        String subscriptionStatus, // ACTIVE, CANCELED
        LocalDate subscriptionPaidUntil,
        Integer usedMachines,
        Integer maxMachines,
        Integer machinesTotal, // substitui apisGenerated
        String lastMachineRegistered, // substitui lastGeneration
        Boolean isPlanEligiblePremiumContact
) { }