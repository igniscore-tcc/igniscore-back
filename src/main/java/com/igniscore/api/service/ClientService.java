package com.igniscore.api.service;

import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.utils.CompanyUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final CompanyUtils companyUtils;

    public ClientService(ClientRepository repository, CompanyUtils companyUtils) {
        this.repository = repository;
        this.companyUtils = companyUtils;
    }

    public Client createClient(String name, String cnpj, String email, Integer number, String ie, String uf_ie, String obs) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        Client client = new Client();
        client.setName(name);
        client.setCnpj(cnpj);
        client.setEmail(email);
        client.setNumber(number);
        client.setIe(ie);
        client.setUfIe(uf_ie);
        client.setObs(obs);
        client.setCompany(company);

        repository.save(client);

        return client;
    }

    public Client updateClient(String name, String cnpj, String email, Integer number, String ie, String uf_ie, String obs, Integer id){

        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        if (!client.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        if (name != null) client.setName(name);
        if (cnpj != null) client.setCnpj(cnpj);
        if (email != null) client.setEmail(email);
        if (number != null) client.setNumber(number);
        if (ie != null) client.setIe(ie);
        if (uf_ie != null) client.setUfIe(uf_ie);
        if (obs != null) client.setObs(obs);

        return repository.save(client);
    }

    public List<Client> findAll() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        return repository.findByCompany(company);
    }

    public Client findClient(Integer id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client não encontrado"));

        if(client.getCompany() != company) {
            throw new RuntimeException("Esse cliente não pertence a essa empresa");
        }

        return client;
    }

    public String deleteClient(Integer id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client não encontrado"));

        if(client.getCompany() != company) {
            throw new RuntimeException("Esse cliente não pertence a essa empresa");
        }


        repository.delete(client);

        return "Deletado!";
    }



}
