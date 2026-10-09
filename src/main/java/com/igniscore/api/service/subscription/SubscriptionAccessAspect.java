package com.igniscore.api.service.subscription;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class SubscriptionAccessAspect {

    private final SubscriptionAccessService subscriptionAccessService;

    @Around("@annotation(com.igniscore.api.service.subscription.RequiresSubscriptionAccess) || @within(com.igniscore.api.service.subscription.RequiresSubscriptionAccess)")
    public Object checkSubscriptionAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("AOP: verificando assinatura");

        subscriptionAccessService.requireBusinessAccess();

        System.out.println("AOP: assinatura autorizada");

        return joinPoint.proceed();
    }
}