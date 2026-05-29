package com.bossawebsolutions.apigen.application.service.stripe;

import com.bossawebsolutions.apigen.domain.entity.PaymentCard;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeClient {

    public StripeClient(@Value("${stripe.secret}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    public String createCustomer(String email) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .build();
        Customer customer = Customer.create(params);
        return customer.getId();
    }

    public void attachAndSetDefaultPaymentMethod(String paymentMethodId, String customerId) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build());

        Customer customer = Customer.retrieve(customerId);
        CustomerUpdateParams params = CustomerUpdateParams.builder()
                .setInvoiceSettings(
                        CustomerUpdateParams.InvoiceSettings.builder()
                                .setDefaultPaymentMethod(paymentMethodId)
                                .build()
                )
                .build();
        customer.update(params);
    }

    public PaymentIntent createOneTimePaymentIntent(
            String customerId,
            long amountInCents,
            String paymentMethodId
    ) throws StripeException {

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setCustomer(customerId)
                        .setAmount(amountInCents)
                        .setCurrency("brl")
                        .setPaymentMethod(paymentMethodId)
                        .setConfirm(true)
                        .build();

        return PaymentIntent.create(params);
    }

    public PaymentCard getCardInfo(String paymentMethodId) throws StripeException {
        PaymentMethod pm = PaymentMethod.retrieve(paymentMethodId);

        if (pm.getCard() == null) {
            pm = PaymentMethod.retrieve(paymentMethodId);
        }

        PaymentMethod.Card card = pm.getCard();

        return new PaymentCard(
                card.getBrand(),
                card.getLast4(),
                card.getExpMonth(),
                card.getExpYear(),
                pm.getId()
        );
    }

    public Price getPrice(String priceId) throws StripeException {
        return Price.retrieve(priceId);
    }

}