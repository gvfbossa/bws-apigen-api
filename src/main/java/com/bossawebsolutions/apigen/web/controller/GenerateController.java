package com.bossawebsolutions.apigen.web.controller;

import com.bossawebsolutions.apigen.application.dto.GenerateRequest;
import com.bossawebsolutions.apigen.application.service.GeneratorService;
import com.bossawebsolutions.apigen.application.service.UserService;
import com.bossawebsolutions.apigen.config.JwtService;
import com.bossawebsolutions.apigen.domain.entity.User;
import com.bossawebsolutions.apigen.domain.model.EntityMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GenerateController {

    private final GeneratorService generatorService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/generate")
    public ResponseEntity<String> generate(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody GenerateRequest request
    ) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);

        String email = jwtService.extractEmail(token);
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User inactive");
        }
        List<EntityMeta> entities = request.getEntities();
        byte[] zip = generatorService.generate(entities, request.getBasePackage());

        String base64Zip = Base64.getEncoder().encodeToString(zip);
        return ResponseEntity.ok(base64Zip);
    }
}