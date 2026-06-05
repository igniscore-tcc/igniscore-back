package com.igniscore.api.controller;

import com.igniscore.api.dto.dashboard.DashboardDTO;
import com.igniscore.api.dto.dashboard.MonthlySalesDTO;
import com.igniscore.api.dto.sale.SalesByClientDTO;
import com.igniscore.api.dto.dashboard.TopSellingProductDTO;
import com.igniscore.api.service.DashboardService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DashBoardController {

    private final DashboardService dashboardService;

    public DashBoardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @QueryMapping
    public DashboardDTO dashboard() {
        return dashboardService.getDashboardMetrics();
    }

    @QueryMapping
    public List<TopSellingProductDTO> topSellingProducts() {
        return dashboardService.getTopSellingProducts();
    }

    @QueryMapping
    public List<MonthlySalesDTO> monthlySales() {
        return dashboardService.getMonthlySales();
    }

    @QueryMapping
    public List<SalesByClientDTO> salesByClient() {
        return dashboardService.getSalesByClient();
    }
}