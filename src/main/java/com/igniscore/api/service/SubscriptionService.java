package com.igniscore.api.service;

import com.igniscore.api.dto.stripe.StripeRequestDTO;
import com.igniscore.api.dto.stripe.StripeResponseDTO;
import com.igniscore.api.model.*;
import com.igniscore.api.repository.PlanPriceRepository;
import com.igniscore.api.repository.PlanRepository;
import com.igniscore.api.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final PlanPriceRepository planPriceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final StripeService stripeService;
    private final PlanRepository planRepository;

    @Transactional
    public StripeResponseDTO createCheckout(
            StripeRequestDTO request
    ) {

        Company company = authenticatedUserService
                .getCompanyOrThrow();

        Integer companyId = company.getId();

        validateRequest(request);

        validateExistingSubscription(companyId);

        PlanPrice planPrice = planPriceRepository
                .findByPlanCodeAndCurrencyAndActiveTrue(
                        request.getPlanCode(),
                        request.getCurrency()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Plan price not found"
                        )
                );

        return stripeService.createCheckout(
                company,
                planPrice
        );
    }

    @Transactional(readOnly = true)
    public String getMySubscription(Company company) {

        Subscription subscription = subscriptionRepository.findFirstByCompanyOrderByCreatedAtDesc(company);

        /*
        PlanPrice planPrice = planPriceRepository.findByID(subscription.getPlanPrice().getId());

        Plan plan = planRepository.findByCode(planPrice.getPlan().getCode()).orElseThrow(() ->
                new IllegalArgumentException(
                        "Plan price not found"
                )
        );;
         */


        String plan = subscription.getPlanPrice().getPlan().getCode();
        return plan;

    }

    private void validateRequest(
            StripeRequestDTO request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request cannot be null"
            );
        }

        if (request.getPlanCode() == null ||
                request.getPlanCode().isBlank()) {

            throw new IllegalArgumentException(
                    "Plan code is required"
            );
        }

        if (request.getCurrency() == null ||
                request.getCurrency().isBlank()) {

            throw new IllegalArgumentException(
                    "Currency is required"
            );
        }

        String currency = request.getCurrency().toUpperCase();

        if (!currency.equals("BRL") &&
                !currency.equals("USD")) {

            throw new IllegalArgumentException(
                    "Unsupported currency"
            );
        }
    }

    private void validateExistingSubscription(
            Integer companyId
    ) {

        subscriptionRepository
                .findFirstByCompanyIdAndStatusIn(
                        companyId,
                        List.of(
                                SubscriptionStatus.TRIALING,
                                SubscriptionStatus.ACTIVE,
                                SubscriptionStatus.PAST_DUE
                        )
                )
                .ifPresent(subscription -> {
                    throw new IllegalStateException(
                            "Company already has an active subscription"
                    );
                });
    }
}