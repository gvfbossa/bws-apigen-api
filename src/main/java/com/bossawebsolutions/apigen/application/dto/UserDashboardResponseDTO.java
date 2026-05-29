package com.bossawebsolutions.apigen.application.dto;

public record UserDashboardResponseDTO(
        Long id,
        String name,
        String email,
        String plan,
        String licenseStatus,
        Integer usedMachines,
        Integer maxMachines,
        Integer machinesTotal,
        String lastMachineRegistered,
        Boolean isPlanEligiblePremiumContact
) { }