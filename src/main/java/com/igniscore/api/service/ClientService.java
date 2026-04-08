package com.igniscore.api.service;

import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.utils.CompanyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private CompanyUtils companyUtils;

    public Client createClient(Client client, Integer companyId) {

        Company company = companyUtils.existsCompany(companyId);
        if (company == null) throw new RuntimeException("Company not found");

        client.setCompany(company);

        //salva pra gerar ID
        Client saved = repository.save(client);

        //gera código
        String codigo = "CLI-" + String.format("%04d", saved.getId());
        saved.setCodigo(codigo);

        //salva novamente com código
        return repository.save(saved);
    }

}
