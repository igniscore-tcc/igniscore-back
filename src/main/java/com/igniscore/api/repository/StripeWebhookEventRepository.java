package com.igniscore.api.repository;

import com.igniscore.api.model.StripeWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StripeWebhookEventRepository
        extends JpaRepository<StripeWebhookEvent, Long> {

    Optional<StripeWebhookEvent> findByStripeEventId(
            String stripeEventId
    );

    boolean existsByStripeEventId(
            String stripeEventId
    );
}