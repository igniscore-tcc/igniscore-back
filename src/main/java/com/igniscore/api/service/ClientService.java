package com.igniscore.api.service;

import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.utils.CompanyUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final CompanyUtils companyUtils;

    @PersistenceContext
    private EntityManager entityManager;

    public ClientService(ClientRepository repository, CompanyUtils companyUtils) {
        this.repository = repository;
        this.companyUtils = companyUtils;
    }

    @Transactional
    public Client createClient(String name, String cnpj, String email, String phone, String ie, String ufIe, String obs, String cpf) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        Client client = new Client();
        client.setName(name);
        client.setCnpj(cnpj);
        client.setEmail(email);
        client.setPhone(phone);
        client.setIe(ie);
        client.setUfIe(ufIe);
        client.setObs(obs);
        client.setCpf(cpf);
        client.setCompany(company);

        Client saved = repository.save(client);
        entityManager.refresh(saved);
        return saved;
    }

    public Client updateClient(String name, String cnpj, String email, String phone, String ie, String ufIe, String obs, String cpf,Integer id){
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"));

        if (!client.getCompany().getId().equals(getCompany().getId())) {
            throw new RuntimeException("Acesso negado");
        }

        if (name != null) client.setName(name);
        if (cnpj != null) client.setCnpj(cnpj);
        if (email != null) client.setEmail(email);
        if (phone != null) client.setPhone(phone);
        if (ie != null) client.setIe(ie);
        if (ufIe != null) client.setUfIe(ufIe);
        if (obs != null) client.setObs(obs);
        if (cpf != null) client.setCpf(cpf);

        return repository.save(client);
    }

    public List<Client> findAll() {
        return repository.findByCompany(getCompany());
    }

    public Client findClient(Integer id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if(client.getCompany() != getCompany()) {
            throw new RuntimeException("Esse cliente não pertence a essa empresa");
        }

        return client;
    }

    public String deleteClient(Integer id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if(client.getCompany() != getCompany()) {
            throw new RuntimeException("Esse cliente não pertence a essa empresa");
        }
        repository.delete(client);

        return "Deletado!";
    }

    public Company getCompany() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        return companyUtils.loggedCompany(loggedUser.getCompany().getId());
    }

}
