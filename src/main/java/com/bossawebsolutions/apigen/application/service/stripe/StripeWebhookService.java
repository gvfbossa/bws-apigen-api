package com.bossawebsolutions.apigen.application.service.stripe;

import com.bossawebsolutions.apigen.application.service.websocket.WebSocketService;
import com.bossawebsolutions.apigen.domain.SubscriptionStatus;
import com.bossawebsolutions.apigen.domain.entity.User;
import com.bossawebsolutions.apigen.domain.repository.UserRepository;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    private final UserRepository usuarioRepository;
    private final WebSocketService webSocketService;

    public void process(Event event) {

        switch (event.getType()) {

            case "invoice.payment_succeeded":
                handlePaymentSucceeded(event);
                break;

            case "invoice.payment_failed":
                handlePaymentFailed(event);
                break;

            case "customer.subscription.deleted":
                handleSubscriptionCancelled(event);
                break;

            case "customer.subscription.updated":
                handleSubscriptionUpdated(event);
                break;

            default:
                return;
        }
    }

    private void handlePaymentSucceeded(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        StripeObject obj = deserializer.getObject()
                .orElseGet(() -> {
                    try {
                        return deserializer.deserializeUnsafe();
                    } catch (EventDataObjectDeserializationException e) {
                        throw new RuntimeException(e);
                    }
                });

        if (!(obj instanceof Invoice)) return;

        Invoice invoice = (Invoice) obj;

        String customerId = invoice.getCustomer();

        User usuario = usuarioRepository.findByStripeCustomerId(customerId)
                .orElse(null);
        if (usuario == null) return;

        usuario.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        usuario.setSubscriptionPaidUntil(LocalDateTime.now().plusDays(30));
        usuarioRepository.save(usuario);

        webSocketService.notifySubscriptionStatus(
                usuario.getId(), SubscriptionStatus.ACTIVE);
    }


    private void handlePaymentFailed(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                .getObject().orElse(null);
        if (invoice == null) return;

        String customerId = invoice.getCustomer();

        User usuario = usuarioRepository.findByStripeCustomerId(customerId)
                .orElse(null);
        if (usuario == null) return;

        usuario.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
        usuarioRepository.save(usuario);

        webSocketService.notifySubscriptionStatus(usuario.getId(), SubscriptionStatus.INACTIVE);
    }

    private void handleSubscriptionCancelled(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject().orElse(null);
        if (subscription == null) return;

        String customerId = subscription.getCustomer();

        User usuario = usuarioRepository.findByStripeCustomerId(customerId)
                .orElse(null);
        if (usuario == null) return;

        usuario.setSubscriptionStatus(SubscriptionStatus.CANCELED);
        usuarioRepository.save(usuario);

        webSocketService.notifySubscriptionStatus(usuario.getId(), SubscriptionStatus.CANCELED);
    }

    private void handleSubscriptionUpdated(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject().orElse(null);
        if (subscription == null) return;

        String customerId = subscription.getCustomer();

        User usuario = usuarioRepository.findByStripeCustomerId(customerId)
                .orElse(null);
        if (usuario == null) return;

        if (subscription.getTrialEnd() != null) {

            usuario.setSubscriptionStatus(getStatusFromSubscription(subscription));

            usuarioRepository.save(usuario);
            webSocketService.notifySubscriptionStatus(usuario.getId(), getStatusFromSubscription(subscription));
        }
    }

    private SubscriptionStatus getStatusFromSubscription(Subscription subscription) {
        for (SubscriptionStatus status : SubscriptionStatus.values()) {
            if (status.name().equals(subscription.getStatus())) {
                return status;
            }
        }
        return null;
    }
}
