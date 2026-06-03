package com.igniscore.api.service;

import com.igniscore.api.dto.CreateSaleDTO;
import com.igniscore.api.dto.CreateSaleItemDTO;
import com.igniscore.api.model.*;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.repository.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service responsible for handling business rules
 * related to sales operations.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Create and persist sales</li>
 *     <li>Validate company ownership of clients and products</li>
 *     <li>Manage transactional consistency during sale creation</li>
 *     <li>Retrieve paginated sales data</li>
 * </ul>
 *
 * <p>This service operates in a multi-tenant context,
 * ensuring users only access resources associated
 * with their company.
 */
@Service
public class SaleService {

    /**
     * Repository responsible for sale persistence operations.
     */
    private final SaleRepository repository;

    /**
     * Repository responsible for client persistence operations.
     */
    private final ClientRepository clientRepository;

    /**
     * Repository responsible for product persistence operations.
     */
    private final ProductRepository productRepository;

    /**
     * Service responsible for retrieving the authenticated user context.
     */
    private final AuthenticatedUserService authUserService;

    /**
     * Creates a new instance of the sale service.
     *
     * @param repository sale repository
     * @param authUserService authenticated user service
     * @param clientRepository client repository
     * @param productRepository product repository
     */
    public SaleService(
            SaleRepository repository,
            AuthenticatedUserService authUserService,
            ClientRepository clientRepository,
            ProductRepository productRepository
    ) {
        this.repository = repository;
        this.authUserService = authUserService;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates and persists a new sale.
     *
     * <p>The operation performs multiple validations:
     * <ul>
     *     <li>Authenticated company existence</li>
     *     <li>Client existence</li>
     *     <li>Client ownership validation</li>
     *     <li>Product existence</li>
     *     <li>Product ownership validation</li>
     * </ul>
     *
     * <p>All sale items are associated with the generated sale
     * before persistence.
     *
     * <p>This method runs inside a transaction to guarantee
     * data consistency.
     *
     * @param dto payload containing sale information
     * @return persisted sale entity
     *
     * @throws RuntimeException when:
     * <ul>
     *     <li>Client is not found</li>
     *     <li>Client does not belong to the authenticated company</li>
     *     <li>Product is not found</li>
     *     <li>Product does not belong to the authenticated company</li>
     * </ul>
     */
    @Transactional
    public Sale store(CreateSaleDTO dto) {

        Company company = authUserService.getCompanyOrThrow();

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        /*
         * Prevents access to clients belonging
         * to another company.
         */
        if (!client.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException(
                    "Cliente não pertence à empresa"
            );
        }

        Sale sale = new Sale();

        sale.setCompany(company);
        sale.setClient(client);
        sale.setPaymentMethod(dto.getPaymentMethod());

        /*
         * Sets creation and due dates.
         * Due date is currently initialized
         * with the same value as the creation date.
         */
        sale.setDate(LocalDate.now());
        sale.setDueDate(LocalDate.now());

        /*
         * Newly created sales start as pending.
         */
        sale.setStatus(SaleStatus.PENDING);

        /*
         * Processes all sale items sent in the request.
         */
        for (CreateSaleItemDTO itemDTO : dto.getItems()) {

            Product product = productRepository
                    .findById(itemDTO.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Produto não encontrado"
                            ));

            /*
             * Prevents access to products belonging
             * to another company.
             */
            if (!product.getCompany().getId().equals(company.getId())) {
                throw new RuntimeException(
                        "Produto não pertence à empresa"
                );
            }

            /*
             * Creates a sale item instance using
             * product data and pricing information.
             */
            SaleItem item = new SaleItem(
                    product,
                    itemDTO.getQuantity(),
                    itemDTO.getUnitPrice()
            );

            /*
             * Associates the item with the sale.
             */
            sale.addItem(item);
        }

        /*
         * Persists the sale and all associated items.
         */
        return repository.save(sale);
    }

    /**
     * Retrieves paginated sales belonging
     * to the authenticated company.
     *
     * <p>This method is read-only transactional
     * to improve performance and avoid unnecessary
     * persistence context synchronization.
     *
     * @param pageable pagination configuration
     * @return paginated sales result
     */
    @Transactional(readOnly = true)
    public Page<Sale> findAll(Pageable pageable) {

        Company company = authUserService.getCompanyOrThrow();

        return repository.findByCompany(company, true, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Sale> findPerPeriod(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Company company = authUserService.getCompanyOrThrow();

        return repository.findByCompanyAndDateBetween(
                company,
                startDate,
                endDate,
                pageable
        );
    }
}