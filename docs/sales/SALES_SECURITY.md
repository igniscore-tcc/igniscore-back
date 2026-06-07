# Guia de Segurança e Multi-Tenancy – Módulo Sales

## Índice

1. [Arquitetura Multi-Tenant](#1-arquitetura-multi-tenant)
2. [Fluxo de Autenticação e Autorização](#2-fluxo-de-autenticação-e-autorização)
3. [Filtragem de Dados por Tenant](#3-filtragem-de-dados-por-tenant)
4. [Validações de Segurança por Operação](#4-validações-de-segurança-por-operação)
5. [Tratamento Seguro de Erros](#5-tratamento-seguro-de-erros)
6. [Auditoria e Logging](#6-auditoria-e-logging)
7. [Proteção contra Ataques Comuns](#7-proteção-contra-ataques-comuns)
8. [Testes de Segurança](#8-testes-de-segurança)
9. [Checklist de Deploy](#9-checklist-de-deploy)

---

## 1. Arquitetura Multi-Tenant

### Database-per-Schema

O Igniscore utiliza arquitetura **database-per-schema** para isolamento multi-tenant:

```
┌─────────────────────────────────────────┐
│        Banco de Dados (igniscore)       │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐   │
│  │   Schema Empresa A              │   │
│  │  ├── sales (Empresa A)          │   │
│  │  ├── sale_items (Empresa A)     │   │
│  │  └── [outras tabelas]           │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │   Schema Empresa B              │   │
│  │  ├── sales (Empresa B)          │   │
│  │  ├── sale_items (Empresa B)     │   │
│  │  └── [outras tabelas]           │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │   Schema Empresa C              │   │
│  │  ├── sales (Empresa C)          │   │
│  │  ├── sale_items (Empresa C)     │   │
│  │  └── [outras tabelas]           │   │
│  └─────────────────────────────────┘   │
│                                         │
└─────────────────────────────────────────┘
```

### Garantias de Isolamento

- **Isolamento Physical**: Cada empresa tem schema separado
- **Isolamento Logical**: Dupla validação de `fk_id_company`
- **Isolamento Temporal**: Dados de histórico isolados por tenant
- **Isolamento de Acesso**: Usuário autenticado só acessa seu schema

---

## 2. Fluxo de Autenticação e Autorização

### Fluxo de Autenticação JWT

```
┌─────────────────────────────────────────────────────┐
│ 1. Usuário faz login com email e senha              │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 2. Sistema valida credenciais no banco              │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 3. Token JWT é gerado com:                          │
│    - user_id                                         │
│    - email                                           │
│    - company_id (tenant)                            │
│    - roles                                           │
│    - expiration (24h)                               │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 4. Token é retornado ao cliente                     │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 5. Cliente inclui token em Authorization header     │
│    Authorization: Bearer eyJhbGciOiJIUzI1NiIs...   │
└─────────────────────────────────────────────────────┘
```

### Validação de Token

```java
@Component
public class JwtTokenProvider {

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Integer extractCompanyId(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("company_id", Integer.class);
    }
}
```

### Contexto de Autorização

```java
@Service
public class AuthenticatedUserService {

    public Integer getAuthenticatedCompanyId() {
        Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        return ((AuthenticatedUserDetails) auth.getPrincipal())
            .getCompanyId();
    }

    public User getAuthenticatedUser() {
        // Retorna usuário do contexto
    }
}
```

---

## 3. Filtragem de Dados por Tenant

### Filtro Obrigatório em Todas as Queries

```java
@Service
public class SaleService {

    private final SaleRepository repository;
    private final AuthenticatedUserService authService;

    /**
     * Obtém o company_id do usuário autenticado
     */
    private Integer getAuthenticatedCompanyId() {
        return authService.getAuthenticatedCompanyId();
    }

    /**
     * Query com filtro de tenant automático
     */
    public Sale findById(Integer id) {
        return repository.findById(id)
            .filter(sale -> isOwnedByAuthenticatedCompany(sale))
            .orElseThrow(() -> new EntityNotFoundException("Sale not found"));
    }

    /**
     * Validação de propriedade
     */
    private boolean isOwnedByAuthenticatedCompany(Sale sale) {
        return sale.getCompany().getId()
            .equals(getAuthenticatedCompanyId());
    }

    /**
     * Query com paginação
     */
    public Page<Sale> findByCompany(Pageable pageable) {
        Integer companyId = getAuthenticatedCompanyId();
        return repository.findByCompanyId(companyId, pageable);
    }
}
```

### Queries Customizadas com Filtro

```java
@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {

    /**
     * Busca vendas por empresa (filtro obrigatório)
     */
    Page<Sale> findByCompanyId(Integer companyId, Pageable pageable);

    /**
     * Busca vendas por cliente dentro de uma empresa
     */
    @Query("SELECT s FROM Sale s " +
           "WHERE s.company.id = :companyId " +
           "AND s.client.id = :clientId " +
           "ORDER BY s.date DESC")
    Page<Sale> findByClientAndCompany(
        @Param("companyId") Integer companyId,
        @Param("clientId") Integer clientId,
        Pageable pageable
    );
}
```

### Garantias de Filtro

- ✅ Filtro é aplicado em **TODA** query
- ✅ Filtro é aplicado em **TODA** mutation
- ✅ Filtro não pode ser bypassado
- ✅ Filtro usa índices para performance

---

## 4. Validações de Segurança por Operação

### CREATE (storeSale)

**Fluxo de validação:**

```
┌──────────────────────────────┐
│ 1. JWT Token valido?         │ → NÃO → 401 Unauthorized
└────────────┬─────────────────┘
             │ SIM
┌────────────▼─────────────────┐
│ 2. Usuário autenticado?      │ → NÃO → 401 Unauthorized
└────────────┬─────────────────┘
             │ SIM
┌────────────▼─────────────────┐
│ 3. Cliente existe?           │ → NÃO → 404 Not Found
└────────────┬─────────────────┘
             │ SIM
┌────────────▼─────────────────┐
│ 4. Cliente pertence à        │ → NÃO → 403 Forbidden
│    empresa do usuário?       │
└────────────┬─────────────────┘
             │ SIM
┌────────────▼─────────────────┐
│ 5. Cliente está ativo?       │ → NÃO → 400 Bad Request
└────────────┬─────────────────┘
             │ SIM
┌────────────▼─────────────────┐
│ 6. Datas válidas?            │ → NÃO → 400 Bad Request
└────────────┬─────────────────┘
             │ SIM
┌────────────▼─────────────────┐
│ 7. Criar venda com          │
│    fk_id_company correto     │
└──────────────────────────────┘
```

**Implementação:**

```java
@Service
public class SaleService {

    public Sale store(CreateSaleDTO input) {
        // 1. Obtém company_id do usuário autenticado
        Integer companyId = authService.getAuthenticatedCompanyId();

        // 2. Valida cliente
        Client client = clientService.findById(input.getClientId());

        if (!client.getCompany().getId().equals(companyId)) {
            throw new ForbiddenException("Client does not belong to your company");
        }

        if (!client.isActive()) {
            throw new BadRequestException("Client is inactive");
        }

        // 3. Valida datas
        if (input.getDueDate().isBefore(input.getDate())) {
            throw new BadRequestException("Due date must be >= sale date");
        }

        // 4. Cria venda
        Sale sale = new Sale();
        sale.setCompany(company); // Sempre da empresa autenticada
        sale.setClient(client);
        sale.setDate(input.getDate());
        sale.setPaymentMethod(input.getPaymentMethod());
        sale.setDueDate(input.getDueDate());
        sale.setStatus(SaleStatus.PENDING);

        return repository.save(sale);
    }
}
```

### READ (saleById)

**Validações:**

```java
public Sale findById(Integer id) {
    Integer companyId = authService.getAuthenticatedCompanyId();

    return repository.findById(id)
        .filter(sale -> sale.getCompany().getId().equals(companyId))
        .orElseThrow(() -> new EntityNotFoundException("Sale not found"));
}
```

- ✅ Verifica autenticação
- ✅ Filtra por company_id
- ✅ Retorna 404 se não encontrado
- ✅ Retorna 403 se não pertence à empresa

### UPDATE (updateSale)

**Validações:**

```java
public Sale update(UpdateSaleDTO input) {
    Integer companyId = authService.getAuthenticatedCompanyId();

    Sale sale = repository.findById(input.getSaleId())
        .orElseThrow(() -> new EntityNotFoundException("Sale not found"));

    // Valida propriedade
    if (!sale.getCompany().getId().equals(companyId)) {
        throw new ForbiddenException("Sale does not belong to your company");
    }

    // Não permite atualizar venda COMPLETED
    if (sale.getStatus() == SaleStatus.COMPLETED) {
        throw new BadRequestException("Cannot update completed sale");
    }

    // Atualiza apenas campos fornecidos
    if (input.getPaymentMethod() != null) {
        sale.setPaymentMethod(input.getPaymentMethod());
    }

    if (input.getDueDate() != null) {
        sale.setDueDate(input.getDueDate());
    }

    return repository.save(sale);
}
```

- ✅ Valida propriedade
- ✅ Impede alterações em vendas finalizadas
- ✅ Apenas campos fornecidos são atualizados
- ✅ Auditoria de mudanças

### DELETE (Soft Delete)

**Validações:**

```java
public void delete(Integer saleId) {
    Integer companyId = authService.getAuthenticatedCompanyId();

    Sale sale = repository.findById(saleId)
        .orElseThrow(() -> new EntityNotFoundException("Sale not found"));

    // Valida propriedade
    if (!sale.getCompany().getId().equals(companyId)) {
        throw new ForbiddenException("Sale does not belong to your company");
    }

    // Apenas PENDING podem ser deletadas
    if (sale.getStatus() != SaleStatus.PENDING) {
        throw new BadRequestException("Only pending sales can be deleted");
    }

    // Soft delete
    sale.setDeleted(true);
    repository.save(sale);
}
```

- ✅ Soft delete (marca como deletada)
- ✅ Apenas PENDING podem ser deletadas
- ✅ Auditoria mantida

---

## 5. Tratamento Seguro de Erros

### Mensagens de Erro Seguras

**❌ ERRADO - Expõe detalhes internos:**

```json
{
  "error": "User 123 from company 456 cannot access sale 789",
  "database_error": "Column 'fk_id_company' does not match",
  "sql": "SELECT * FROM sales WHERE id = 789"
}
```

**✅ CORRETO - Genérico e seguro:**

```json
{
  "error": "Unauthorized",
  "code": "UNAUTHORIZED",
  "message": "You do not have permission to access this resource"
}
```

### Tratamento por Tipo de Erro

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Erros de autenticação
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
        UnauthorizedException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(
                "UNAUTHORIZED",
                "Invalid or expired credentials"
            ));
    }

    // Erros de autorização (acesso negado)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
        ForbiddenException ex
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(
                "FORBIDDEN",
                "You do not have permission to access this resource"
            ));
    }

    // Erros de negócio
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
        BusinessException ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                ex.getCode(),
                ex.getMessage()
            ));
    }

    // Erros genéricos do servidor (não expor detalhes)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex
    ) {
        // Log completo internamente
        logger.error("Unexpected error", ex);

        // Resposta genérica ao cliente
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred"
            ));
    }
}
```

### GraphQL Error Handling

```graphql
# Erro de autenticação
{
  "errors": [
    {
      "message": "Unauthorized",
      "extensions": {
        "code": "UNAUTHORIZED"
      }
    }
  ]
}

# Erro de validação
{
  "errors": [
    {
      "message": "Invalid discount amount",
      "extensions": {
        "code": "INVALID_DISCOUNT"
      }
    }
  ]
}
```

---

## 6. Auditoria e Logging

### Campos de Auditoria

```java
@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ... campos de negócio ...

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        createdBy = getCurrentUser();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updatedBy = getCurrentUser();
    }
}
```

### Logging de Operações

```java
@Aspect
@Component
public class SaleAuditAspect {

    @Around("@annotation(Audited)")
    public Object auditOperation(ProceedingJoinPoint joinPoint)
        throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Integer companyId = authService.getAuthenticatedCompanyId();
        String username = authService.getAuthenticatedUsername();

        try {
            Object result = joinPoint.proceed();

            logger.info(
                "AUDIT: User {} (company {}) executed {} with result {}",
                username, companyId, methodName, result
            );

            return result;
        } catch (Exception ex) {
            logger.warn(
                "AUDIT: User {} (company {}) executed {} with error: {}",
                username, companyId, methodName, ex.getMessage()
            );
            throw ex;
        }
    }
}
```

### Log Levels

- **ERROR**: Falhas críticas (ex: banco de dados)
- **WARN**: Operações rejeitadas (ex: acesso negado)
- **INFO**: Operações bem-sucedidas
- **DEBUG**: Detalhes de execução (não em produção)

---

## 7. Proteção contra Ataques Comuns

### SQL Injection

**❌ VULNERÁVEL:**

```java
String query = "SELECT * FROM sales WHERE id = " + saleId;
```

**✅ SEGURO (Using JPA):**

```java
@Query("SELECT s FROM Sale s WHERE s.id = :id AND s.company.id = :companyId")
Sale findByIdAndCompanyId(
    @Param("id") Integer id,
    @Param("companyId") Integer companyId
);
```

- ✅ Queries parametrizadas via JPA
- ✅ Sem concatenação de strings
- ✅ Validação automática de tipos

### CSRF (Cross-Site Request Forgery)

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .authorizeRequests()
            .anyRequest().authenticated();

        return http.build();
    }
}
```

- ✅ CSRF token em cookies HTTP-only
- ✅ Validação em todas as mutations
- ✅ Same-Site cookie policy

### XSS (Cross-Site Scripting)

**Validação de entrada:**

```java
@Service
public class InputValidationService {

    public String sanitizeInput(String input) {
        return input
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#x27;")
            .replaceAll("/", "&#x2F;");
    }
}
```

- ✅ Sanitização de strings
- ✅ HTML escaping
- ✅ Content-Security-Policy headers

### Broken Authorization

**Validação em toda operação:**

```java
private void validateOwnership(Sale sale) {
    Integer authCompanyId = authService.getAuthenticatedCompanyId();

    if (!sale.getCompany().getId().equals(authCompanyId)) {
        throw new ForbiddenException(
            "You do not have permission to access this sale"
        );
    }
}
```

- ✅ Validação de propriedade em TODA operação
- ✅ Sem exceções
- ✅ Erro genérico ao cliente

### Rate Limiting

```java
@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(100.0); // 100 requisições por segundo
    }
}
```

- ✅ Limita requisições por usuário
- ✅ Protege contra brute force
- ✅ Retorna 429 Too Many Requests

---

## 8. Testes de Segurança

### Teste de Isolamento Multi-Tenant

```java
@SpringBootTest
public class SaleSecurityTest {

    @Autowired
    private SaleService saleService;

    @Autowired
    private AuthenticationProvider authProvider;

    /**
     * Testa que usuário não pode acessar venda de outra empresa
     */
    @Test
    public void testCannotAccessOtherCompanySale() {
        // Setup: Cria venda na empresa A
        Sale saleCompanyA = createSaleForCompany(1);

        // Setup: Autentica como usuário da empresa B
        authenticateAsCompany(2);

        // Teste: Tenta acessar venda da empresa A
        assertThrows(
            EntityNotFoundException.class,
            () -> saleService.findById(saleCompanyA.getId())
        );
    }

    /**
     * Testa que usuário pode acessar sua própria venda
     */
    @Test
    public void testCanAccessOwnSale() {
        // Setup
        authenticateAsCompany(1);
        Sale sale = createSaleForCompany(1);

        // Teste
        Sale retrieved = saleService.findById(sale.getId());
        assertEquals(sale.getId(), retrieved.getId());
    }
}
```

### Teste de Autenticação

```java
@Test
public void testInvalidTokenReturnsUnauthorized() {
    String invalidToken = "invalid.token.here";

    graphQLClient.query(SALES_QUERY)
        .withHeader("Authorization", "Bearer " + invalidToken)
        .execute()
        .assertUnauthorized();
}

@Test
public void testExpiredTokenReturnsUnauthorized() {
    String expiredToken = generateTokenWithExpiration(LocalDateTime.now().minusHours(1));

    graphQLClient.query(SALES_QUERY)
        .withHeader("Authorization", "Bearer " + expiredToken)
        .execute()
        .assertUnauthorized();
}
```

### Teste de Validação

```java
@Test
public void testCannotCreateSaleWithInactiveClient() {
    Client inactiveClient = createInactiveClient();

    assertThrows(
        BadRequestException.class,
        () -> saleService.store(
            new CreateSaleDTO(
                inactiveClient.getId(),
                LocalDate.now(),
                PaymentMethod.CREDIT_CARD,
                LocalDate.now().plusDays(30)
            )
        )
    );
}

@Test
public void testCannotApplyNegativeDiscount() {
    Sale sale = createSale();

    assertThrows(
        BadRequestException.class,
        () -> saleService.applyDiscount(
            sale.getId(),
            BigDecimal.valueOf(-50)
        )
    );
}
```

### Teste de Proteção contra Ataques

```java
@Test
public void testSqlInjectionAttemptFails() {
    String injectedId = "1; DROP TABLE sales;--";

    assertThrows(
        NumberFormatException.class,
        () -> saleService.findById(Integer.parseInt(injectedId))
    );
}

@Test
public void testXssAttemptSanitized() {
    String xssInput = "<script>alert('xss')</script>";
    Sale sale = createSaleWithNotes(xssInput);

    String sanitized = sale.getNotes();
    assertFalse(sanitized.contains("<script>"));
}
```

---

## 9. Checklist de Deploy

Antes de fazer deploy da aplicação, siga este checklist de segurança:

### Variáveis de Ambiente

- [ ] JWT_SECRET está definido e é forte (mínimo 32 caracteres)
- [ ] DATABASE_PASSWORD está definido e é forte
- [ ] APP_ENV = production
- [ ] DEBUG_MODE = false
- [ ] LOG_LEVEL = info (não debug)

### Configuração de Banco de Dados

- [ ] SSL/TLS habilitado para conexões
- [ ] Credenciais de banco separadas por ambiente
- [ ] Backup automático configurado
- [ ] Replicação configurada para alta disponibilidade

### Configuração da Aplicação

- [ ] CORS configurado apenas para domínios autorizados
- [ ] HTTPS obrigatório (SSL/TLS)
- [ ] HTTP Strict-Transport-Security habilitado
- [ ] X-Content-Type-Options = nosniff
- [ ] X-Frame-Options = DENY
- [ ] Content-Security-Policy configurada

### Segurança de Rede

- [ ] WAF (Web Application Firewall) ativo
- [ ] Rate limiting configurado
- [ ] DDoS protection ativo
- [ ] Firewall restringindo IPs necessários

### Auditoria e Logging

- [ ] Logs centralizados (ELK, Splunk, etc.)
- [ ] Alertas configurados para anomalias
- [ ] Retenção de logs por 90 dias mínimo
- [ ] Acesso a logs restrito

### Testes

- [ ] Testes unitários passando (100% de coverage nas operações críticas)
- [ ] Testes de integração passando
- [ ] Testes de segurança executados
- [ ] Teste de carga realizado

### Monitoramento

- [ ] APM (Application Performance Monitoring) configurado
- [ ] Health checks do servidor funcionando
- [ ] Alertas para taxa de erro > 1%
- [ ] Alertas para latência > 500ms

### Documentação

- [ ] README atualizado
- [ ] Documentação de segurança revisada
- [ ] Plano de resposta a incidentes documentado
- [ ] Procedimentos de rollback documentados

### Backup e Disaster Recovery

- [ ] Backup automático ativo
- [ ] Teste de restore realizado
- [ ] RPO (Recovery Point Objective) documentado
- [ ] RTO (Recovery Time Objective) documentado

### Compliance

- [ ] LGPD compliance verificado
- [ ] Política de privacidade atualizada
- [ ] Termos de serviço atualizados
- [ ] Consentimento de dados coletado

### Segurança da Aplicação

- [ ] Todas as dependências atualizadas
- [ ] Vulnerabilidades conhecidas corrigidas
- [ ] SAST (Static Application Security Testing) executado
- [ ] DAST (Dynamic Application Security Testing) executado

---

## Referências de Segurança

### Padrões e Melhores Práticas

- [OWASP Top 10 2023](https://owasp.org/Top10/)
- [OWASP GraphQL Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/GraphQL_Cheat_Sheet.html)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

### Conformidade

- [LGPD (Lei Geral de Proteção de Dados)](https://www.gov.br/cidadania/pt-br/acesso-a-informacao/lgpd)
- [GDPR (General Data Protection Regulation)](https://gdpr-info.eu/)
- [ISO 27001 (Information Security Management)](https://www.iso.org/isoiec-27001-information-security-management.html)

### Ferramentas

- [OWASP ZAP (Penetration Testing)](https://www.zaproxy.org/)
- [Snyk (Vulnerability Scanning)](https://snyk.io/)
- [Sonarqube (Code Quality)](https://www.sonarqube.org/)

---

## Contato para Segurança

Vulnerabilidade encontrada? Entre em contato com:

**Email**: security@igniscore.com  
**Telefone**: +55 (11) XXXX-XXXX  
**Policy**: [Responsible Disclosure Policy](./SECURITY_POLICY.md)

---

**Documentação Versão**: 1.0  
**Data**: 1º de junho de 2026  
**Status**: Produção

Para dúvidas sobre segurança, consulte o [README técnico](SALES_README.md).
