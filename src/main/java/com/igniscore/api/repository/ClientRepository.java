package com.igniscore.api.repository;

import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    List<Client> findByCompany(Company company);
}
