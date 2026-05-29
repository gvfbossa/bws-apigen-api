package com.bossawebsolutions.apigen.config;

import com.bossawebsolutions.apigen.application.service.UserService;
import com.bossawebsolutions.apigen.domain.LicenceStatus;
import com.bossawebsolutions.apigen.domain.Plan;
import com.bossawebsolutions.apigen.domain.entity.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private final UserService userService;

    public DatabaseInitializer(UserService userService) {
        this.userService = userService;
    }

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.pwd}")
    private String adminPwd;

    @PostConstruct
    public void init() {
        if (userService.findByEmail(adminEmail).isEmpty()) {
            User userAdmin = new User();

            userAdmin.setName("Bossa Web Solutions");
            userAdmin.setEmail(adminEmail);
            userAdmin.setPasswordHash(
                    new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                            .encode(adminPwd)
            );
            userAdmin.setPlan(Plan.ADMIN);
            userAdmin.setActive(true);
            userAdmin.setLicenceStatus(LicenceStatus.ACTIVE);
            userAdmin.setStripeCustomerId(null);
            userAdmin.setStripePaymentIntentId(null);
            userService.save(userAdmin);
        }
    }
}
