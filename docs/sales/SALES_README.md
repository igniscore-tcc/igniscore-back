# README Técnico – Módulo Sales (API GraphQL / Spring Boot)

## Visão Geral

O módulo **Sales** é responsável pela gestão de vendas vinculadas às empresas que utilizam a plataforma Igniscore.

Cada venda pertence obrigatoriamente a uma empresa e todas as operações respeitam o contexto multi-tenant da aplicação, garantindo que utilizadores acessem apenas os dados da empresa à qual estão vinculados.

O módulo fornece operações de cadastro e consulta de vendas através da API GraphQL.

### Objetivos

* Centralizar o registro de vendas por empresa
* Garantir isolamento de dados entre tenants
* Disponibilizar operações de consulta via GraphQL
* Manter integridade dos dados comerciais
* Servir como base para módulos financeiros e de faturamento

---

# Modelo de Dados

## Tabela `sales`

| Campo            | Tipo           | Descrição                          |
|------------------|----------------|------------------------------------|
| pk_id_sale       | INT            | Identificador único da venda       |
| number_sale      | INT            | Número sequencial da venda         |
| date_sale        | DATETIME       | Data da venda                      |
| total_sale       | DECIMAL(15,2)  | Valor total da venda               |
| status_sale      | VARCHAR(30)    | Status da venda                    |
| obs_sale         | TEXT           | Observações da venda               |
| fk_id_client     | INT            | Cliente vinculado                  |
| fk_id_company    | INT            | Empresa proprietária               |
| created_at       | DATETIME       | Data de criação                    |
| updated_at       | DATETIME       | Data da última atualização         |

### Constraints

```sql
ALTER TABLE sales
ADD CONSTRAINT fk_sales_company
FOREIGN KEY (fk_id_company)
REFERENCES companies(pk_id_company)
ON DELETE CASCADE;
```

```sql
ALTER TABLE sales
ADD CONSTRAINT fk_sales_client
FOREIGN KEY (fk_id_client)
REFERENCES clients(pk_id_client);
```

```sql
ALTER TABLE sales
ADD CONSTRAINT uq_sale_per_company
UNIQUE (fk_id_company, number_sale);
```

---

# Regras de Negócio

## Multi-Tenant

* Cada venda pertence a apenas uma empresa
* Utilizadores acessam apenas vendas da própria empresa
* Empresas diferentes podem possuir vendas com a mesma numeração
* O número da venda deve ser único dentro da empresa

## Cadastro

Para criação de uma venda são obrigatórios:

* Cliente
* Pelo menos um ‘item’
* Data da venda

Campos opcionais:

* Observações
* Status
* Informações complementares

## Consulta

As consultas retornam apenas vendas pertencentes à empresa autenticada.

## Validações

* Cliente deve existir
* Cliente deve pertencer à empresa autenticada
* Venda deve possuir ao menos um ‘item’
* Valor total deve ser maior que zero
* Produtos devem existir e pertencer à empresa autenticada

---

# Arquitetura

```text
GraphQL
   │
   ▼
SaleController
   │
   ▼
SaleService
   │
   ▼
SaleRepository
   │
   ▼
MySQL
```

---

# Estrutura do Projeto

```text
src/main/java/com/igniscore/api

├── controller
│   └── SaleController.java

├── service
│   └── SaleService.java

├── repository
│   └── SaleRepository.java

├── dto
│   ├── CreateSaleDTO.java
│   ├── CreateSaleItemDTO.java
│   └── SaleQueryDTO.java

├── model
│   ├── Sale.java
│   └── SaleItem.java
```

---

# Operações Disponíveis

## Query – Listar Vendas

```graphql
query {
  sales(page: 0, size: 10) {
    sales {
      id
      number
      total
      date
      status
    }

    totalPages
    totalSales
  }
}
```

### Parâmetros

| Campo | Tipo    |
|-------|---------|
| page  | Integer |
| size  | Integer |

### Retorno

Lista paginada de vendas da empresa autenticada.

---

## Query – Buscar Venda por ID

```graphql
query {
  sale(id: 1) {
    id
    number
    total
    date

    client {
      id
      name
    }

    items {
      productName
      quantity
      unitPrice
      totalPrice
    }
  }
}
```

### Parâmetros

| Campo | Tipo    |
|-------|---------|
| id    | Integer |

### Retorno

Venda pertencente à empresa autenticada.

---

## Mutation – Criar Venda

```graphql
mutation {
  createSale(
    input: {
      clientId: 1
      items: [
        {
          productId: 1
          quantity: 2
          unitPrice: 150.00
        }
      ]
    }
  ) {
    id
    number
    total
  }
}
```

### DTO

```java
public class CreateSaleDTO {

    private Integer clientId;

    private List<CreateSaleItemDTO> items;

}
```

### DTO ‘Item’

```java
public class CreateSaleItemDTO {

    private Integer productId;

    private Integer quantity;

    private BigDecimal unitPrice;

}
```

---

# Segurança

Todas as operações devem respeitar o contexto da empresa autenticada.

Regra aplicada em todas as consultas:

```sql
WHERE sales.fk_id_company = :authenticatedCompanyId
```

## Exemplo

```java
public Sale findById(Integer id) {

    Integer companyId =
        authService.getAuthenticatedCompany().getId();

    return repository.findById(id)
        .filter(sale ->
            sale.getCompany().getId().equals(companyId))
        .orElseThrow(() ->
            new EntityNotFoundException("Sale not found"));
}
```

---

# Tratamento de Erros

| Código            | Descrição                    |
|-------------------|------------------------------|
| SALE_NOT_FOUND    | Venda não encontrada         |
| CLIENT_NOT_FOUND  | Cliente não encontrado       |
| PRODUCT_NOT_FOUND | Produto não encontrado       |
| INVALID_TOTAL     | Valor total inválido         |
| INVALID_COMPANY   | Empresa inválida             |
| UNAUTHORIZED      | Venda não pertence à empresa |

### Exemplo

```json
{
  "errors": [
    {
      "message": "Sale not found",
      "extensions": {
        "code": "SALE_NOT_FOUND"
      }
    }
  ]
}
```

---

# Índices Recomendados

```sql
CREATE INDEX idx_sale_company
ON sales(fk_id_company);
```

```sql
CREATE INDEX idx_sale_client
ON sales(fk_id_client);
```

```sql
CREATE INDEX idx_sale_date
ON sales(date_sale);
```

---

# Testes

## Testes Unitários

### SaleService

* Criação de venda
* Busca por ID
* Cálculo de valor total
* Isolamento multi-tenant
* Validação de cliente
* Validação de produtos

## Testes de Integração

### SaleController

* Queries GraphQL
* Mutations GraphQL
* Autenticação
* Autorização
* Paginação

### Execução

```bash
mvn test
```

---

# Dependências

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

---

# Status

| Item                    | Status |
|-------------------------|--------|
| Cadastro de vendas      | ✅      |
| Consulta por ID         | ✅      |
| Consulta paginada       | ✅      |
| Multi-tenancy           | ✅      |
| Validações              | ✅      |
| Soft Delete             | ❌      |
| Auditoria               | ✅      |
| Histórico de alterações | ❌      |
| Cancelamento de venda   | ❌      |
| Bulk Operations         | ❌      |

---

**Projeto:** Igniscore API  
**Módulo:** Sales  
**Tecnologias:** Spring Boot, GraphQL, JPA/Hibernate, MySQL  
**Status do Documento:** Atualizado conforme implementação atual.