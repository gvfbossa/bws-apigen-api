package com.bossawebsolutions.apigen.application.service.websocket;

import com.bossawebsolutions.apigen.domain.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifySubscriptionStatus(Long userId, SubscriptionStatus status) {
        messagingTemplate.convertAndSend(
                "/topic/subscription-status/" + userId,
                status
        );
    }
}
