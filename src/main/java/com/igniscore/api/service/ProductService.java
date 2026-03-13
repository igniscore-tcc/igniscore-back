package com.igniscore.api.service;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.igniscore.api.utils.CompanyUtils;

import java.util.Date;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private CompanyUtils companyUtils;

    public Product createProduct(String name, String type, Date validity, String lot, Float price, Integer company_id){
        Company company = companyUtils.existsCompany(company_id);

        Product product = new Product();
        product.setName(name);
        product.setType(type);
        product.setValidity(validity);
        product.setLot(lot);
        product.setPrice(price);
        product.setCompany(company);


        return repository.save(product);

    }
}
