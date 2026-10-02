package com.igniscore.api.controller;

import com.igniscore.api.dto.address.AddressRegisterDTO;
import com.igniscore.api.model.Address;
import com.igniscore.api.service.AddressService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @MutationMapping
    public Address storeAddress(@Argument AddressRegisterDTO input) {
        return addressService.store(input);
    }
}