package com.bossawebsolutions.apigen.web.controller;

import com.bossawebsolutions.apigen.application.dto.ChangePasswordRequestDTO;
import com.bossawebsolutions.apigen.application.dto.LoginRequest;
import com.bossawebsolutions.apigen.application.dto.LoginResponse;
import com.bossawebsolutions.apigen.application.dto.RegisterRequest;
import com.bossawebsolutions.apigen.application.service.AuthService;
import com.bossawebsolutions.apigen.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(
                request.name(),
                request.email(),
                request.password(),
                request.plan(),
                request.paymentMethodId()
        );
        return ResponseEntity.ok("User Created");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(
                request.email(),
                request.password(),
                request.machineHash()
        );

        LoginResponse resp = new LoginResponse(
                token,
                System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3,
                request.email()
        );

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login-web")
    public ResponseEntity<LoginResponse> loginWeb(@RequestBody LoginRequest request) {
        String token = authService.loginWeb(request.email(), request.password());
        LoginResponse resp = new LoginResponse(
                token,
                System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3,
                request.email()
        );
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequestDTO dto) {
        userService.changePassword(dto.currentPassword(), dto.newPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel-subscription")
    public ResponseEntity<Void> cancelSubscription() {
        userService.cancelSubscription();
        return ResponseEntity.ok().build();
    }

}