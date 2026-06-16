package com.igniscore.api.service;

import com.igniscore.api.dto.sale.CreateSaleDTO;
import com.igniscore.api.dto.sale.CreateSaleItemDTO;
import com.igniscore.api.dto.sale.SaleQueryDTO;
import com.igniscore.api.model.*;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.repository.ExpirationRepository;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.repository.SaleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private final ExpirationRepository expirationRepository;

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
            ProductRepository productRepository,
            ExpirationRepository expirationRepository
    ) {
        this.repository = repository;
        this.authUserService = authUserService;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.expirationRepository = expirationRepository;
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
    @CacheEvict(value = "sales", allEntries = true)
    public Sale store(CreateSaleDTO dto) {

        Company company = authUserService.getCompanyOrThrow();

        Client client =
                getClientForCompany(
                        dto.getClientId(),
                        company
                );

        Sale sale = createSale(
                company,
                client,
                dto.getPaymentMethod()
        );

        Map<Integer, Product> products =
                loadAndValidateProducts(dto.getItems());

        validateProductsOwnership(
                products,
                company
        );

        addItemsToSale(
                sale,
                dto.getItems(),
                products
        );

        repository.save(sale);

        var expiration = new Expiration(sale, ExpirationStatus.NORMAL);

        expirationRepository.save(expiration);

        /*
         * Persists the sale and all associated items.
         */
        return sale;
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
    /*
    @Cacheable(
            value = "sales",
            key = "@cacheKeyService.salesKey(#pageable)",
            unless = "#result == null"
    )
     */
    @Transactional(readOnly = true)
    public SaleQueryDTO findAll(Pageable pageable) {

        Company company = authUserService.getCompanyOrThrow();

        Page<Sale> page = repository.findByCompany(company, true, pageable);
        return new SaleQueryDTO(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    /*
    @Cacheable(
            value = "sales",
            key = "@cacheKeyService.salesKey(#pageable)",
            unless = "#result == null"
    )
     */
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

    private Map<Integer, Product> loadAndValidateProducts(
            List<CreateSaleItemDTO> items
    ) {

        List<Integer> productIds = items.stream()
                .map(CreateSaleItemDTO::getProductId)
                .distinct()
                .toList();

        Map<Integer, Product> products =
                productRepository.findAllById(productIds)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Product::getId,
                                        Function.identity()
                                )
                        );

        if (products.size() != productIds.size()) {
            throw new RuntimeException(
                    "Um ou mais produtos não foram encontrados"
            );
        }

        return products;
    }

    private void validateProductsOwnership(
            Map<Integer, Product> products,
            Company company
    ) {

        for (Product product : products.values()) {

            if (!product.getCompany().getId().equals(company.getId())) {
                throw new RuntimeException(
                        "Produto não pertence à empresa"
                );
            }
        }
    }

    private Client getClientForCompany(
            Integer clientId,
            Company company
    ) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        if (!client.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException(
                    "Cliente não pertence à empresa"
            );
        }

        return client;
    }

    private void addItemsToSale(
            Sale sale,
            List<CreateSaleItemDTO> items,
            Map<Integer, Product> products
    ) {

        for (CreateSaleItemDTO itemDTO : items) {

            Product product =
                    products.get(itemDTO.getProductId());

            SaleItem item = new SaleItem(
                    product,
                    itemDTO.getQuantity(),
                    itemDTO.getUnitPrice()
            );

            sale.addItem(item);
        }
    }

    private Sale createSale(
            Company company,
            Client client,
            PaymentMethod paymentMethod
    ) {
        LocalDate today = LocalDate.now();

        return new Sale(today, SaleStatus.PENDING, today.plusYears(1), company, client, paymentMethod);
    }
}