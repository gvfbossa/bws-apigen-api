package com.bossawebsolutions.apigen.application.service.stripe;

import com.bossawebsolutions.apigen.domain.Plan;
import com.bossawebsolutions.apigen.domain.LicenceStatus;
import com.bossawebsolutions.apigen.domain.entity.PaymentCard;
import com.bossawebsolutions.apigen.domain.entity.User;
import com.bossawebsolutions.apigen.domain.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

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
    public void processOneTimePayment(User usuario, String paymentMethodId) {
        try {
            String customerId = usuario.getStripeCustomerId();

            if (customerId == null || customerId.isBlank()) {
                customerId = stripeClient.createCustomer(usuario.getEmail());
                usuario.setStripeCustomerId(customerId);
            }
            stripeClient.attachAndSetDefaultPaymentMethod(paymentMethodId, customerId);

            String priceId = mapPlanToPriceId(usuario.getPlan());

            Long amount = stripeClient
                    .getPrice(priceId)
                    .getUnitAmount();

            PaymentIntent paymentIntent =
                    stripeClient.createOneTimePaymentIntent(
                            customerId,
                            amount,
                            paymentMethodId
                    );
            if (!"succeeded".equals(paymentIntent.getStatus())) {
                throw new RuntimeException(
                        "Pagamento não confirmado. Status: " + paymentIntent.getStatus()
                );
            }
            PaymentCard cardInfo = stripeClient.getCardInfo(paymentMethodId);
            usuario.setStripePaymentIntentId(paymentIntent.getId());

            usuario.setLicenceStatus(LicenceStatus.ACTIVE);
            if (usuario.getCard() == null) {
                usuario.setCard(new PaymentCard());
            }
            usuario.getCard().setBrand(cardInfo.getBrand());
            usuario.getCard().setLast4(cardInfo.getLast4());
            usuario.getCard().setExpMonth(cardInfo.getExpMonth());
            usuario.getCard().setExpYear(cardInfo.getExpYear());
            usuario.getCard().setGatewayCardId(paymentMethodId);

            usuario.setActive(true);

            usuarioRepository.save(usuario);
        } catch (StripeException e) {
            throw new RuntimeException("Falha ao processar pagamento: " + e.getMessage());
        }
    }

    private String mapPlanToPriceId(Plan plan) {
        return switch (plan) {
            case ADMIN -> priceIdAdmin;
            case SOLO -> priceIdSolo;
            case SMALL -> priceIdSmall;
            case FULL -> priceIdFull;
            default -> throw new IllegalArgumentException("Invalid plan");
        };
    }

}
