package com.igniscore.api.repository;

import com.igniscore.api.dto.dashboard.*;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.ExpirationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Company, Integer> {

    @Query("""
        SELECT COUNT(c) FROM Client c
        WHERE c.company.id = :companyId
    """)
    Long countClientsByCompanyId(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COUNT(c) FROM Client c
        WHERE c.company.id = :companyId
          AND CAST(c.createdAt AS LocalDate) >= :oneWeekAgo
    """)
    Long countNewClientsThisWeek(@Param("companyId") Integer companyId, @Param("oneWeekAgo") LocalDate oneWeekAgo);

    @Query("""
        SELECT COALESCE(SUM(s.total), 0) FROM Sale s
        WHERE s.company.id = :companyId AND s.date BETWEEN :startDate AND :endDate
    """)
    BigDecimal getRevenueByPeriod(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT COUNT(s) FROM Sale s
        WHERE s.company.id = :companyId 
          AND s.status = com.igniscore.api.model.SaleStatus.PENDING 
    """)
    Long countPendingOrders(@Param("companyId") Integer companyId);

    @Query("""
        SELECT new com.igniscore.api.dto.dashboard.UpcomingEquipmentExpirationDTO(
            'Equipamento #' || CAST(s.numberSale AS string),
            'Estoque Geral',
            dateDiff(day, CURRENT_DATE, s.dueDate),
            s.dueDate
        )
        FROM Expiration e
        JOIN e.sale s
        WHERE s.company.id = :companyId
          AND s.dueDate BETWEEN CURRENT_DATE AND :thirtyDaysHence
        ORDER BY s.dueDate ASC
    """)
    List<UpcomingEquipmentExpirationDTO> findUpcomingEquipmentExpirations(
            @Param("companyId") Integer companyId,
            @Param("thirtyDaysHence") LocalDate thirtyDaysHence
    );

    @Query("""
        SELECT COUNT(e) FROM Expiration e
        JOIN e.sale s
        WHERE s.company.id = :companyId
          AND s.dueDate BETWEEN CURRENT_DATE AND :thirtyDaysHence
    """)
    Long countItemsExpiringSoon(@Param("companyId") Integer companyId, @Param("thirtyDaysHence") LocalDate thirtyDaysHence);

    @Query("""
        SELECT COUNT(e) FROM Expiration e
        JOIN e.sale s
        WHERE s.company.id = :companyId
          AND s.dueDate < CURRENT_DATE
    """)
    Long countExpiredItems(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COUNT(e) FROM Expiration e
        JOIN e.sale s
        WHERE s.company.id = :companyId
          AND s.dueDate >= CURRENT_DATE
    """)
    Long countCompliantItems(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COUNT(e) FROM Expiration e
        JOIN e.sale s
        WHERE s.company.id = :companyId
    """)
    Long countTotalItems(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COALESCE(SUM(s.total), 0) FROM Sale s
        WHERE s.company.id = :companyId
          AND s.dueDate BETWEEN CURRENT_DATE AND :ninetyDaysHence
    """)
    BigDecimal getForecastRechargesRevenue(@Param("companyId") Integer companyId, @Param("ninetyDaysHence") LocalDate ninetyDaysHence);

    @Query("""
        SELECT COALESCE(SUM(s.total), 0) FROM Sale s
        WHERE s.company.id = :companyId
          AND s.dueDate < CURRENT_DATE
          AND s.status = com.igniscore.api.model.SaleStatus.PENDING
    """)
    BigDecimal getOverdueRevenue(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COUNT(DISTINCT s.client.id) FROM Sale s
        WHERE s.company.id = :companyId
          AND s.dueDate < CURRENT_DATE
          AND s.status = com.igniscore.api.model.SaleStatus.PENDING
    """)
    Long countOverdueClients(@Param("companyId") Integer companyId);

    @Query("""
        SELECT COUNT(e)
        FROM Expiration e
        JOIN e.sale s
        WHERE s.company.id = :companyId
            AND e.status = :status
            AND s.date BETWEEN :startOfMonth AND :endOfMonth
    """)
    long countCondemnedExpirations(
            @Param("companyId") Integer companyId,
            @Param("status") ExpirationStatus status,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth
    );

    @Query("""
        SELECT new com.igniscore.api.dto.dashboard.MonthlySalesDTO(
            MONTH(s.date),
            YEAR(s.date),
            COALESCE(SUM(s.total), 0)
        )
        FROM Sale s
        WHERE s.company.id = :companyId
          AND s.date BETWEEN :twelveMonthsAgo AND :today
        GROUP BY YEAR(s.date), MONTH(s.date)
        ORDER BY YEAR(s.date) ASC, MONTH(s.date) ASC
    """)
    List<MonthlySalesDTO> getSalesLast12Months(
            @Param("companyId") Integer companyId,
            @Param("twelveMonthsAgo") LocalDate twelveMonthsAgo,
            @Param("today") LocalDate today
    );
}