package com.igniscore.api.repository;

import com.igniscore.api.model.PlanPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlanPriceRepository extends JpaRepository<PlanPrice, Integer> {

    Optional<PlanPrice> findByPlanCodeAndCurrencyAndActiveTrue(
            String code,
            String currency
    );

    Optional<PlanPrice> findByStripePriceId(String stripePriceId);

    @Query("SELECT pp FROM PlanPrice pp JOIN FETCH pp.plan p WHERE pp.id = :planPriceId")
    PlanPrice findByID(Integer planPriceId);
}