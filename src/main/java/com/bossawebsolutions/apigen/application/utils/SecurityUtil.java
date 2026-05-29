package com.bossawebsolutions.apigen.application.utils;

import com.bossawebsolutions.apigen.application.service.UserService;
import com.bossawebsolutions.apigen.domain.entity.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final UserService usuarioService;

    public SecurityUtil(@Lazy UserService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                (authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            throw new SecurityException("Nenhum usuário autenticado");
        }

        String email;

        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof String s) {
            email = s;
        } else {
            throw new SecurityException("Principal inválido");
        }

        return usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}