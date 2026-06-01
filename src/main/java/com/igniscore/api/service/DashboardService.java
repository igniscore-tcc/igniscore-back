package com.igniscore.api.service;

import com.igniscore.api.dto.DashboardDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final AuthenticatedUserService authUserService;

    public DashboardService(DashboardRepository dashboardRepository, AuthenticatedUserService authUserService) {
        this.dashboardRepository = dashboardRepository;
        this.authUserService = authUserService;
    }

    public DashboardDTO getDashboardMetrics() {

        Company company = authUserService.getCompanyOrThrow();

        Integer companyId = company.getId();

        Long totalClients =
                dashboardRepository.countClientsByCompanyId(companyId);

        Long totalProducts =
                dashboardRepository.countProductsByCompanyId(companyId);

        Long totalSales =
                dashboardRepository.countSalesByCompanyId(companyId);

        LocalDate startDate = LocalDate.now()
                .withDayOfMonth(1);

        LocalDate endDate = startDate
                .withDayOfMonth(startDate.lengthOfMonth());

        var monthlyRevenue =
                dashboardRepository.getRevenueByPeriod(
                        companyId,
                        startDate,
                        endDate
                );

        return new DashboardDTO(
                totalClients,
                totalProducts,
                totalSales,
                monthlyRevenue,
                0L,
                0L
        );
    }
}