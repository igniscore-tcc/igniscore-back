# README Técnico – Módulo Sales (API GraphQL / Spring Boot)

## Visão Geral

O módulo **sales** é responsável pelo gerenciamento de vendas realizadas pelos clientes das empresas (tenants) usuárias da plataforma Igniscore. Cada venda está estritamente vinculada a uma única empresa e a um cliente específico, garantindo isolamento completo dos dados em ambiente multi-tenant.

O sistema permite:

- Criação e gestão de vendas completas
- Adição de múltiplos itens por venda
- Controle de pagamentos e descontos
- Rastreamento de status das vendas
- Integração com clientes e produtos

### Objetivos Principais

- Centralizar o gerenciamento de vendas por empresa (tenant)
- Garantir isolamento de dados entre empresas
- Fornecer operações CRUD via GraphQL
- Manter histórico e rastreabilidade de transações
- Integrar-se com módulos de clientes, produtos e pagamentos

---

## Modelo de Dados

### Tabela: `sales`

| Campo                 | Tipo          | Constraint         | Descrição                           |
| --------------------- | ------------- | ------------------ | ----------------------------------- |
| `pk_id_sale`          | INT           | PK, Auto-increment | Identificador único da venda        |
| `quantity_items_sale` | INT           | NOT NULL           | Quantidade total de itens na venda  |
| `discount_sale`       | DECIMAL(5,2)  | NOT NULL           | Desconto aplicado à venda           |
| `total_sale`          | DECIMAL(10,2) | NOT NULL           | Valor total da venda (com desconto) |
| `date_sale`           | DATE          | NOT NULL           | Data de realização da venda         |
| `type_sale`           | VARCHAR(30)   | NOT NULL           | Método de pagamento (enum)          |
| `status_sale`         | VARCHAR(20)   | NOT NULL           | Status da venda (enum)              |
| `due_date`            | DATE          | NOT NULL           | Data de vencimento do pagamento     |
| `fk_id_company`       | INT           | FK, NOT NULL       | Empresa proprietária da venda       |
| `fk_id_client`        | INT           | FK, NOT NULL       | Cliente associado à venda           |

### Tabela: `sale_items`

| Campo                  | Tipo          | Constraint         | Descrição                            |
| ---------------------- | ------------- | ------------------ | ------------------------------------ |
| `pk_id_sale_item`      | INT           | PK, Auto-increment | Identificador único do item de venda |
| `quantity_sale_item`   | INT           | NOT NULL           | Quantidade do produto no item        |
| `unit_price_sale_item` | DECIMAL(10,2) | NOT NULL           | Preço unitário do produto            |
| `total_sale_item`      | DECIMAL(10,2) | NOT NULL           | Total do item (quantidade × preço)   |
| `fk_id_prod`           | INT           | FK, NOT NULL       | Produto incluído no item             |
| `fk_id_sale`           | INT           | FK, NOT NULL       | Venda associada ao item              |

### Constraints e Relacionamentos

```sql
ALTER TABLE sales ADD CONSTRAINT fk_sales_company
    FOREIGN KEY (fk_id_company) REFERENCES companies(pk_id_company)
    ON DELETE CASCADE;

ALTER TABLE sales ADD CONSTRAINT fk_sales_client
    FOREIGN KEY (fk_id_client) REFERENCES clients(pk_id_client)
    ON DELETE RESTRICT;

ALTER TABLE sale_items ADD CONSTRAINT fk_sale_items_sale
    FOREIGN KEY (fk_id_sale) REFERENCES sales(pk_id_sale)
    ON DELETE CASCADE;

ALTER TABLE sale_items ADD CONSTRAINT fk_sale_items_product
    FOREIGN KEY (fk_id_prod) REFERENCES products(pk_id_prod)
    ON DELETE RESTRICT;
```

---

## Enums

### PaymentMethod (type_sale)

Métodos de pagamento disponíveis:

| Tipo               | Descrição                     |
| ------------------ | ----------------------------- |
| `CASH`             | Pagamento à vista em dinheiro |
| `CREDIT_CARD`      | Cartão de crédito             |
| `DEBIT_CARD`       | Cartão de débito              |
| `BANK_TRANSFER`    | Transferência bancária        |
| `CHECK`            | Cheque                        |
| `INSTALLMENT`      | Parcelado/Crediário           |
| `PPayment_PENDING` | Pendente de pagamento         |

### SaleStatus (status_sale)

Estados possíveis de uma venda:

| Status      | Descrição                             |
| ----------- | ------------------------------------- |
| `PENDING`   | Venda criada mas ainda não finalizada |
| `COMPLETED` | Venda finalizada e paga               |
| `CANCELED`  | Venda cancelada                       |

---

## Regras de Negócio

### Isolamento Multi-Tenant

- Cada venda pertence exclusivamente a uma empresa
- Cada venda está associada a um cliente da empresa
- Vendas de diferentes empresas não podem ser acessadas
- Ao deletar uma empresa, todas suas vendas são deletadas em cascata
- Ao deletar um cliente, as vendas associadas não são automaticamente deletadas (RESTRICT)

### Ciclo de Vida da Venda

- **Criação**: Requer cliente válido, empresa válida, data, método de pagamento e data de vencimento
- **Adição de Itens**: Produtos devem estar ativos e pertencer à mesma empresa
- **Cálculo de Totais**: Recalculado automaticamente ao adicionar/remover itens
- **Desconto**: Pode ser aplicado a qualquer momento (validação: não negativo, não pode exceder total)
- **Finalização**: Status alterado para COMPLETED
- **Cancelamento**: Status alterado para CANCELED (sem exclusão física)
- **Deleção**: Vendas podem ser deletadas apenas se em status PENDING

### Acesso e Autorização

- Apenas usuários autenticados da empresa podem acessar suas vendas
- Nenhum usuário pode acessar vendas de outra empresa
- Controle de acesso baseado em contexto de empresa (tenant ID)
- Operações de escrita requerem permissão de gerenciador de vendas

### Validações

- Cliente deve existir e estar ativo
- Cliente deve pertencer à empresa da venda
- Produtos devem estar ativos
- Quantidade de itens deve ser maior que zero
- Preço unitário deve ser maior que zero
- Desconto não pode ser negativo
- Desconto não pode exceder o total
- Data de venda deve ser válida
- Data de vencimento deve ser igual ou posterior à data de venda

---

## Arquitetura da Camada de Sales

```
┌─────────────────────────────────────┐
│      GraphQL Schema (schema.graphqls)│
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  SaleController (Spring GraphQL)    │
│  - storeSale (Mutation)             │
│  - updateSale (Mutation)            │
│  - sales (Query - paginado)         │
│  - saleById (Query)                 │
│  - addSaleItem (Mutation)           │
│  - removeSaleItem (Mutation)        │
│  - applySaleDiscount (Mutation)     │
│  - completeSale (Mutation)          │
│  - cancelSale (Mutation)            │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│    SaleService (Business Layer)     │
│  - store(CreateSaleDTO)             │
│  - update(UpdateSaleDTO)            │
│  - findById(Integer)                │
│  - findByCompany(Integer, Pageable) │
│  - findByClient(Integer, Pageable)  │
│  - addItem(SaleID, SaleItemDTO)     │
│  - removeItem(SaleItemID)           │
│  - applyDiscount(SaleID, amount)    │
│  - complete(SaleID)                 │
│  - cancel(SaleID)                   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  SaleRepository (Spring Data JPA)   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Database (sales tables)      │
└─────────────────────────────────────┘
```

---

## Operações Disponíveis (GraphQL)

### Queries

#### 1. Listar Vendas (Paginado)

```graphql
query {
  sales(page: 0, size: 10) {
    id
    quantityItems
    discount
    total
    date
    paymentMethod
    status
    dueDate
    client {
      id
      name
      cnpj
    }
    company {
      id
      name
    }
  }
}
```

**Retorno**: Lista paginada de vendas da empresa autenticada

**Parâmetros**:

- `page` (Int, opcional): Número da página (default: 0)
- `size` (Int, opcional): Quantidade de registros por página (default: 20)

---

#### 2. Buscar Venda por ID

```graphql
query {
  saleById(id: 1) {
    id
    quantityItems
    discount
    total
    date
    paymentMethod
    status
    dueDate
    client {
      id
      name
    }
    items {
      id
      quantity
      unitPrice
      total
      product {
        id
        name
        type
      }
    }
  }
}
```

**Retorno**: Dados completos da venda incluindo todos os itens

**Parâmetros**:

- `id` (Int, obrigatório): Identificador único da venda

---

#### 3. Listar Vendas por Cliente

```graphql
query {
  salesByClient(clientId: 5, page: 0, size: 10) {
    id
    total
    date
    status
    paymentMethod
  }
}
```

**Retorno**: Vendas associadas a um cliente específico

**Parâmetros**:

- `clientId` (Int, obrigatório): ID do cliente
- `page` (Int, opcional): Número da página
- `size` (Int, opcional): Registros por página

---

### Mutations

#### 1. Criar Venda

```graphql
mutation {
  storeSale(
    input: {
      clientId: 5
      date: "2026-06-01"
      paymentMethod: "CREDIT_CARD"
      dueDate: "2026-07-01"
    }
  ) {
    id
    quantityItems
    total
    date
    status
  }
}
```

**DTO**: `CreateSaleDTO`

```java
{
  clientId: Integer (required)
  date: LocalDate (required)
  paymentMethod: PaymentMethod (required)
  dueDate: LocalDate (required)
}
```

**Validações**:

- Cliente deve existir e estar ativo
- Cliente deve pertencer à empresa autenticada
- Data de venda deve ser válida
- Data de vencimento deve ser >= data de venda
- Método de pagamento deve ser válido

**Retorno**: Objeto `Sale` criado com status PENDING

---

#### 2. Adicionar Item à Venda

```graphql
mutation {
  addSaleItem(
    input: { saleId: 1, productId: 10, quantity: 5, unitPrice: 85.50 }
  ) {
    id
    quantityItems
    total
    items {
      id
      quantity
      unitPrice
      total
    }
  }
}
```

**DTO**: `CreateSaleItemDTO`

```java
{
  saleId: Integer (required)
  productId: Integer (required)
  quantity: Integer (required, > 0)
  unitPrice: BigDecimal (required, > 0)
}
```

**Validações**:

- Venda deve existir
- Venda não pode estar em status CANCELED
- Produto deve existir e estar ativo
- Quantidade deve ser maior que zero
- Preço unitário deve ser maior que zero
- Produto deve pertencer à mesma empresa

**Retorno**: Venda atualizada com novo item

---

#### 3. Remover Item da Venda

```graphql
mutation {
  removeSaleItem(saleItemId: 42) {
    id
    quantityItems
    total
    items {
      id
    }
  }
}
```

**Comportamento**:

- Remove o item especificado
- Recalcula quantidade e total da venda
- Valida que venda não está COMPLETED

**Validações**:

- Item deve existir
- Venda associada não pode estar COMPLETED
- Venda não pode estar CANCELED

**Retorno**: Venda atualizada

---

#### 4. Aplicar Desconto

```graphql
mutation {
  applySaleDiscount(input: { saleId: 1, discountAmount: 50.00 }) {
    id
    total
    discount
  }
}
```

**DTO**: `ApplySaleDiscountDTO`

```java
{
  saleId: Integer (required)
  discountAmount: BigDecimal (required, >= 0)
}
```

**Validações**:

- Venda deve existir
- Desconto não pode ser negativo
- Desconto não pode exceder total (sem desconto anterior)
- Venda não pode estar CANCELED

**Retorno**: Venda com desconto aplicado

---

#### 5. Completar Venda

```graphql
mutation {
  completeSale(saleId: 1) {
    id
    status
    total
    date
  }
}
```

**Comportamento**:

- Altera status para COMPLETED
- Valida que venda tem pelo menos um item
- Não permite completar venda sem itens

**Validações**:

- Venda deve existir
- Venda não pode estar CANCELED
- Venda deve ter pelo menos um item

**Retorno**: Venda com status COMPLETED

---

#### 6. Cancelar Venda

```graphql
mutation {
  cancelSale(saleId: 1) {
    id
    status
  }
}
```

**Comportamento**:

- Altera status para CANCELED
- Não deleta a venda (soft cancel)
- Impede novas operações na venda

**Validações**:

- Venda deve existir
- Venda não pode estar COMPLETED

**Retorno**: Venda com status CANCELED

---

#### 7. Atualizar Venda

```graphql
mutation {
  updateSale(
    input: { saleId: 1, paymentMethod: "BANK_TRANSFER", dueDate: "2026-08-01" }
  ) {
    id
    paymentMethod
    dueDate
    updatedAt
  }
}
```

**DTO**: `UpdateSaleDTO`

```java
{
  saleId: Integer (required)
  paymentMethod: PaymentMethod (optional)
  dueDate: LocalDate (optional)
}
```

**Comportamento**: Apenas campos fornecidos são atualizados (PATCH semântico)

**Validações**:

- Venda deve existir
- Venda não pode estar COMPLETED
- Duedate deve ser >= data de venda

**Retorno**: Venda atualizada

---

## Estrutura de Código

### Localizações

```
src/main/java/com/igniscore/api/
├── model/
│   ├── Sale.java                    # Entidade JPA
│   ├── SaleItem.java                # Item de venda
│   ├── SaleStatus.java              # Enum de status
│   └── PaymentMethod.java           # Enum de métodos de pagamento
├── dto/
│   ├── CreateSaleDTO.java           # DTO para criação
│   ├── UpdateSaleDTO.java           # DTO para atualização
│   ├── CreateSaleItemDTO.java       # DTO para adicionar item
│   └── ApplySaleDiscountDTO.java    # DTO para aplicar desconto
├── repository/
│   ├── SaleRepository.java          # Acesso a dados
│   └── SaleItemRepository.java      # Acesso a itens
├── service/
│   └── SaleService.java             # Lógica de negócio
├── controller/
│   └── SaleController.java          # Camada GraphQL
└── utils/
    └── SaleValidator.java           # Validações específicas

src/main/resources/
└── graphql/
    └── schema.graphqls              # Definições GraphQL
```

### Classes Principais

#### `Sale.java`

Entity JPA que representa uma venda completa com suporte a itens em cascata.

```java
@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quantityItems;
    private BigDecimal discount;
    private BigDecimal total;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    private LocalDate dueDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_id_company")
    private Company company;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_id_client")
    private Client client;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleItem> items;

    // Métodos de domínio
    public void addItem(SaleItem item) { ... }
    public void removeItem(SaleItem item) { ... }
    public void applyDiscount(BigDecimal discount) { ... }
}
```

#### `SaleItem.java`

Entity JPA que representa um produto dentro de uma venda.

```java
@Entity
@Table(name = "sale_items")
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_id_prod")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_id_sale")
    private Sale sale;

    // Validação e cálculo de totais
    public void changeQuantity(Integer quantity) { ... }
    public void changeUnitPrice(BigDecimal unitPrice) { ... }
}
```

#### `SaleService.java`

Encapsula toda lógica de negócio relacionada a vendas.

```java
@Service
public class SaleService {

    private final SaleRepository repository;
    private final SaleItemRepository itemRepository;
    private final AuthenticatedUserService authService;
    private final ClientService clientService;
    private final ProductService productService;

    // Criar venda
    public Sale store(CreateSaleDTO input) { ... }

    // Adicionar item
    public Sale addSaleItem(CreateSaleItemDTO input) { ... }

    // Remover item
    public Sale removeSaleItem(Integer itemId) { ... }

    // Aplicar desconto
    public Sale applyDiscount(Integer saleId, BigDecimal amount) { ... }

    // Completar venda
    public Sale completeSale(Integer saleId) { ... }

    // Cancelar venda
    public Sale cancelSale(Integer saleId) { ... }

    // Consultas
    public Sale findById(Integer id) { ... }
    public Page<Sale> findByCompany(Integer companyId, Pageable pageable) { ... }
    public Page<Sale> findByClient(Integer clientId, Pageable pageable) { ... }
}
```

#### `SaleController.java`

Controller GraphQL que expõe operações para vendas.

```java
@Controller
public class SaleController {

    private final SaleService service;

    @MutationMapping
    public Sale storeSale(@Argument CreateSaleDTO input) { ... }

    @MutationMapping
    public Sale addSaleItem(@Argument CreateSaleItemDTO input) { ... }

    @MutationMapping
    public Sale removeSaleItem(@Argument Integer saleItemId) { ... }

    @QueryMapping
    public List<Sale> sales(@Argument Integer page, @Argument Integer size) { ... }

    @QueryMapping
    public Sale saleById(@Argument Integer id) { ... }
}
```

---

## Integrações

### Módulo Clients (Clientes)

- **Relação**: Uma venda é sempre associada a um cliente
- **Operação**: Buscar clientes da empresa para criar vendas
- **Endpoint**: Via query `clients` do módulo Clients
- **Validação**: Cliente deve estar ativo

### Módulo Products (Produtos)

- **Relação**: Itens de venda contêm produtos
- **Operação**: Adicionar produtos às vendas
- **Endpoint**: Via query `activeProducts` do módulo Products
- **Validação**: Produto deve estar ativo

### Módulo Companies (Empresas)

- **Relação**: Cada venda pertence a uma empresa
- **Operação**: Filtrar vendas por empresa do usuário
- **Comportamento**: Deleção em cascata quando empresa é deletada

### Módulo Auth (Autenticação)

- **Relação**: Validação de permissão do usuário para acessar venda
- **Operação**: Verificar se usuário pertence à empresa da venda
- **Filtro**: Todas as queries aplicam filtro automático de `company_id`

---

## Segurança e Multi-tenancy

### Isolamento de Dados

```
FILTERING RULE:
WHERE sales.fk_id_company = :authenticatedUserCompanyId
```

Toda query ou mutation aplicará automaticamente este filtro baseado no contexto de autenticação.

### Validações de Segurança

| Operação   | Validação                                             | Erro             |
| ---------- | ----------------------------------------------------- | ---------------- |
| **CREATE** | Cliente deve existir e pertencer à empresa do usuário | `INVALID_CLIENT` |
| **READ**   | Venda deve pertencer à empresa do usuário             | `UNAUTHORIZED`   |
| **UPDATE** | Usuário deve ser da mesma empresa da venda            | `UNAUTHORIZED`   |
| **DELETE** | Apenas vendas em status PENDING podem ser deletadas   | `FORBIDDEN`      |

### Implementação da Segurança

```java
@Service
public class SaleService {

    private final AuthenticatedUserService authService;

    private Integer getAuthenticatedCompanyId() {
        return authService.getAuthenticatedCompany().getId();
    }

    private void validateOwnership(Sale sale) {
        Integer authCompanyId = getAuthenticatedCompanyId();
        if (!sale.getCompany().getId().equals(authCompanyId)) {
            throw new UnauthorizedException("Sale does not belong to your company");
        }
    }

    public Sale findById(Integer id) {
        return repository.findById(id)
            .filter(s -> s.getCompany().getId().equals(getAuthenticatedCompanyId()))
            .orElseThrow(() -> new EntityNotFoundException("Sale not found"));
    }
}
```

### Auditoria

- Registrar quem criou a venda e quando
- Registrar modificações com timestamp
- Manter histórico de status
- Não revelar informações de vendas de outras empresas em erros

---

## Tratamento de Erros

| Código de Erro              | HTTP Status | Descrição                                    |
| --------------------------- | ----------- | -------------------------------------------- |
| `SALE_NOT_FOUND`            | 404         | Venda com ID especificado não existe         |
| `INVALID_CLIENT`            | 400         | Cliente não existe ou não pertence à empresa |
| `INVALID_PRODUCT`           | 400         | Produto não existe ou está inativo           |
| `INVALID_QUANTITY`          | 400         | Quantidade inválida (deve ser > 0)           |
| `INVALID_PRICE`             | 400         | Preço inválido (deve ser > 0)                |
| `INVALID_DISCOUNT`          | 400         | Desconto negativo ou excede total            |
| `INVALID_STATUS_TRANSITION` | 400         | Transição de status não permitida            |
| `UNAUTHORIZED`              | 403         | Usuário não autorizado a acessar esta venda  |
| `EMPTY_SALE`                | 400         | Venda não pode estar vazia (0 itens)         |
| `SALE_ALREADY_COMPLETED`    | 409         | Venda já foi finalizada                      |

**Exemplo de resposta de erro GraphQL:**

```json
{
  "errors": [
    {
      "message": "Client is inactive",
      "extensions": {
        "code": "INVALID_CLIENT",
        "timestamp": "2026-06-01T15:30:00Z"
      }
    }
  ]
}
```

---

## Fluxo de Exemplo: Criar e Finalizar Venda

### 1. Criar Venda

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "query": "mutation { storeSale(input: { clientId: 5, date: \"2026-06-01\", paymentMethod: \"CREDIT_CARD\", dueDate: \"2026-07-01\" }) { id status total } }"
  }'
```

**Resposta:**

```json
{
  "data": {
    "storeSale": {
      "id": 1,
      "status": "PENDING",
      "total": 0
    }
  }
}
```

### 2. Adicionar Itens

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "query": "mutation { addSaleItem(input: { saleId: 1, productId: 10, quantity: 5, unitPrice: 85.50 }) { id quantityItems total } }"
  }'
```

### 3. Aplicar Desconto (opcional)

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "query": "mutation { applySaleDiscount(input: { saleId: 1, discountAmount: 50.00 }) { id total discount } }"
  }'
```

### 4. Finalizar Venda

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "query": "mutation { completeSale(saleId: 1) { id status } }"
  }'
```

---

## Performance e Otimizações

### Índices Recomendados

```sql
-- Índice na foreign key (usado em filtros tenant)
CREATE INDEX idx_sale_company ON sales(fk_id_company);

-- Índice em cliente (para buscas de vendas por cliente)
CREATE INDEX idx_sale_client ON sales(fk_id_client);

-- Índice em status (para filtros de status)
CREATE INDEX idx_sale_status ON sales(status_sale);

-- Índice em data (para buscas temporais)
CREATE INDEX idx_sale_date ON sales(date_sale);

-- Índice composto (company + status)
CREATE INDEX idx_sale_company_status ON sales(fk_id_company, status_sale);

-- Índice em sale_items para relação com sale
CREATE INDEX idx_sale_item_sale ON sale_items(fk_id_sale);

-- Índice em sale_items para relação com produto
CREATE INDEX idx_sale_item_product ON sale_items(fk_id_prod);
```

### Paginação

- Sempre use paginação ao listar vendas (máximo 100 registros por página)
- Implemente cursor-based pagination para grandes datasets
- Use índices compostos para melhorar performance

### Cache

- Cache de venda ativa por 5 minutos (invalidação na atualização)
- Cache de lista paginada por 2 minutos
- Invalidar cache ao adicionar/remover itens

### N+1 Query Prevention

- Usar `FetchType.LAZY` em relacionamentos
- GraphQL retorna apenas campos solicitados (lazy loading)
- Eager loading apenas quando necessário

---

## Configuração e Deployment

### Variáveis de Ambiente

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/igniscore
SPRING_DATASOURCE_USERNAME=igniscore_user
SPRING_DATASOURCE_PASSWORD=secure_password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# GraphQL
GRAPHQL_SERVLET_ENABLED=true
GRAPHQL_SERVLET_PATH=/graphql
```

### Dependências Maven

```xml
<!-- JPA/Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- GraphQL -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>

<!-- MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

---

## Testes

### Testes Unitários (SaleService)

- Teste de criação de venda
- Teste de adição de itens
- Teste de remoção de itens
- Teste de aplicação de desconto
- Teste de transições de status
- Teste de isolamento de tenant
- Teste de edge cases (venda vazia, desconto maior que total)

### Testes de Integração (SaleController)

- Teste de endpoint GraphQL completo
- Teste de autenticação e autorização
- Teste de paginação
- Teste de fluxo completo (criar → adicionar itens → completar)

**Executar testes:**

```bash
mvn test -Dtest=SaleServiceTest
mvn test -Dtest=SaleControllerTest
```

---

## Roadmap Futuro

- [ ] Soft delete com temporal queries
- [ ] Histórico de alterações (audit log completo)
- [ ] Relatórios de vendas por período
- [ ] API REST adicional (além de GraphQL)
- [ ] Integração com webhooks (notificar quando venda é criada/completada)
- [ ] Busca full-text em vendas
- [ ] Exportação em CSV/PDF
- [ ] Bulk operations (criar múltiplas vendas)
- [ ] Integração com sistema de pagamento
- [ ] Nota fiscal eletrônica (NF-e)

---

## Contribuindo

1. Siga as convenções de código do projeto
2. Faça testes para novas funcionalidades
3. Atualize este README se adicionar operações
4. Valide ISO 8601 para datas e timezones UTC

---

## Referências

- **Especificação GraphQL**: [graphql.org](https://graphql.org)
- **Spring GraphQL**: [spring.io/projects/spring-graphql](https://spring.io/projects/spring-graphql)
- **JPA/Hibernate**: [hibernate.org](https://hibernate.org)
- **Validação no Spring**: [spring.io/guides/gs/validating-form-input/](https://spring.io/guides/gs/validating-form-input/)

---

## Suporte

Dúvidas ou problemas? Entre em contato com:

- **Desenvolvedor**: [Seu Nome]
- **Email**: dev@igniscore.com
- **Issue Tracker**: GitHub Issues do projeto

---

**Última Atualização**: 1º de junho de 2026  
**Versão do Documento**: 1.0  
**Status**: Produção
