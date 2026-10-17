
package com.igniscore.api.service;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.Plan;
import com.igniscore.api.model.Subscription;
import com.igniscore.api.model.SubscriptionStatus;
import com.igniscore.api.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

@AllArgsConstructor
@Service
public class PlanLimitService {

    private final SubscriptionRepository subscriptionRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SaleRepository saleRepository;

    @Transactional(readOnly = true)
    public Plan getCurrentPlan() {
        Company company = authenticatedUserService.getCompanyOrThrow();

        return subscriptionRepository
                .findByCompany_IdOrderByUpdatedAtDesc(company.getId())
                .stream()
                .filter(this::hasAccess)
                .max(Comparator.comparing(
                        Subscription::getUpdatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(subscription -> subscription.getPlanPrice().getPlan())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Sua empresa não possui uma assinatura com acesso permitido."
                        )
                );
    }

    private boolean hasAccess(Subscription subscription) {
        SubscriptionStatus status = subscription.getStatus();
        LocalDateTime now = LocalDateTime.now();

        if (status == SubscriptionStatus.ACTIVE) {
            return true;
        }

        if (status == SubscriptionStatus.TRIALING) {
            return subscription.getTrialEnd() != null
                    && subscription.getTrialEnd().isAfter(now);
        }

        return status == SubscriptionStatus.CANCELED
                && Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd())
                && subscription.getCurrentPeriodEnd() != null
                && subscription.getCurrentPeriodEnd().isAfter(now);
    }

    public void checkLimit(
            String resource,
            Integer limit,
            long currentCount
    ) {
        if (limit != null && currentCount >= limit) {
            Plan plan = getCurrentPlan();

            throw new PlanLimitExceededException(
                    "Você atingiu o limite de " + limit
                            + " " + resource
                            + " do plano " + plan.getName()
                            + ". Faça upgrade para continuar."
            );
        }
    }

    @Transactional(readOnly = true)
    public void checkClientLimit() {
        Company company = authenticatedUserService.getCompanyOrThrow();
        Plan plan = getCurrentPlan();

        long count = clientRepository.countByCompanyAndDeletedAtIsNull(company);

        checkLimit("clientes", plan.getMaxClients(), count);
    }

    @Transactional(readOnly = true)
    public void checkProductLimit() {
        Company company = authenticatedUserService.getCompanyOrThrow();
        Plan plan = getCurrentPlan();

        long count = productRepository.countByCompanyAndDeletedAtIsNull(company);

        checkLimit("produtos", plan.getMaxProducts(), count);
    }

    @Transactional(readOnly = true)
    public void checkUserLimit() {
        Company company = authenticatedUserService.getCompanyOrThrow();
        Plan plan = getCurrentPlan();

        long count = userRepository.countByCompanyAndDeletedAtIsNull(company);

        checkLimit("usuários", plan.getMaxUsers(), count);
    }


    @Transactional(readOnly = true)
    public void checkSaleLimit() {
        Company company = authenticatedUserService.getCompanyOrThrow();
        Plan plan = getCurrentPlan();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());

        long count = saleRepository.countByCompanyAndDateBetweenAndDeletedAtIsNull(
                company,
                startDate,
                endDate
        );

        checkLimit("vendas neste mês", plan.getMaxSalesPerMonth(), count);
    }

}