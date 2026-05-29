package com.bossawebsolutions.apigen.application.service.stripe;

import com.bossawebsolutions.apigen.application.service.websocket.WebSocketService;
import com.bossawebsolutions.apigen.domain.LicenceStatus;
import com.bossawebsolutions.apigen.domain.entity.User;
import com.bossawebsolutions.apigen.domain.repository.UserRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    private final UserRepository usuarioRepository;
    private final WebSocketService webSocketService;

    public void process(Event event) {
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentFailed(event);
                break;
            default:
                return;
        }
    }

    private void handlePaymentSucceeded(Event event) {
        PaymentIntent paymentIntent =
                (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);
        if (paymentIntent == null) return;
        if (!"succeeded".equals(paymentIntent.getStatus())) return;
        String customerId = paymentIntent.getCustomer();
        User usuario = usuarioRepository.findByStripeCustomerId(customerId)
                .orElse(null);

        if (usuario == null) return;

        usuario.setLicenceStatus(LicenceStatus.ACTIVE);
        usuario.setActive(true);

        usuarioRepository.save(usuario);

        webSocketService.notifyLicenceStatus(
                usuario.getId(),
                LicenceStatus.ACTIVE
        );
    }

    private void handlePaymentFailed(Event event) {
        PaymentIntent paymentIntent =
                (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

        if (paymentIntent == null) return;
        String customerId = paymentIntent.getCustomer();
        User usuario = usuarioRepository.findByStripeCustomerId(customerId)
                .orElse(null);
        if (usuario == null) return;
        usuario.setLicenceStatus(LicenceStatus.INACTIVE);
        usuario.setActive(false);

        usuarioRepository.save(usuario);

        webSocketService.notifyLicenceStatus(
                usuario.getId(),
                LicenceStatus.INACTIVE
        );
    }

}