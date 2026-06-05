package com.igniscore.api.dto.sale;

import com.igniscore.api.model.Sale;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SaleQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Sale> sales;

    private final int totalPages;

    private final long totalSales;

    public SaleQueryDTO(
            List<Sale> sales,
            int totalPages,
            long totalSales
    ) {
        this.sales = sales;
        this.totalPages = totalPages;
        this.totalSales = totalSales;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalSales() {
        return totalSales;
    }
}
