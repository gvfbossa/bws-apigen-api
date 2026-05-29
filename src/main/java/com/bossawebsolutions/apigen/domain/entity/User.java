package com.bossawebsolutions.apigen.domain.entity;

import com.bossawebsolutions.apigen.domain.Plan;
import com.bossawebsolutions.apigen.domain.LicenceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Plan plan = Plan.SOLO;

    @Embedded
    private PaymentCard card;

    @Column(nullable = false)
    private boolean active = false;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Machine> machines = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String stripePaymentIntentId;

    @Column(unique = true)
    private String stripeCustomerId;

    @Enumerated(EnumType.STRING)
    private LicenceStatus licenceStatus = LicenceStatus.INACTIVE;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}