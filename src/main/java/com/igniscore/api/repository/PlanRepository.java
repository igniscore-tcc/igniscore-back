
package com.igniscore.api.repository;

import com.igniscore.api.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    Optional<Plan> findByCode(String code);

    Optional<Plan> findByCodeAndActiveTrue(String code);
}