package com.igniscore.api.utils;

import com.igniscore.api.model.Company;
import com.igniscore.api.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyUtils {

    @Autowired
    private CompanyRepository repository;

    public Company existsCompany(Integer id) {
        String lixo = "Lixo";
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Empresa não encotrada"));
    }
}
