package com.bossawebsolutions.apigen.application.service.websocket;

import com.bossawebsolutions.apigen.domain.LicenceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyLicenceStatus(Long userId, LicenceStatus status) {
        messagingTemplate.convertAndSend(
                "/topic/licence-status/" + userId,
                status
        );
    }
}