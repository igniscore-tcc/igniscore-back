package com.igniscore.api.repository;

import com.igniscore.api.model.Subscription;
import com.igniscore.api.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByStripeSubscriptionId(
            String stripeSubscriptionId
    );

    Optional<Subscription> findByStripeCustomerId(
            String stripeCustomerId
    );

    List<Subscription> findByCompanyId(Long companyId);

    Optional<Subscription> findFirstByCompanyIdAndStatusIn(
            Integer companyId,
            List<SubscriptionStatus> statuses
    );


    List<Subscription> findByCompany_IdOrderByUpdatedAtDesc(Integer companyId);
}