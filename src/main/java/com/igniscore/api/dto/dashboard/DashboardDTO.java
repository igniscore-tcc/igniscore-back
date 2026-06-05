package com.igniscore.api.dto.dashboard;

import java.math.BigDecimal;

public record DashboardDTO(
        Long totalClients,
        Long totalProducts,
        Long totalSales,
        BigDecimal monthlyRevenue,
        Long pendingOrders,
        Long expiringProducts,
        Long currentMonthExpirations,
        Long upcomingExpirations,
        Long expiredExpirations
) {}
