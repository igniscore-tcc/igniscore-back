package com.igniscore.api.service;

import com.igniscore.api.dto.CompanyDTO;
import com.igniscore.api.dto.ProductDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.utils.CompanyUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final CompanyUtils companyUtils;

    public ProductService(ProductRepository repository, CompanyUtils companyUtils) {
        this.repository = repository;
        this.companyUtils = companyUtils;
    }

    public ProductDTO createProduct(String name, String type, LocalDate validity, String lot, Float price) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        Product product = new Product();
        product.setName(name);
        product.setType(type);
        product.setValidity(validity);
        product.setLot(lot);
        product.setPrice(price);
        product.setCompany(company);

        Product savedProduct = repository.save(product);

        return toDTO(savedProduct);
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setType(product.getType());
        dto.setValidity(product.getValidity());
        dto.setLot(product.getLot());
        dto.setPrice(product.getPrice());

        Company company = product.getCompany();
        if (company != null) {
            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId(company.getId());
            companyDTO.setName(company.getName());
            companyDTO.setCnpj(company.getCnpj());
            companyDTO.setIe(company.getIe());
            companyDTO.setUfIe(company.getUfIe());
            companyDTO.setEmail(company.getEmail());
            companyDTO.setPhone(company.getPhone());
            dto.setCompany(companyDTO);
        }

        return dto;
    }
}