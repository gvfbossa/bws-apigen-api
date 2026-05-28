package com.bossawebsolutions.apigen.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCard {

    @Column(name = "card_brand")
    private String brand;
    @Column(name = "card_last4")
    private String last4;
    @Column(name = "card_exp_month")
    private Long expMonth;
    @Column(name = "card_exp_year")
    private Long expYear;
    @Column(name = "card_gateway_id")
    private String gatewayCardId;
}

