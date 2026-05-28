package com.bossawebsolutions.apigen.application.service;

import com.bossawebsolutions.apigen.application.dto.UserDashboardResponseDTO;
import com.bossawebsolutions.apigen.application.utils.SecurityUtil;
import com.bossawebsolutions.apigen.domain.entity.Machine;
import com.bossawebsolutions.apigen.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SecurityUtil securityUtil;

    public UserDashboardResponseDTO getUserDashboardInfo() {
        User user = securityUtil.getUsuarioLogado();

        int maxMachines = switch (user.getPlan().name()) {
            case "SOLO" -> 1;
            case "SMALL" -> 15;
            case "FULL" -> 50;
            default -> 0;
        };

        boolean isPlanEligiblePremiumContact =
                user.getPlan().name().equals("SMALL") || user.getPlan().name().equals("FULL");

        int usedMachines = (int) user.getMachines().stream()
                .map(Machine::getMachineHash)
                .filter(h -> h != null && !h.isEmpty())
                .count();

        int machinesTotal = user.getMachines().size(); // substitui apisGenerated
        String lastMachineRegistered = user.getMachines().stream()
                .max(Comparator.comparing(Machine::getCreatedAt))
                .map(m -> m.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .orElse("Never");

        return new UserDashboardResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPlan().name(),
                user.getSubscriptionStatus().name(),
                LocalDate.of(user.getSubscriptionPaidUntil().getYear(),
                        user.getSubscriptionPaidUntil().getMonth(),
                        user.getSubscriptionPaidUntil().getDayOfMonth()),
                usedMachines,
                maxMachines,
                machinesTotal,
                lastMachineRegistered,
                isPlanEligiblePremiumContact
        );
    }
}