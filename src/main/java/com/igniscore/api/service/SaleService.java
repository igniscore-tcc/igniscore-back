package com.igniscore.api.service;

import com.igniscore.api.model.*;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.repository.SaleItemRepository;
import com.igniscore.api.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository repository;
    private final SaleItemRepository saleItemRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final AuthenticatedUserService authUserService;

    public SaleService(SaleRepository repository,
                       AuthenticatedUserService authUserService,
                       ClientRepository clientRepository,
                       ProductRepository productRepository,
                       SaleItemRepository saleItemRepository
    ) {
        this.repository = repository;
        this.authUserService = authUserService;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.saleItemRepository = saleItemRepository;
    }

    @Transactional
    public Sale store(Integer clientId, String paymentMethod, List<SaleItem> itemsRequest) {

        Company company = authUserService.getCompanyOrThrow();

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (!client.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Cliente não pertence à empresa");
        }

        if (itemsRequest == null || itemsRequest.isEmpty()) {
            throw new RuntimeException("Venda deve possuir itens");
        }

        Sale sale = new Sale();
        sale.setCompany(company);
        sale.setClient(client);
        sale.setType(paymentMethod);

        sale.setDate(LocalDate.now());
        sale.setStatus(SaleStatus.PENDING);
        sale.setDiscount(BigDecimal.ZERO);
        sale.setDue_date(LocalDate.now());
        sale.setQuantityItems(0);
        sale.setTotal(BigDecimal.ZERO);

        sale = repository.save(sale);

        BigDecimal total = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (SaleItem req : itemsRequest) {

            Product product = productRepository.findById(req.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + req.getProduct().getId()));

            if (!product.getCompany().getId().equals(company.getId())) {
                throw new RuntimeException("Produto não pertence à empresa");
            }

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setQuantity(req.getQuantity());
            item.setUnitPrice(req.getUnitPrice());

            BigDecimal itemTotal = req.getUnitPrice()
                    .multiply(BigDecimal.valueOf(req.getQuantity()));

            item.setTotal(itemTotal);

            total = total.add(itemTotal);
            totalQuantity += req.getQuantity();

            saleItemRepository.save(item);
        }

        sale.setQuantityItems(totalQuantity);
        sale.setTotal(total);

        return repository.save(sale);
    }
}
