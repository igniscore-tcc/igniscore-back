package com.igniscore.api.repository;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
    Page<Sale> findByCompany(Company company, boolean b, Pageable pageable);
}
