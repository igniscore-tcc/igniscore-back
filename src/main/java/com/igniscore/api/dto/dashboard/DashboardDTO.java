package com.igniscore.api.dto.dashboard;

import java.math.BigDecimal;

public record DashboardDTO(
        Long totalClients,
        Long newClientsThisWeek,
        BigDecimal monthlyRevenue,
        Double revenueGrowthPercentage,
        Long pendingOrders,
        Long itemsExpiringSoon,
        Long expiredItems,
        Long compliantItems,
        Long totalItems,
        Double compliancePercentage,
        BigDecimal forecastRecharges,
        BigDecimal overdueRevenue,
        Long overdueClientsCount,
        Long condemnedItemsThisMonth
) {}