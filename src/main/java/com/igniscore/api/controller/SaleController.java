package com.igniscore.api.controller;

import com.igniscore.api.model.Product;
import com.igniscore.api.model.Sale;
import com.igniscore.api.model.SaleItem;
import com.igniscore.api.service.ProductService;
import com.igniscore.api.service.SaleService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SaleController {

    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    @MutationMapping
    public Sale storeSale(
            @Argument Integer clientId,
            @Argument String paymentMethod,
            @Argument List<SaleItem> items
    ) {
        return service.store(clientId, paymentMethod, items);
    }
}
