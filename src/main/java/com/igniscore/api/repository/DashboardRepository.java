package com.igniscore.api.repository;

import com.igniscore.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DashboardRepository extends JpaRepository<Company, Integer> {

    @Query("""
        SELECT COUNT(c)
        FROM Client c
        WHERE c.company.id = :companyId
    """)
    Long countClientsByCompanyId(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COUNT(p)
        FROM Product p
        WHERE p.company.id = :companyId
    """)
    Long countProductsByCompanyId(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COUNT(s)
        FROM Sale s
        WHERE s.company.id = :companyId
    """)
    Long countSalesByCompanyId(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COALESCE(SUM(s.total), 0)
        FROM Sale s
        WHERE s.company.id = :companyId
          AND s.date BETWEEN :startDate AND :endDate
    """)
    BigDecimal getRevenueByPeriod(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}