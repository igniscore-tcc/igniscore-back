package com.igniscore.api.repository;

import com.igniscore.api.model.PlanPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanPriceRepository extends JpaRepository<PlanPrice, Integer> {

    Optional<PlanPrice> findByPlanCodeAndCurrencyAndActiveTrue(
            String code,
            String currency
    );
}