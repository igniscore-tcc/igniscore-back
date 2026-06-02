package com.igniscore.api.controller;

import com.igniscore.api.dto.DashboardDTO;
import com.igniscore.api.dto.MonthlySalesDTO;
import com.igniscore.api.dto.SalesByClientDTO;
import com.igniscore.api.dto.TopSellingProductDTO;
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