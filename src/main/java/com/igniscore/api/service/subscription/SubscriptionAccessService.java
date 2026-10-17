package com.igniscore.api.service.subscription;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.Subscription;
import com.igniscore.api.model.SubscriptionStatus;
import com.igniscore.api.repository.SubscriptionRepository;
import com.igniscore.api.service.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionAccessService {

    private static final int GRACE_PERIOD_DAYS = 7;

    private final AuthenticatedUserService authenticatedUserService;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public SubscriptionAccessResult evaluateCurrentCompany() {
        Company company = authenticatedUserService.getCompanyOrThrow();

        if (company == null) {
            return blocked("Nenhuma empresa está vinculada ao usuário.");
        }

        List<Subscription> subscriptions =
                subscriptionRepository.findByCompany_IdOrderByUpdatedAtDesc(
                        company.getId()
                );

        if (subscriptions.isEmpty()) {
            return blocked("Esta empresa não possui uma assinatura.");
        }

        LocalDateTime now = LocalDateTime.now();

        Subscription subscription = subscriptions.get(0);

        SubscriptionStatus status = subscription.getStatus();

        if (status == null) {
            return blocked("Não foi possível validar a assinatura.");
        }

        return switch (status) {
            case ACTIVE -> evaluateActive(subscription, now);
            case TRIALING -> evaluateTrial(subscription, now);
            case PAST_DUE -> evaluatePastDue(subscription, now);
            case CANCELED -> evaluateCanceled(subscription, now);
            default -> blocked("A assinatura não permite acesso ao sistema.");
        };
    }

    private SubscriptionAccessResult evaluateActive(
            Subscription subscription,
            LocalDateTime now
    ) {
        LocalDateTime periodEnd = subscription.getCurrentPeriodEnd();

        if (periodEnd != null && !now.isBefore(periodEnd)) {
            return blocked("O período da assinatura expirou.");
        }

        return allowed("Assinatura ativa.");
    }

    private SubscriptionAccessResult evaluateTrial(
            Subscription subscription,
            LocalDateTime now
    ) {
        LocalDateTime trialEnd = subscription.getTrialEnd();

        if (trialEnd == null || !now.isBefore(trialEnd)) {
            return blocked("O período de teste terminou.");
        }

        return allowed("Período de teste ativo.");
    }

    private SubscriptionAccessResult evaluatePastDue(
            Subscription subscription,
            LocalDateTime now
    ) {
        LocalDateTime pastDueSince = subscription.getPastDueSince();

        if (pastDueSince == null) {
            return blocked(
                    "A data inicial da inadimplência ainda não foi registrada."
            );
        }

        LocalDateTime gracePeriodEndsAt =
                pastDueSince.plusDays(GRACE_PERIOD_DAYS);

        if (!now.isBefore(gracePeriodEndsAt)) {
            return blocked(
                    "O prazo para regularizar o pagamento terminou."
            );
        }

        return new SubscriptionAccessResult(
                SubscriptionAccessStatus.PAYMENT_WARNING,
                "Pagamento pendente. Regularize sua assinatura antes do fim do prazo.",
                gracePeriodEndsAt
        );
    }

    private SubscriptionAccessResult evaluateCanceled(
            Subscription subscription,
            LocalDateTime now
    ) {
        LocalDateTime periodEnd = subscription.getCurrentPeriodEnd();

        if (Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd())
                && periodEnd != null
                && now.isBefore(periodEnd)) {
            return allowed("A assinatura permanece válida até o fim do período.");
        }

        return blocked("A assinatura foi cancelada.");
    }

    private SubscriptionAccessResult allowed(String message) {
        return new SubscriptionAccessResult(
                SubscriptionAccessStatus.ALLOWED,
                message,
                null
        );
    }

    private SubscriptionAccessResult blocked(String message) {
        return new SubscriptionAccessResult(
                SubscriptionAccessStatus.BLOCKED,
                message,
                null
        );
    }

    public boolean canAccessBusinessFeatures(
            Subscription subscription,
            LocalDateTime now
    ) {
        if (subscription == null) {
            return false;
        }

        return switch (subscription.getStatus()) {
            case ACTIVE, TRIALING -> true;

            case PAST_DUE -> {
                LocalDateTime pastDueSince =
                        subscription.getPastDueSince();

                yield pastDueSince != null
                        && !now.isAfter(pastDueSince.plusDays(7));
            }

            default -> false;
        };
    }

    @Transactional(readOnly = true)
    public SubscriptionAccessResult requireBusinessAccess() {
        SubscriptionAccessResult result = evaluateCurrentCompany();

        if (!result.canAccessBusinessFeatures()) {
            throw new SubscriptionAccessDeniedException(result.message());
        }

        return result;
    }
}