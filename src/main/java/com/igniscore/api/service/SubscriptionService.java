package com.igniscore.api.service;

import com.igniscore.api.dto.stripe.StripeRequestDTO;
import com.igniscore.api.dto.stripe.StripeResponseDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.PlanPrice;
import com.igniscore.api.model.SubscriptionStatus;
import com.igniscore.api.repository.PlanPriceRepository;
import com.igniscore.api.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final PlanPriceRepository planPriceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final StripeService stripeService;

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