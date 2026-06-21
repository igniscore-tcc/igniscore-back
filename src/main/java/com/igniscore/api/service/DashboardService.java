package com.igniscore.api.service;

import com.igniscore.api.dto.dashboard.*;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.ExpirationStatus;
import com.igniscore.api.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        LocalDate today = LocalDate.now();
        LocalDate startOfCurrentMonth = today.withDayOfMonth(1);
        LocalDate endOfCurrentMonth = today.withDayOfMonth(today.lengthOfMonth());

        LocalDate startOfLastMonth = startOfCurrentMonth.minusMonths(1);
        LocalDate endOfLastMonth = today.minusMonths(1);

        Long totalClients = dashboardRepository.countClientsByCompanyId(companyId);
        Long newClientsThisWeek = dashboardRepository.countNewClientsThisWeek(companyId, today.minusDays(7));

        BigDecimal currentMonthRevenue = dashboardRepository.getRevenueByPeriod(companyId, startOfCurrentMonth, endOfCurrentMonth);
        BigDecimal lastMonthRevenue = dashboardRepository.getRevenueByPeriod(companyId, startOfLastMonth, endOfLastMonth);
        Double revenueGrowthPercentage = calculatePercentageGrowth(currentMonthRevenue, lastMonthRevenue);

        Long pendingOrders = dashboardRepository.countPendingOrders(companyId);

        LocalDate thirtyDaysHence = today.plusDays(30);
        Long itemsExpiringSoon = dashboardRepository.countItemsExpiringSoon(companyId, thirtyDaysHence);
        Long expiredItems = dashboardRepository.countExpiredItems(companyId);

        Long compliantItems = dashboardRepository.countCompliantItems(companyId);
        Long totalItems = dashboardRepository.countTotalItems(companyId);
        Double compliancePercentage = totalItems > 0 ? (compliantItems.doubleValue() / totalItems) * 100 : 100.0;

        LocalDate ninetyDaysHence = today.plusDays(90);
        BigDecimal forecastRecharges = dashboardRepository.getForecastRechargesRevenue(companyId, ninetyDaysHence);

        BigDecimal overdueRevenue = dashboardRepository.getOverdueRevenue(companyId);
        Long overdueClientsCount = dashboardRepository.countOverdueClients(companyId);

        long condemnedCount = dashboardRepository.countCondemnedExpirations(
                companyId,
                ExpirationStatus.EXPIRED,
                startOfCurrentMonth,
                endOfCurrentMonth
        );

        return new DashboardDTO(
                totalClients,
                newClientsThisWeek,
                currentMonthRevenue,
                revenueGrowthPercentage,
                pendingOrders,
                itemsExpiringSoon,
                expiredItems,
                compliantItems,
                totalItems,
                compliancePercentage,
                forecastRecharges,
                overdueRevenue,
                overdueClientsCount,
                condemnedCount
        );
    }

    public List<UpcomingEquipmentExpirationDTO> getUpcomingEquipmentExpirations() {
        Company company = authUserService.getCompanyOrThrow();
        LocalDate thirtyDaysHence = LocalDate.now().plusDays(30);
        return dashboardRepository.findUpcomingEquipmentExpirations(company.getId(), thirtyDaysHence);
    }

    public List<MonthlySalesDTO> getSalesLast12Months() {
        Company company = authUserService.getCompanyOrThrow();
        LocalDate today = LocalDate.now();
        LocalDate twelveMonthsAgo = today.minusMonths(11).withDayOfMonth(1);

        return dashboardRepository.getSalesLast12Months(company.getId(), twelveMonthsAgo, today);
    }

    private Double calculatePercentageGrowth(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}