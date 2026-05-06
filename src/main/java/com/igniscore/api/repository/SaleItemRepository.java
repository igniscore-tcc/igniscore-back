package com.igniscore.api.repository;

import com.igniscore.api.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
}
