package com.igniscore.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.PlanPrice;
import com.igniscore.api.model.Subscription;
import com.igniscore.api.model.SubscriptionStatus;
import com.igniscore.api.repository.CompanyRepository;
import com.igniscore.api.repository.PlanPriceRepository;
import com.igniscore.api.repository.SubscriptionRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final CompanyRepository companyRepository;
    private final PlanPriceRepository planPriceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handle(
            String payload,
            String signature
    ) {

        Event event;

        try {

            event = Webhook.constructEvent(
                    payload,
                    signature,
                    webhookSecret
            );

        } catch (SignatureVerificationException e) {

            log.error(
                    "Invalid Stripe webhook signature"
            );

            throw new IllegalArgumentException(
                    "Invalid Stripe webhook signature"
            );
        }

        log.info(
                "Stripe webhook received: {}",
                event.getType()
        );

        switch (event.getType()) {

            case "customer.subscription.created":
                handleSubscriptionCreated(event, payload);
                break;

            case "customer.subscription.updated":
                handleSubscriptionUpdated(event, payload);
                break;

            case "customer.subscription.deleted":
                handleSubscriptionDeleted(event, payload);
                break;

            case "invoice.paid":
                handleInvoicePaid(event);
                break;

            case "invoice.payment_failed":
                handleInvoicePaymentFailed(event);
                break;

            default:
                log.info(
                        "Ignoring Stripe event: {}",
                        event.getType()
                );
        }
    }

    private void handleSubscriptionCreated(
            Event event,
            String payload
    ) {

        try {

            JsonNode root =
                    objectMapper.readTree(payload);

            JsonNode subscription =
                    root.path("data")
                            .path("object");

            String stripeSubscriptionId =
                    subscription
                            .path("id")
                            .textValue();

            String stripeCustomerId =
                    subscription
                            .path("customer")
                            .textValue();

            String stripeStatus =
                    subscription
                            .path("status")
                            .textValue();

            boolean cancelAtPeriodEnd =
                    subscription
                            .path("cancel_at_period_end")
                            .asBoolean(false);

            JsonNode items =
                    subscription
                            .path("items")
                            .path("data");

            if (!items.isArray() || items.isEmpty()) {

                throw new IllegalStateException(
                        "Subscription has no items"
                );
            }

            JsonNode firstItem =
                    items.get(0);

            String stripePriceId =
                    firstItem
                            .path("price")
                            .path("id")
                            .textValue();

            Long currentPeriodStart =
                    firstItem
                            .path("current_period_start")
                            .asLong();

            Long currentPeriodEnd =
                    firstItem
                            .path("current_period_end")
                            .asLong();

            Long trialStart =
                    getNullableLong(
                            subscription,
                            "trial_start"
                    );

            Long trialEnd =
                    getNullableLong(
                            subscription,
                            "trial_end"
                    );

            if (subscriptionRepository
                    .findByStripeSubscriptionId(
                            stripeSubscriptionId
                    )
                    .isPresent()) {

                log.info(
                        "Subscription already exists: {}",
                        stripeSubscriptionId
                );

                return;
            }

            Company company =
                    companyRepository
                            .findByStripeCustomerId(
                                    stripeCustomerId
                            )
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Company not found for Stripe customer: "
                                                    + stripeCustomerId
                                    )
                            );

            PlanPrice planPrice =
                    planPriceRepository
                            .findByStripePriceId(
                                    stripePriceId
                            )
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Plan price not found for Stripe price: "
                                                    + stripePriceId
                                    )
                            );

            LocalDateTime now =
                    LocalDateTime.now(
                            ZoneOffset.UTC
                    );

            Subscription entity =
                    new Subscription();

            entity.setCompany(company);

            entity.setPlanPrice(planPrice);

            entity.setStripeCustomerId(
                    stripeCustomerId
            );

            entity.setStripeSubscriptionId(
                    stripeSubscriptionId
            );

            entity.setCancelAtPeriodEnd(
                    cancelAtPeriodEnd
            );

            entity.setCurrentPeriodStart(
                    toLocalDateTime(
                            currentPeriodStart
                    )
            );

            entity.setCurrentPeriodEnd(
                    toLocalDateTime(
                            currentPeriodEnd
                    )
            );

            entity.setTrialStart(
                    toLocalDateTime(
                            trialStart
                    )
            );

            entity.setTrialEnd(
                    toLocalDateTime(
                            trialEnd
                    )
            );

            entity.setCreatedAt(now);

            entity.setUpdatedAt(now);

            SubscriptionStatus status = mapStatus(stripeStatus);

            entity.setStatus(status);
            entity.setPastDueSince(
                    status == SubscriptionStatus.PAST_DUE ? now : null
            );

            subscriptionRepository.save(entity);

            log.info(
                    "Subscription created successfully: {}",
                    stripeSubscriptionId
            );

        } catch (Exception e) {

            log.error("Error processing Stripe subscription: {} / {}", event.getId(), event.getType(), e);

            throw new IllegalStateException(
                    "Unable to process Stripe subscription",
                    e
            );
        }
    }

    private void handleSubscriptionUpdated(
            Event event,
            String payload
    ) {

        try {

            JsonNode root =
                    objectMapper.readTree(payload);

            JsonNode stripeSubscription =
                    root.path("data")
                            .path("object");

            String stripeSubscriptionId =
                    stripeSubscription
                            .path("id")
                            .textValue();

            Subscription subscription =
                    subscriptionRepository
                            .findByStripeSubscriptionId(
                                    stripeSubscriptionId
                            )
                            .orElse(null);

            if (subscription == null) {

                log.warn(
                        "Subscription not found during update: {}",
                        stripeSubscriptionId
                );

                handleSubscriptionCreated(
                        event,
                        payload
                );

                return;
            }

            String stripeStatus =
                    stripeSubscription
                            .path("status")
                            .textValue();

            boolean cancelAtPeriodEnd =
                    stripeSubscription
                            .path("cancel_at_period_end")
                            .asBoolean(false);

            JsonNode items =
                    stripeSubscription
                            .path("items")
                            .path("data");

            if (!items.isArray() || items.isEmpty()) {

                throw new IllegalStateException(
                        "Subscription has no items"
                );
            }

            JsonNode firstItem =
                    items.get(0);

            Long currentPeriodStart =
                    firstItem
                            .path("current_period_start")
                            .asLong();

            Long currentPeriodEnd =
                    firstItem
                            .path("current_period_end")
                            .asLong();

            Long trialStart =
                    getNullableLong(
                            stripeSubscription,
                            "trial_start"
                    );

            Long trialEnd =
                    getNullableLong(
                            stripeSubscription,
                            "trial_end"
                    );

            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

            SubscriptionStatus previousStatus = subscription.getStatus();
            SubscriptionStatus newStatus = mapStatus(stripeStatus);

            subscription.setStatus(newStatus);
            subscription.setPastDueSince(
                    resolvePastDueSince(
                            previousStatus,
                            newStatus,
                            subscription.getPastDueSince(),
                            now
                    )
            );

            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);

            subscription.setCurrentPeriodStart(
                    toLocalDateTime(currentPeriodStart)
            );

            subscription.setCurrentPeriodEnd(
                    toLocalDateTime(currentPeriodEnd)
            );

            subscription.setTrialStart(
                    toLocalDateTime(trialStart)
            );

            subscription.setTrialEnd(
                    toLocalDateTime(trialEnd)
            );

            subscription.setUpdatedAt(now);

            subscriptionRepository.save(subscription);

            log.info(
                    "Subscription updated successfully: {}",
                    stripeSubscriptionId
            );

        } catch (Exception e) {

            log.error("Error updating Stripe subscription: {} / {}", event.getId(), event.getType(), e);

            throw new IllegalStateException(
                    "Unable to update Stripe subscription",
                    e
            );
        }
    }

    private void handleSubscriptionDeleted(
            Event event,
            String payload
    ) {

        try {

            JsonNode root =
                    objectMapper.readTree(payload);

            JsonNode stripeSubscription =
                    root.path("data")
                            .path("object");

            String stripeSubscriptionId =
                    stripeSubscription
                            .path("id")
                            .textValue();

            Subscription subscription =
                    subscriptionRepository
                            .findByStripeSubscriptionId(
                                    stripeSubscriptionId
                            )
                            .orElse(null);

            if (subscription == null) {

                log.warn(
                        "Subscription not found during deletion: {}",
                        stripeSubscriptionId
                );

                return;
            }

            subscription.setStatus(
                    SubscriptionStatus.CANCELED
            );

            subscription.setPastDueSince(null);

            subscription.setCanceledAt(
                    LocalDateTime.now(
                            ZoneOffset.UTC
                    )
            );

            subscription.setUpdatedAt(
                    LocalDateTime.now(
                            ZoneOffset.UTC
                    )
            );

            subscriptionRepository.save(
                    subscription
            );

            log.info(
                    "Subscription canceled: {}",
                    stripeSubscriptionId
            );

        } catch (Exception e) {

            log.error("Error deleting Stripe subscription: {} / {}", event.getId(), event.getType(), e);

            throw new IllegalStateException(
                    "Unable to delete Stripe subscription",
                    e
            );
        }
    }

    private void handleInvoicePaid(Event event) {

        log.info(
                "Invoice paid: {}",
                event.getId()
        );
    }

    private void handleInvoicePaymentFailed(Event event) {

        log.info(
                "Invoice payment failed: {}",
                event.getId()
        );
    }

    private Long getNullableLong(
            JsonNode node,
            String field
    ) {

        JsonNode value =
                node.path(field);

        if (value.isMissingNode() || value.isNull()) {
            return null;
        }

        return value.asLong();
    }

    private SubscriptionStatus mapStatus(
            String stripeStatus
    ) {

        return switch (stripeStatus) {

            case "trialing" ->
                    SubscriptionStatus.TRIALING;

            case "active" ->
                    SubscriptionStatus.ACTIVE;

            case "past_due" ->
                    SubscriptionStatus.PAST_DUE;

            case "canceled" ->
                    SubscriptionStatus.CANCELED;

            case "unpaid" ->
                    SubscriptionStatus.UNPAID;

            case "incomplete" ->
                    SubscriptionStatus.INCOMPLETE;

            case "incomplete_expired" ->
                    SubscriptionStatus.INCOMPLETE_EXPIRED;

            case "paused" ->
                    SubscriptionStatus.PAUSED;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported Stripe subscription status: "
                                    + stripeStatus
                    );
        };
    }

    private LocalDateTime toLocalDateTime(
            Long timestamp
    ) {

        if (timestamp == null) {
            return null;
        }

        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                ZoneOffset.UTC
        );
    }

    private LocalDateTime resolvePastDueSince(
            SubscriptionStatus previousStatus,
            SubscriptionStatus newStatus,
            LocalDateTime existingPastDueSince,
            LocalDateTime now
    ) {
        if (newStatus != SubscriptionStatus.PAST_DUE) {
            return null;
        }

        if (existingPastDueSince != null) {
            return existingPastDueSince;
        }

        return now;
    }
}