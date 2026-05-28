package com.bossawebsolutions.apigen.application.service.stripe;

import com.bossawebsolutions.apigen.domain.Plan;
import com.bossawebsolutions.apigen.domain.SubscriptionStatus;
import com.bossawebsolutions.apigen.domain.entity.PaymentCard;
import com.bossawebsolutions.apigen.domain.entity.User;
import com.bossawebsolutions.apigen.domain.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.SubscriptionUpdateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final StripeClient stripeClient;
    private final UserRepository usuarioRepository;

    @Value("${stripe.apigen.price-id.admin:}")
    private String priceIdAdmin;

    @Value("${stripe.apigen.price-id.solo}")
    private String priceIdSolo;

    @Value("${stripe.apigen.price-id.small}")
    private String priceIdSmall;

    @Value("${stripe.apigen.price-id.full}")
    private String priceIdFull;

    @Transactional
    public void criarAssinaturaStripe(User usuario, String paymentMethodId) {
        String priceId = mapPlanoToPrice(usuario.getPlan());
        try {
            String customerId = usuario.getStripeCustomerId();

            if (customerId == null || customerId.isBlank()) {
                customerId = stripeClient.createCustomer(usuario.getEmail());
                usuario.setStripeCustomerId(customerId);
            }
            stripeClient.attachAndSetDefaultPaymentMethod(paymentMethodId, customerId);

            Subscription subscription = stripeClient.createSubscription(
                    customerId,
                    priceId,
                    paymentMethodId
            );

            if (!"active".equals(subscription.getStatus())) {
                throw new RuntimeException(
                        "Pagamento não confirmado. Status: " + subscription.getStatus()
                );
            }

            PaymentCard cardInfo = stripeClient.getCardInfo(paymentMethodId);

            usuario.setStripeSubscriptionId(subscription.getId());
            usuario.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            usuario.setSubscriptionPaidUntil(LocalDateTime.now().plusDays(30));

            if (usuario.getCard() == null) {
                usuario.setCard(new PaymentCard());
            }

            usuario.getCard().setBrand(cardInfo.getBrand());
            usuario.getCard().setLast4(cardInfo.getLast4());
            usuario.getCard().setExpMonth(cardInfo.getExpMonth());
            usuario.getCard().setExpYear(cardInfo.getExpYear());
            usuario.getCard().setGatewayCardId(paymentMethodId);

            usuarioRepository.save(usuario);

        } catch (StripeException e) {
            throw new RuntimeException("Falha ao processar pagamento: " + e.getMessage());
        }
    }

    public void cancelarAssinaturaNoStripe(String stripeSubscriptionId) {
        try {
            Subscription subscription = Subscription.retrieve(stripeSubscriptionId);

            SubscriptionUpdateParams updateParams =
                    SubscriptionUpdateParams.builder()
                            .setCancelAtPeriodEnd(true)
                            .build();

            subscription.update(updateParams);

            subscription.cancel(
                    SubscriptionCancelParams.builder()
                            .setProrate(false)
                            .build()
            );

            log.info("Assinatura Stripe {} cancelada com sucesso (fim do período).", stripeSubscriptionId);
        } catch (StripeException e) {
            log.error("Erro ao cancelar assinatura Stripe {}: {}", stripeSubscriptionId, e.getMessage());
            throw new RuntimeException("Falha ao cancelar assinatura no Stripe. Tente novamente.", e);
        }
    }

    private String mapPlanoToPrice(Plan plano) {
        return switch (plano) {
            case ADMIN -> priceIdAdmin;
            case SOLO -> priceIdSolo;
            case SMALL -> priceIdSmall;
            case FULL -> priceIdFull;
            default -> throw new IllegalArgumentException("Invalid Plan");
        };
    }

}
