package com.igniscore.api.controller;

import com.igniscore.api.dto.sale.CreateSaleDTO;
import com.igniscore.api.dto.sale.SaleQueryDTO;
import com.igniscore.api.dto.sale.SaleResponseDTO;
import com.igniscore.api.model.Sale;
import com.igniscore.api.model.SaleStatus;
import com.igniscore.api.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

/**
 * GraphQL controller responsible for handling
 * sales-related operations.
 *
 * <p>This controller exposes GraphQL queries and mutations
 * for creating and retrieving sales records.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Create new sales</li>
 *     <li>Retrieve paginated sales data</li>
 *     <li>Delegate business rules to {@link SaleService}</li>
 * </ul>
 *
 * <p>All business validations and transactional logic
 * are handled by the service layer.
 */
@Controller
@RequiredArgsConstructor
public class SaleController {

    /**
     * Service responsible for sales business operations.
     */
    private final SaleService service;

    /**
     * GraphQL mutation responsible for creating a new sale.
     *
     * <p>The mutation receives a {@link CreateSaleDTO}
     * containing client information, payment method,
     * and sale items.
     *
     * @param input sale creation payload
     * @return persisted {@link Sale}
     */
    @MutationMapping
    public Sale storeSale(@Argument CreateSaleDTO input) {
        return service.store(input);
    }

    /**
     * GraphQL query responsible for retrieving
     * paginated sales records.
     *
     * <p>If pagination parameters are not provided,
     * default values are applied:
     * <ul>
     *     <li>page = 0</li>
     *     <li>size = 10</li>
     * </ul>
     *
     * <p>Results are sorted by ID in descending order.
     *
     * @param page requested page number
     * @param size number of records per page
     * @return paginated sales data
     */
    @QueryMapping
    public SaleQueryDTO sales(
            @Argument Integer page,
            @Argument Integer size
    ) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                Sort.by(Sort.Direction.DESC, "id")
        );

        return service.findAll(pageable);
    }

    @QueryMapping
    public List<Sale> salesByPeriod(
            @Argument Integer page,
            @Argument Integer size,
            @Argument LocalDate startDate,
            @Argument LocalDate endDate
    ) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                Sort.by(Sort.Direction.DESC, "id")
        );

        return service.findPerPeriod(
                startDate,
                endDate,
                pageable
        ).getContent();
    }

    @MutationMapping
    public SaleResponseDTO updateSaleStatus(
            @Argument Integer saleId,
            @Argument SaleStatus status
    ) {
        return service.updateSaleStatus(saleId, status);
    }

    @MutationMapping
    public Boolean deleteSale(@Argument Integer saleId) {
        return service.deleteSale(saleId);
    }
}