package com.igniscore.api.repository;

import com.igniscore.api.dto.company.CreateCompanyDTO;
import com.igniscore.api.dto.product.ProductStoreDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.model.ProductType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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


        createProduct(company, true);
        createProduct(company, true);
        createProduct(company, false);

        Page<Product> products =
                productRepository.findByCompanyAndStatusOrderByIdAsc(
                        company,
                        true,
                        PageRequest.of(0, 10)
                );

        assertEquals(2, products.getNumberOfElements());
        assertTrue(products.stream().allMatch(Product::getStatus));
    }

    @Test
    void findByCompanyAndStatus_returnsEmptyPage_whenNoActiveProducts() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dto);

        createProduct(company, false);
        createProduct(company, false);

        Page<Product> products = productRepository.findByCompanyAndStatusOrderByIdAsc(
                company, true, PageRequest.of(0, 10)
        );

        assertTrue(products.isEmpty());
        assertEquals(0, products.getTotalElements());
    }

    @Test
    void findByCompanyAndStatus_ordersResultsByIdAscending() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dto);

        Product p1 = createProduct(company, true);
        Product p2 = createProduct(company, true);
        Product p3 = createProduct(company, true);

        Page<Product> products = productRepository.findByCompanyAndStatusOrderByIdAsc(
                company, true, PageRequest.of(0, 10)
        );

        List<Product> content = products.getContent();
        assertEquals(3, content.size());
        assertEquals(p1.getId(), content.get(0).getId());
        assertEquals(p2.getId(), content.get(1).getId());
        assertEquals(p3.getId(), content.get(2).getId());
    }

    @Test
    void findByCompanyAndStatus_respectsPagination() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dto);

        for (int i = 0; i < 5; i++) {
            createProduct(company, true);
        }

        Page<Product> firstPage = productRepository.findByCompanyAndStatusOrderByIdAsc(
                company, true, PageRequest.of(0, 2)
        );
        Page<Product> secondPage = productRepository.findByCompanyAndStatusOrderByIdAsc(
                company, true, PageRequest.of(1, 2)
        );

        assertEquals(2, firstPage.getNumberOfElements());
        assertEquals(2, secondPage.getNumberOfElements());
        assertEquals(5, firstPage.getTotalElements());
        assertEquals(3, firstPage.getTotalPages());
    }

    @Test
    void findByCompanyAndStatus_doesNotMixProductsFromDifferentCompanies() {
        String cnpj1 = "0000000000";
        String cnpj2 = "2222222222";
        CreateCompanyDTO dto1 = new CreateCompanyDTO("IgnisCore", cnpj1, "12345678", "SP", "igniscore1@tcc.com", "0000000000");
        CreateCompanyDTO dto2 = new CreateCompanyDTO("IgnisCore2", cnpj2, "87654321", "SP", "igniscore2@tcc.com", "1111111111");

        Company companyA = createCompany(dto1);
        Company companyB = createCompany(dto2);

        createProduct(companyA, true);
        createProduct(companyB, true);
        createProduct(companyB, true);

        Page<Product> productsA = productRepository.findByCompanyAndStatusOrderByIdAsc(
                companyA, true, PageRequest.of(0, 10)
        );
        Page<Product> productsB = productRepository.findByCompanyAndStatusOrderByIdAsc(
                companyB, true, PageRequest.of(0, 10)
        );

        assertEquals(1, productsA.getTotalElements());
        assertEquals(2, productsB.getTotalElements());
    }

    @Test
    void save_persistsProductWithGeneratedId() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto1 = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dto1);

        ProductStoreDTO dto = new ProductStoreDTO(
                "Extintor",
                ProductType.EXTINGUISHER,
                LocalDate.now().plusMonths(6),
                "LOTE-001",
                BigDecimal.valueOf(99.90)
        );
        Product product = new Product(dto, company);

        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());
        assertEquals("Extintor", saved.getName());
        assertEquals(ProductType.EXTINGUISHER, saved.getType());
        assertTrue(saved.getStatus());
    }

    @Test
    void findById_returnsProduct_whenExists() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dto);
        Product product = createProduct(company, true);

        var found = productRepository.findById(product.getId());

        assertTrue(found.isPresent());
        assertEquals(product.getId(), found.get().getId());
    }

    @Test
    void deactivate_andSave_persistsStatusChange() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dto);
        Product product = createProduct(company, true);

        product.deactivate();
        productRepository.save(product);
        entityManager.flush();
        entityManager.clear();

        Product reloaded = productRepository.findById(product.getId()).orElseThrow();
        assertFalse(reloaded.getStatus());
    }

    // --- helpers ---


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

    private Company createCompany(CreateCompanyDTO dto) {
        Company company = new Company(dto);

        this.entityManager.persist(company);
        entityManager.flush();

        return company;

    }

}