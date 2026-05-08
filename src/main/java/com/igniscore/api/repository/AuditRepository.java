package com.igniscore.api.repository;

import com.igniscore.api.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

interface AuditRepository extends JpaRepository<Audit, Integer> {
}
