package com.bossawebsolutions.apigen.web.controller;

import com.bossawebsolutions.apigen.application.dto.UserDashboardResponseDTO;
import com.bossawebsolutions.apigen.application.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/info")
    public ResponseEntity<UserDashboardResponseDTO> getUserDashboardInfo() {
        return ResponseEntity.ok(dashboardService.getUserDashboardInfo());
    }

}
