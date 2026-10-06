package com.igniscore.api.controller;

import com.igniscore.api.dto.stripe.StripeRequestDTO;
import com.igniscore.api.dto.stripe.StripeResponseDTO;
import com.igniscore.api.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/v1")
public class StripeController {

    private StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponseDTO> checkoutProducts(@RequestBody StripeRequestDTO stripeRequestDTO) {
        StripeResponseDTO response = stripeService.checkoutProducts(stripeRequestDTO);

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }
}
