package com.igniscore.api.dto.address;

import com.igniscore.api.model.Address;
import java.io.Serial;
import java.io.Serializable;

public record AddressResponseDTO( Integer id,
                                  String street,
                                  String number,
                                  String city,
                                  String neighborhood,
                                  String state,
                                  String cep ) implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    public AddressResponseDTO(Address address) {
        this(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getNeighborhood(),
                address.getState(),
                address.getCep()
        );
    }
}