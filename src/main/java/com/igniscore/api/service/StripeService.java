package com.igniscore.api.service;

import com.igniscore.api.dto.stripe.StripeResponseDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.PlanPrice;
import com.igniscore.api.repository.CompanyRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.model.checkout.SessionCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secret}")
    private String stripeSecretKey;

    private final CompanyRepository companyRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public StripeResponseDTO createCheckout(
            Company company,
            PlanPrice planPrice
    ) {

        try {

            Customer customer = getOrCreateCustomer(company);

            Session existingSession =
                    findOpenCheckoutSession(customer.getId());

            if (existingSession != null) {

                return StripeResponseDTO.builder()
                        .status("PENDING")
                        .message("A payment session already exists")
                        .sessionId(existingSession.getId())
                        .sessionUrl(existingSession.getUrl())
                        .build();
            }

            SessionCreateParams params =
                    SessionCreateParams.builder()

                            .setMode(
                                    SessionCreateParams.Mode.SUBSCRIPTION
                            )

                            .setCustomer(
                                    customer.getId()
                            )

                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice(
                                                    planPrice.getStripePriceId()
                                            )
                                            .setQuantity(1L)
                                            .build()
                            )

                            .setSubscriptionData(
                                    SessionCreateParams.SubscriptionData.builder()
                                            .setTrialPeriodDays(14L)
                                            .build()
                            )

                            .setSuccessUrl(
                                    "https://app.igniscore.com.br/dashboard?subscription=success"
                            )

                            .setCancelUrl(
                                    "https://app.igniscore.com.br/planos?subscription=canceled"
                            )

                            .build();

            Session session = Session.create(params);

            return StripeResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Checkout session created successfully")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {

            log.error(
                    "Error creating Stripe Checkout Session",
                    e
            );

            return StripeResponseDTO.builder()
                    .status("ERROR")
                    .message(e.getMessage())
                    .sessionId(null)
                    .sessionUrl(null)
                    .build();
        }
    }

    private Customer getOrCreateCustomer(
            Company company
    ) throws StripeException {

        if (company.getStripeCustomerId() != null &&
                !company.getStripeCustomerId().isBlank()) {

            return Customer.retrieve(
                    company.getStripeCustomerId()
            );
        }

        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(company.getName())
                        .setEmail(company.getEmail())
                        .putMetadata(
                                "company_id",
                                String.valueOf(company.getId())
                        )
                        .build();

        Customer customer = Customer.create(params);

        company.setStripeCustomerId(
                customer.getId()
        );

        companyRepository.save(company);

        return customer;
    }

    private Session findOpenCheckoutSession(
            String customerId
    ) throws StripeException {

        SessionListParams params =
                SessionListParams.builder()
                        .setCustomer(customerId)
                        .setStatus(
                                SessionListParams.Status.OPEN
                        )
                        .setLimit(10L)
                        .build();

        SessionCollection sessions =
                Session.list(params);

        return sessions.getData()
                .stream()
                .findFirst()
                .orElse(null);
    }
}