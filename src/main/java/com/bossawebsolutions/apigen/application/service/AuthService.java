package com.bossawebsolutions.apigen.application.service;

import com.bossawebsolutions.apigen.application.service.stripe.PaymentService;
import com.bossawebsolutions.apigen.application.service.websocket.WebSocketService;
import com.bossawebsolutions.apigen.config.JwtService;
import com.bossawebsolutions.apigen.domain.Plan;
import com.bossawebsolutions.apigen.domain.LicenceStatus;
import com.bossawebsolutions.apigen.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final MachineService machineService;
    private final PaymentService paymentService;
    private final WebSocketService webSocketService;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public void register(String name, String email, String password, Plan plan, String paymentMethodId) {
        email = email.toLowerCase();

        if (userService.existsByEmail(email)) {
            throw new RuntimeException("Email already in use");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));
        user.setPlan(plan);
        user.setActive(false);

        userService.save(user);

        if (!Plan.ADMIN.equals(plan)) {
            if (paymentMethodId == null || paymentMethodId.isBlank()) {
                throw new RuntimeException("Payment method required for plan " + plan);
            }
            paymentService.processOneTimePayment(user, paymentMethodId);
            user.setActive(true);
            userService.save(user);

            webSocketService.notifyLicenceStatus(user.getId(), user.getLicenceStatus());
        } else {
            user.setActive(true);
            userService.save(user);
            webSocketService.notifyLicenceStatus(user.getId(), LicenceStatus.ACTIVE);
        }
    }

    public String login(String email, String password, String machineHash) {
        email = email.toLowerCase();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        checkLicenceActive(user);
        var machineOpt = machineService.findByUserAndMachineHash(user, machineHash);

        if (machineOpt.isEmpty()) {
            long count = machineService.countMachinesByUser(user);
            int limit = machineLimit(user.getPlan());

            if (count >= limit) {
                throw new RuntimeException("Machine limit reached");
            }
            machineService.registerMachine(user, machineHash);
        }
        return jwtService.generateToken(user.getEmail(), machineHash);
    }

    public String loginWeb(String email, String password) {
        email = email.toLowerCase();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        checkLicenceActive(user);
        return jwtService.generateToken(user.getEmail(), null);
    }

    private void checkLicenceActive(User user) {
        if (!user.isActive()) {
            throw new RuntimeException("Inactive account");
        }
        if (user.getLicenceStatus() != LicenceStatus.ACTIVE) {
            throw new RuntimeException("Licence inactive");
        }
    }

    private int machineLimit(Plan plan) {
        return switch (plan) {
            case ADMIN -> 9999;
            case SOLO -> 1;
            case SMALL -> 5;
            case FULL -> 10;
        };
    }

}