package com.igniscore.api.repository;

import com.igniscore.api.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, Integer> {
}
