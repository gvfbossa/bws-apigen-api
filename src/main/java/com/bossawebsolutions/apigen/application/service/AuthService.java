package com.bossawebsolutions.apigen.application.service;

import com.bossawebsolutions.apigen.application.service.stripe.SubscriptionService;
import com.bossawebsolutions.apigen.application.service.websocket.WebSocketService;
import com.bossawebsolutions.apigen.config.JwtService;
import com.bossawebsolutions.apigen.domain.Plan;
import com.bossawebsolutions.apigen.domain.SubscriptionStatus;
import com.bossawebsolutions.apigen.domain.entity.User;
import com.bossawebsolutions.apigen.application.utils.SystemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final MachineService machineService;
    private final SubscriptionService subscriptionService;
    private final WebSocketService webSocketService;
    private final JwtService jwtService;
    private final SystemUtils systemUtils;

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
            subscriptionService.criarAssinaturaStripe(user, paymentMethodId);
            user.setActive(true);
            userService.save(user);

            webSocketService.notifySubscriptionStatus(user.getId(), user.getSubscriptionStatus());
        } else {
            user.setActive(true);
            userService.save(user);
            webSocketService.notifySubscriptionStatus(user.getId(), SubscriptionStatus.ACTIVE);
        }
    }

    public String login(String email, String password, String machineHash) {
        email = email.toLowerCase();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        checkSubscriptionActive(user);
        var machineOpt = machineService.findByUserAndMachineHash(user, machineHash);

        if (machineOpt.isEmpty()) {
            long count = machineService.countMachinesByUser(user);
            int limit = systemUtils.machineLimit(user.getPlan());

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
        checkSubscriptionActive(user);
        return jwtService.generateToken(user.getEmail(), null);
    }

    private void checkSubscriptionActive(User user) {
        if (!user.isActive() && (user.getSubscriptionPaidUntil() == null || LocalDate.now().isAfter(user.getSubscriptionPaidUntil().toLocalDate()))) {
            throw new RuntimeException("Subscription expired or inactive");
        }
    }

}