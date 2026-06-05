package com.igniscore.api.repository;

import com.igniscore.api.dto.ExpirationDTO;
import com.igniscore.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpirationRepository extends JpaRepository<Company, Integer> {

    @Query("""
        SELECT new com.igniscore.api.dto.ExpirationDTO(
            s.id,
            c.name,
            s.date,
            s.dueDate,
            s.total
        )
        FROM Sale s
        JOIN s.client c
        WHERE s.company.id = :companyId
        ORDER BY s.dueDate ASC
    """)
    List<ExpirationDTO> findExpirationsByCompanyId(
            @Param("companyId") Integer companyId
    );

    @Query("""
    SELECT new com.igniscore.api.dto.ExpirationDTO(
        s.id,
        c.name,
        s.date,
        s.dueDate,
        s.total
    )
    FROM Sale s
    JOIN s.client c
    WHERE s.company.id = :companyId
      AND s.dueDate BETWEEN :startDate AND :endDate
    ORDER BY s.dueDate ASC
    """)
    List<ExpirationDTO> findExpirationsByPeriod(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT new com.igniscore.api.dto.ExpirationDTO(
        s.id,
        c.name,
        s.date,
        s.dueDate,
        s.total
    )
    FROM Sale s
    JOIN s.client c
    WHERE s.company.id = :companyId
      AND s.dueDate BETWEEN :startDate AND :endDate
    ORDER BY s.dueDate ASC
    """)
    List<ExpirationDTO> findUpcomingExpirations(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}