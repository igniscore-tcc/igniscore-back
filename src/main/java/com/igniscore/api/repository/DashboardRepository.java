package com.igniscore.api.repository;

import com.igniscore.api.dto.dashboard.MonthlySalesDTO;
import com.igniscore.api.dto.dashboard.SalesByClientDTO;
import com.igniscore.api.dto.dashboard.TopSellingProductDTO;
import com.igniscore.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @Query("""
    SELECT new com.igniscore.api.dto.dashboard.TopSellingProductDTO(
        p.id,
        p.name,
        SUM(si.quantity)
    )
    FROM SaleItem si
    JOIN si.product p
    JOIN si.sale s
    WHERE s.company.id = :companyId
    GROUP BY p.id, p.name
    ORDER BY SUM(si.quantity) DESC
    """)
    List<TopSellingProductDTO> findTopSellingProducts(
            @Param("companyId") Integer companyId
    );

    @Query("""
    SELECT new com.igniscore.api.dto.dashboard.MonthlySalesDTO(
        MONTH(s.date),
        COALESCE(SUM(s.total), 0)
    )
    FROM Sale s
    WHERE s.company.id = :companyId
      AND YEAR(s.date) = :year
    GROUP BY MONTH(s.date)
    ORDER BY MONTH(s.date)
    """)
    List<MonthlySalesDTO> getMonthlySales(
            @Param("companyId") Integer companyId,
            @Param("year") Integer year
    );

    @Query("""
    SELECT new com.igniscore.api.dto.dashboard.SalesByClientDTO(
        c.id,
        c.name,
        COALESCE(SUM(s.total), 0)
    )
    FROM Sale s
    JOIN s.client c
    WHERE s.company.id = :companyId
    GROUP BY c.id, c.name
    ORDER BY SUM(s.total) DESC
    """)
    List<SalesByClientDTO> getSalesByClient(
            @Param("companyId") Integer companyId
    );

    @Query("""
    SELECT COUNT(s)
    FROM Sale s
    WHERE s.company.id = :companyId
      AND s.dueDate BETWEEN :startDate AND :endDate
    """)
    Long countCurrentMonthExpirations(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT COUNT(e)
    FROM Expiration e
    JOIN e.sale s
    WHERE s.company.id = :companyId
      AND e.status = com.igniscore.api.model.ExpirationStatus.NEXT
    """)
    Long countUpcomingExpirations(
            @Param("companyId") Integer companyId
    );

    @Query("""
    SELECT COUNT(e)
    FROM Expiration e
    JOIN e.sale s
    WHERE s.company.id = :companyId
      AND e.status = com.igniscore.api.model.ExpirationStatus.EXPIRED
    """)
    Long countExpiredExpirations(
            @Param("companyId") Integer companyId
    );
}