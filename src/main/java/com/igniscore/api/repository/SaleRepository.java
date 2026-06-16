package com.igniscore.api.repository;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
    Page<Sale> findByCompany(Company company, boolean b, Pageable pageable);
    Page<Sale> findByCompanyAndDateBetween(
            Company company,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
