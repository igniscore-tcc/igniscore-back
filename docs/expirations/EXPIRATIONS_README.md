# README Técnico – Módulo Expirations (API GraphQL / Spring Boot)

## Visão Geral

O módulo **Expirations** é responsável pela gestão de vencimento vinculados às empresas que utilizam a plataforma Igniscore.

Cada vencimento pertence obrigatoriamente a uma empresa, e todas as operações respeitam o contexto multi-tenant da aplicação, garantindo isolamento completo de dados entre organizações.

O módulo fornece operações de consulta de vencimentos por API GraphQL.

---

## Objetivos

* Centralizar a gestão de vencimentos por empresa
* Garantir isolamento de dados entre tenants
* Disponibilizar operações de READ via GraphQL
* Manter integridade e controle dos vencimentos

---

# Modelo de Dados

## Tabela `expirations`

| Campo            | Tipo        | Descrição                     |
|------------------|-------------|-------------------------------|
| pk_id_expiration | INT         | Identificador único           |
| fk_id_sales      | INT         | Compra que gerou o vencimento |
| status           | VARCHAR(30) | Status do vencimento          |


---

## Constraints

```sql
ALTER TABLE products
ADD CONSTRAINT fk_expirations_sales
CONSTRAINT fk_expirations_sale
FOREIGN KEY (fk_id_sale)
REFERENCES sales(pk_id_sale)
ON DELETE CASCADE
```

---

# Enum ProductType

| Tipo    | Descrição          |
|---------|--------------------|
| EXPIRED | Vencido            |
| NEXT    | Vencimento próximo |
| NORMAL  | Vencimento normal  |

---

# Regras de Negócio

## Multi-Tenant

* Cada vencimento pertence a apenas uma empresa
* Utilizadores acessam apenas vencimentos da própria empresa
* Vencimentos de empresas diferentes podem ter dados iguais

---

## Ciclo de Vida

* **Criação:** vencimento nasce a partir de uma venda

---

## Regras de Domínio

* Vencimento deve ser automático

---

# Arquitetura

```text
GraphQL
   │
   ▼
ExpirationController
   │
   ▼
ExpirationService
   │
   ▼
ExpirationRepository
   │
   ▼
JPA Entity (Expiration)
```

---

# Estrutura do Projeto

```text
src/main/java/com/igniscore/api

├── controller
│   └── ExpirationController.java

├── service
│   └── ExpirationService.java

├── repository
│   └── ExpirationRepository.java

├── dto
│   ├── ExpirationDTO.java
│   └── ExpirationProjectionDTO.java

├── model
│   └── Expiration.java
```

---

# Operações Disponíveis

## Query – Listar Vencimentos

```graphql
query {
    expirations {
        saleId
        clientName
        saleDate
        dueDate
        totalSale
        status
    }
}
```

## Query – Listar Vencimentos por período

```graphql
query {
    expirationsByPeriod(
        startDate: "2026-06-03",
        endDate: "2026-06-30"
    ) {
        clientName
        saleDate
        dueDate
        totalSale
        status
    }
}
```

## Query – Listar Vencimentos por período de dias

```graphql
query {
    upcomingExpirations(days: 120) {
        clientName
        dueDate
        totalSale
        status
    }
}
```

## Query – Listar Vencimentos por cliente

```graphql
query {
    expirationsByClient(clientId: 6) {
        saleId
        clientName
        saleDate
        dueDate
        totalSale
        status
    }
}
```

---

# Segurança

## Regra Principal

Todas as operações são filtradas por empresa:

```sql
WHERE fk_id_company = :authenticatedCompanyId
```

---

## Controle de Acesso

* JWT obrigatório
* Empresa extraída do token
* Validação de ownership em todas as operações
* Nenhum acesso cross-tenant permitido

---

## Exemplo de Validação

```java
Company company = authenticatedUserService.getCompanyOrThrow();

        return expirationRepository
                .findExpirationsByCompanyId(company.getId())
        .stream()
                .map(this::mapExpiration)
                .toList();
```

---

# Testes

## Unitários

* ProductService
* Regras de negócio
* Validações de domínio

## Integração

* GraphQL queries
* Fluxo completo READ
* Isolamento multi-tenant

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
```

---

# Status

| Item                  | Status |
|-----------------------|--------|
| Criação de vencimento | ✅      |
| Atualização           | ❌      |
| Consulta por ID       | ✅      |
| Consulta paginada     | ❌      |
| Multi-tenancy         | ✅      |
| Soft delete           | ❌      |
| Auditoria             | ❌      |
| Histórico             | ❌      |
| Bulk operations       | ❌      |

---

**Projeto:** Igniscore API
**Módulo:** Expirations
**Tecnologias:** Spring Boot, GraphQL, JPA/Hibernate
**Status do Documento:** Padronizado com módulo Clients
