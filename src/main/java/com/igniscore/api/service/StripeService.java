package com.igniscore.api.service;

import com.igniscore.api.dto.stripe.StripeRequestDTO;
import com.igniscore.api.dto.stripe.StripeResponseDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secret}")
    private String stripeKey;

    public StripeResponseDTO checkoutProducts(StripeRequestDTO requestDTO) {

        Stripe.apiKey = stripeKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(requestDTO.getName()).build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("BRL")
                .setUnitAmount(requestDTO.getAmount())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(requestDTO.getQuantity())
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://app.igniscore.com.br/login")
                .setCancelUrl("https://app.igniscore.com.br/register")
                .addLineItem(lineItem)
                .build();

        Session session = null;

        try {

            session = Session.create(params);

        } catch (StripeException ex) {

            System.out.println(ex.getMessage());
        }

        return StripeResponseDTO.builder()
                .status("SUCCESS")
                .message("Payment session created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();

    }

}
