package com.igniscore.api.service;

import com.igniscore.api.dto.address.AddressRegisterDTO;
import com.igniscore.api.dto.address.AddressUpdateDTO;
import com.igniscore.api.model.Address;
import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.AddressRepository;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.utils.AuditUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

    private final AddressRepository repository;
    private final ClientRepository clientRepository;
    private final AuthenticatedUserService authUserService;
    private final AuditUtils audit;

    public AddressService(
            AddressRepository repository,
            ClientRepository clientRepository,
            AuthenticatedUserService authUserService,
            AuditUtils audit
    ) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.authUserService = authUserService;
        this.audit = audit;
    }

    @Transactional
    public Address store(AddressRegisterDTO dto) {

        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Client client = clientRepository
                .findByIdAndCompanyAndDeletedAtIsNull(dto.getClientId(), company)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Address address = new Address(dto, client);

        Address saved = repository.save(address);

        audit.newAudit(
                user,
                company,
                "Address",
                "Create",
                null,
                saved
        );

        return saved;
    }

    @Transactional
    public Address update(AddressUpdateDTO dto) {

        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Client client = clientRepository
                .findByIdAndCompanyAndDeletedAtIsNull(dto.getClientId(), company)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Address address = repository
                .findByClient(client)
                .orElse(null);

        boolean creating = address == null;

        if (creating) {
            address = new Address(dto, client);
        } else {
            address.setStreet(dto.getStreet());
            address.setNumber(dto.getNumber());
            address.setCity(dto.getCity());
            address.setNeighborhood(dto.getNeighborhood());
            address.setState(dto.getState());
            address.setCep(dto.getCep());
        }

        Address saved = repository.save(address);

        audit.newAudit(
                user,
                company,
                "Address",
                creating ? "Create" : "Update",
                null,
                saved
        );

        return saved;
    }
}