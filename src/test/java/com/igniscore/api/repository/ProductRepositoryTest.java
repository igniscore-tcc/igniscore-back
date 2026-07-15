package com.igniscore.api.repository;

import com.igniscore.api.dto.company.CreateCompanyDTO;
import com.igniscore.api.dto.product.ProductStoreDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.model.ProductType;
import jakarta.persistence.EntityManager;
import org.hibernate.query.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void findByCompanyAndStatusOrderByIdAsc() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dto);

        Product active1 = createProduct(company, true);
        Product active2 = createProduct(company, true);
        createProduct(company, false);

        var pageable = PageRequest.of(0, 10);

        org.springframework.data.domain.Page<Product> result =
                productRepository.findByCompanyAndStatusOrderByIdAsc(
                        company,
                        true,
                        pageable
                );

        assertEquals(2, result.getTotalElements());

        assertEquals(active1.getId(),
                result.getContent().get(0).getId());

        assertEquals(active2.getId(),
                result.getContent().get(1).getId());

        assertTrue(
                result.getContent()
                        .stream()
                        .allMatch(Product::getStatus)
        );
    }

    @Test
    void shouldReturnProductWhenIdExistsAndStatusIsTrue() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");
        Company company = createCompany(dto);

        Product product = createProduct(company, true);

        var result = productRepository.findByIdAndStatusTrue(
                product.getId()
        );

        assertTrue(result.isPresent());

        assertEquals(
                product.getId(),
                result.get().getId()
        );
    }

    @Test
    void shouldNotReturnProductWhenStatusIsFalse() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");
        Company company = createCompany(dto);

        Product product = createProduct(company, false);

        var result = productRepository.findByIdAndStatusTrue(
                product.getId()
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        var result = productRepository.findByIdAndStatusTrue(99999);
        assertTrue(result.isEmpty());
    }

    private Company createCompany(CreateCompanyDTO dto) {
        Company company = new Company(dto);
        this.entityManager.persist(company);
        entityManager.flush();

        return company;

    }

    private Product createProduct(Company company, boolean status) {

        ProductStoreDTO dto = new ProductStoreDTO(
                "Extintor",
                ProductType.EXTINGUISHER,
                LocalDate.now().plusMonths(6),
                "LOTE-" + System.nanoTime(),
                BigDecimal.valueOf(12.50)
        );

        Product product = new Product(dto, company);

        if (!status) {
            product.deactivate();
        }

        entityManager.persist(product);
        entityManager.flush();

        return product;
    }
}