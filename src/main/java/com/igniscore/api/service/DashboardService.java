package com.igniscore.api.service;

import com.igniscore.api.dto.DashboardDTO;
import com.igniscore.api.dto.MonthlySalesDTO;
import com.igniscore.api.dto.SalesByClientDTO;
import com.igniscore.api.dto.TopSellingProductDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

        Long currentMonthExpirations =
                dashboardRepository.countCurrentMonthExpirations(
                        companyId,
                        startDate,
                        endDate
                );

        LocalDate today = LocalDate.now();
        LocalDate next30Days = today.plusDays(30);

        Long upcomingExpirations =
                dashboardRepository.countUpcomingExpirations(
                        companyId,
                        today,
                        next30Days
                );

        Long expiredExpirations =
                dashboardRepository.countExpiredExpirations(
                        companyId,
                        today
                );

        return new DashboardDTO(
                totalClients,
                totalProducts,
                totalSales,
                monthlyRevenue,
                0L,
                0L,
                currentMonthExpirations,
                upcomingExpirations,
                expiredExpirations
        );
    }

    public List<TopSellingProductDTO> getTopSellingProducts() {

        Company company = authUserService.getCompanyOrThrow();

        return dashboardRepository.findTopSellingProducts(
                company.getId()
        );
    }

    public List<MonthlySalesDTO> getMonthlySales() {

        Company company = authUserService.getCompanyOrThrow();

        Integer currentYear = LocalDate.now().getYear();

        return dashboardRepository.getMonthlySales(
                company.getId(),
                currentYear
        );
    }

    public List<SalesByClientDTO> getSalesByClient() {

        Company company = authUserService.getCompanyOrThrow();

        return dashboardRepository.getSalesByClient(
                company.getId()
        );
    }
}