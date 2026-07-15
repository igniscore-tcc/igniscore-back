package com.igniscore.api.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service responsible for generating cache keys used in application caching strategies.
 *
 * <p>This service centralizes cache key construction logic to ensure
 * consistency, maintainability, and tenant isolation across cached operations.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Generate deterministic cache keys</li>
 *     <li>Enforce tenant-aware cache segregation</li>
 *     <li>Support pageable resource caching</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Cache keys are scoped by authenticated company identifier</li>
 *     <li>Pagination metadata is included to avoid cache collisions between pages</li>
 *     <li>Intended for use with Spring Cache annotations</li>
 * </ul>
 */
@Service
public class CacheKeyService {

    /**
     * Service responsible for resolving the authenticated user's company context.
     */
    private final AuthenticatedUserService authUserService;

    /**
     * Creates a new cache key service instance.
     *
     * @param authUserService authenticated user context resolver
     */
    public CacheKeyService(AuthenticatedUserService authUserService) {
        this.authUserService = authUserService;
    }

    /**
     * Generates a cache key for paginated product queries.
     *
     * <p>The generated key includes:
     * <ul>
     *     <li>Authenticated company identifier</li>
     *     <li>Requested page number</li>
     *     <li>Requested page size</li>
     * </ul>
     *
     * <p>This strategy guarantees cache isolation between tenants
     * and prevents collisions between paginated responses.
     *
     * <p>Example generated key:
     * <pre>
     * company:10:page:0:size:20
     * </pre>
     *
     * @param pageable pagination metadata
     * @return deterministic cache key for product queries
     */
    public String productsKey(Pageable pageable) {

        Integer companyId = authUserService.getCompanyOrThrow().getId();

        return "company:" + companyId +
                ":page:" + pageable.getPageNumber() +
                ":size:" + pageable.getPageSize();
    }

    public String clientsKey(Pageable pageable) {

        Integer companyId = authUserService.getCompanyOrThrow().getId();

        return "company:" + companyId +
                ":page:" + pageable.getPageNumber() +
                ":size:" + pageable.getPageSize();
    }

    public String salesKey(Pageable pageable) {
        Integer companyId = authUserService.getCompanyOrThrow().getId();

        return "company:" + companyId +
                ":page:" + pageable.getPageNumber() +
                ":size:" + pageable.getPageSize();
    }

    public String salesPerPeriodKey(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Integer companyId = authUserService.getCompanyOrThrow().getId();

        return "company:" + companyId +
                ":start:" + startDate +
                ":end:" + endDate +
                ":page:" + pageable.getPageNumber() +
                ":size:" + pageable.getPageSize();
    }
}