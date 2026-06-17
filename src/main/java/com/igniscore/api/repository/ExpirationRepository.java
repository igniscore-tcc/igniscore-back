package com.igniscore.api.repository;

import com.igniscore.api.dto.expiration.ExpirationProjectionDTO;
import com.igniscore.api.model.Expiration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpirationRepository extends JpaRepository<Expiration, Integer> {

    @Query("""
    SELECT new com.igniscore.api.dto.expiration.ExpirationProjectionDTO(
        e.id,
        s.id,
        c.name,
        s.date,
        s.dueDate,
        s.total,
        e.status
    )
    FROM Expiration e
    JOIN e.sale s
    JOIN s.client c
    WHERE s.company.id = :companyId
    ORDER BY s.dueDate ASC
    """)
    Page<ExpirationProjectionDTO> findExpirationsByCompanyId(
            @Param("companyId") Integer companyId,
            Pageable pageable
    );

    @Query("""
    SELECT new com.igniscore.api.dto.expiration.ExpirationProjectionDTO(
        e.id,
        s.id,
        c.name,
        s.date,
        s.dueDate,
        s.total,
        e.status
    )
    FROM Expiration e
    JOIN e.sale s
    JOIN s.client c
    WHERE s.company.id = :companyId
      AND s.dueDate BETWEEN :startDate AND :endDate
    ORDER BY s.dueDate ASC
    """)
    Page<ExpirationProjectionDTO> findExpirationsByPeriod(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
    SELECT new com.igniscore.api.dto.expiration.ExpirationProjectionDTO(
        e.id,
        s.id,
        c.name,
        s.date,
        s.dueDate,
        s.total,
        e.status
    )
    FROM Expiration e
    JOIN e.sale s
    JOIN s.client c
    WHERE s.company.id = :companyId
      AND s.dueDate BETWEEN :startDate AND :endDate
    ORDER BY s.dueDate ASC
    """)
    Page<ExpirationProjectionDTO> findUpcomingExpirations(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
    SELECT new com.igniscore.api.dto.expiration.ExpirationProjectionDTO(
        e.id,
        s.id,
        c.name,
        s.date,
        s.dueDate,
        s.total,
        e.status
    )
    FROM Expiration e
    JOIN e.sale s
    JOIN s.client c
    WHERE s.company.id = :companyId
      AND c.id = :clientId
    ORDER BY s.dueDate ASC
    """)
    Page<ExpirationProjectionDTO> findExpirationsByClient(
            @Param("companyId") Integer companyId,
            @Param("clientId") Integer clientId,
            Pageable pageable
    );
}