package com.igniscore.api.service;

import com.igniscore.api.dto.sale.CreateSaleDTO;
import com.igniscore.api.dto.sale.CreateSaleItemDTO;
import com.igniscore.api.dto.sale.SaleQueryDTO;
import com.igniscore.api.dto.sale.SaleResponseDTO;
import com.igniscore.api.model.*;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.repository.ExpirationRepository;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.repository.SaleRepository;
import com.igniscore.api.service.subscription.RequiresSubscriptionAccess;
import com.igniscore.api.utils.AuditUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
 *     <li>Record sale operations in the audit log</li>
 * </ul>
 *
 * <p>This service operates in a multi-tenant context,
 * ensuring users only access resources associated
 * with their company.
 */
@RequiresSubscriptionAccess
@Service
@RequiredArgsConstructor
public class SaleService {

    private static final String ENTITY_NAME = "Sale";

    private final SaleRepository repository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ExpirationRepository expirationRepository;
    private final AuthenticatedUserService authUserService;
    private final AuditUtils audit;

    /**
     * Creates and persists a new sale.
     *
     * @param dto payload containing sale information
     * @return persisted sale entity
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "sales", allEntries = true),
            @CacheEvict(value = "salesPerPeriod", allEntries = true)
    })
    public Sale store(CreateSaleDTO dto) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Client client = getClientForCompany(dto.getClientId(), company);

        Sale sale = createSale(
                company,
                client,
                dto.getType(),
                dto.getDocument(),
                dto.getPaymentMethod()
        );

        Map<Integer, Product> products = loadAndValidateProducts(dto.getItems());

        validateProductsOwnership(products, company);
        addItemsToSale(sale, dto.getItems(), products);

        BigDecimal discount = dto.getDiscount() != null
                ? dto.getDiscount()
                : BigDecimal.ZERO;

        sale.applyDiscount(discount);

        Sale saved = repository.save(sale);

        Expiration expiration = new Expiration(
                saved,
                ExpirationStatus.NORMAL,
                company
        );

        expirationRepository.save(expiration);

        auditSale(user, company, "Create", null, saved);

        return saved;
    }

    /**
     * Retrieves paginated sales belonging to the authenticated company.
     *
     * @param pageable pagination configuration
     * @return paginated sales result
     */
    @Cacheable(
            value = "sales",
            key = "@cacheKeyService.salesKey(#pageable)",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public SaleQueryDTO findAll(Pageable pageable) {
        Company company = authUserService.getCompanyOrThrow();

        Page<Sale> page = repository.findByCompanyAndDeletedAtIsNull(
                company,
                pageable
        );

        List<SaleResponseDTO> sales = page.getContent()
                .stream()
                .map(SaleResponseDTO::new)
                .toList();

        return new SaleQueryDTO(
                sales,
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    /**
     * Retrieves sales for the specified date range.
     *
     * @param startDate beginning of the period
     * @param endDate end of the period
     * @param pageable pagination configuration
     * @return page of sales within the requested period
     */
    @Cacheable(
            value = "salesPerPeriod",
            key = "@cacheKeyService.salesPerPeriodKey(#startDate, #endDate, #pageable)",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public Page<Sale> findPerPeriod(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Company company = authUserService.getCompanyOrThrow();

        return repository.findByCompanyAndDateBetweenAndDeletedAtIsNull(
                company,
                startDate,
                endDate,
                pageable
        );
    }

    /**
     * Updates the status of a sale belonging to the authenticated company.
     *
     * @param saleId identifier of the sale
     * @param status new sale status
     * @return updated sale response
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "sales", allEntries = true),
            @CacheEvict(value = "salesPerPeriod", allEntries = true)
    })
    public SaleResponseDTO updateSaleStatus(
            Integer saleId,
            SaleStatus status
    ) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Sale sale = getSaleForCompany(saleId, company);
        Map<String, Object> oldData = saleSnapshot(sale);

        sale.setStatus(status);

        Sale saved = repository.save(sale);

        auditSale(user, company, "Update", oldData, saleSnapshot(saved));

        return new SaleResponseDTO(saved);
    }

    /**
     * Logically deletes a sale belonging to the authenticated company.
     *
     * @param saleId identifier of the sale
     * @return true when the sale is marked as deleted
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "sales", allEntries = true),
            @CacheEvict(value = "salesPerPeriod", allEntries = true)
    })
    public Boolean deleteSale(Integer saleId) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Sale sale = getSaleForCompany(saleId, company);
        Map<String, Object> oldData = saleSnapshot(sale);

        sale.setDeletedAt(LocalDateTime.now());

        Sale saved = repository.save(sale);

        auditSale(user, company, "Delete", oldData, saleSnapshot(saved));

        return true;
    }

    private Sale getSaleForCompany(Integer saleId, Company company) {
        Sale sale = repository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found"));

        if (sale.getCompany() == null
                || !sale.getCompany().getId().equals(company.getId())) {
            throw new EntityNotFoundException(
                    "Sale does not belong to the company"
            );
        }

        return sale;
    }

    private Map<Integer, Product> loadAndValidateProducts(
            List<CreateSaleItemDTO> items
    ) {
        List<Integer> productIds = items.stream()
                .map(CreateSaleItemDTO::getProductId)
                .distinct()
                .toList();

        Map<Integer, Product> products = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        Function.identity()
                ));

        if (products.size() != productIds.size()) {
            throw new EntityNotFoundException(
                    "One or more products were not found."
            );
        }

        return products;
    }

    private void validateProductsOwnership(
            Map<Integer, Product> products,
            Company company
    ) {
        for (Product product : products.values()) {
            if (product.getCompany() == null
                    || !product.getCompany().getId().equals(company.getId())) {
                throw new EntityNotFoundException(
                        "The product does not belong to the company."
                );
            }
        }
    }

    private Client getClientForCompany(
            Integer clientId,
            Company company
    ) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (client.getCompany() == null
                || !client.getCompany().getId().equals(company.getId())) {
            throw new EntityNotFoundException(
                    "Client does not belong to the company"
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
            Product product = products.get(itemDTO.getProductId());

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
            SaleDocument type,
            String document,
            SaleType paymentMethod
    ) {
        LocalDate today = LocalDate.now();

        return new Sale(
                today,
                SaleStatus.PENDING,
                type,
                document,
                today.plusYears(1),
                company,
                client,
                paymentMethod
        );
    }

    private Map<String, Object> saleSnapshot(Sale sale) {
        if (sale == null) {
            return null;
        }

        Map<String, Object> snapshot = new LinkedHashMap<>();

        snapshot.put("id", sale.getId());
        snapshot.put("numberSale", sale.getNumberSale());
        snapshot.put("quantityItems", sale.getQuantityItems());
        snapshot.put("discount", sale.getDiscount());
        snapshot.put("total", sale.getTotal());
        snapshot.put("date", sale.getDate());
        snapshot.put("paymentMethod", sale.getPaymentMethod());
        snapshot.put("status", sale.getStatus());
        snapshot.put("type", sale.getType());
        snapshot.put("document", sale.getDocument());
        snapshot.put("dueDate", sale.getDueDate());
        snapshot.put("clientId", sale.getClient() != null
                ? sale.getClient().getId()
                : null);

        return snapshot;
    }

    /**
     * Records a sale operation in the audit log.
     *
     * @param user authenticated user
     * @param company associated company
     * @param action operation performed
     * @param oldData previous sale state, if applicable
     * @param newData resulting sale state, if applicable
     */
    private void auditSale(
            User user,
            Company company,
            String action,
            Object oldData,
            Object newData
    ) {
        if (oldData instanceof Map<?, ?> oldMap
                && newData instanceof Map<?, ?> newMap) {
            Map<String, Object> oldSnapshot = castSnapshot(oldMap);
            Map<String, Object> newSnapshot = castSnapshot(newMap);

            Map<String, Object> oldChanges = new LinkedHashMap<>();
            Map<String, Object> newChanges = new LinkedHashMap<>();

            for (String key : newSnapshot.keySet()) {
                Object oldValue = oldSnapshot.get(key);
                Object newValue = newSnapshot.get(key);

                if (!Objects.equals(oldValue, newValue)) {
                    oldChanges.put(key, oldValue);
                    newChanges.put(key, newValue);
                }
            }

            audit.newAudit(
                    user,
                    company,
                    ENTITY_NAME,
                    action,
                    oldChanges,
                    newChanges
            );
            return;
        }

        audit.newAudit(
                user,
                company,
                ENTITY_NAME,
                action,
                oldData,
                newData instanceof Sale sale ? saleSnapshot(sale) : newData
        );
    }

    private Map<String, Object> castSnapshot(Map<?, ?> source) {
        Map<String, Object> snapshot = new LinkedHashMap<>();

        source.forEach((key, value) -> snapshot.put(String.valueOf(key), value));

        return snapshot;
    }
}
