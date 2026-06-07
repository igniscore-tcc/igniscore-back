Abaixo está o **README do módulo Products reestruturado e padronizado** no mesmo nível do exemplo do Clients, com linguagem mais consistente, menos redundância e organização mais “clean” para documentação técnica.

---

# README Técnico – Módulo Products (API GraphQL / Spring Boot)

## Visão Geral

O módulo **Products** é responsável pela gestão de produtos e serviços vinculados às empresas que utilizam a plataforma Igniscore.

Cada produto pertence obrigatoriamente a uma empresa, e todas as operações respeitam o contexto multi-tenant da aplicação, garantindo isolamento completo de dados entre organizações.

O módulo fornece operações de cadastro, atualização e consulta de produtos por API GraphQL, sendo utilizado diretamente por módulos de vendas e locações.

---

## Objetivos

* Centralizar a gestão de produtos por empresa
* Garantir isolamento de dados entre tenants
* Disponibilizar operações de CRUD via GraphQL
* Manter integridade e rastreabilidade dos produtos
* Suportar integração com vendas e locações

---

# Modelo de Dados

## Tabela `products`

| Campo         | Tipo          | Descrição              |
|---------------|---------------|------------------------|
| pk_id_prod    | INT           | Identificador único    |
| name_prod     | VARCHAR(150)  | Nome do produto        |
| type_prod     | VARCHAR(50)   | Tipo do produto (enum) |
| validity_prod | DATE          | Data de validade       |
| lot_prod      | VARCHAR(50)   | Lote do produto        |
| price_prod    | DECIMAL(10,2) | Preço padrão de venda  |
| status        | BOOLEAN       | Status (ativo/inativo) |
| fk_id_company | INT           | Empresa proprietária   |
| created_at    | TIMESTAMP     | Data de criação        |
| updated_at    | TIMESTAMP     | Última atualização     |

---

## Constraints

```sql
ALTER TABLE products
ADD CONSTRAINT fk_products_company
FOREIGN KEY (fk_id_company)
REFERENCES companies(pk_id_company)
ON DELETE CASCADE;
```

---

# Enum ProductType

| Tipo         | Descrição                                |
|--------------|------------------------------------------|
| EXTINGUISHER | Extintores de incêndio                   |
| SERVICE      | Serviços (manutenção, inspeção, recarga) |
| CONSUMABLE   | Materiais consumíveis                    |
| ACCESSORY    | Acessórios                               |
| HOSE         | Mangueiras                               |
| DETECTOR     | Detectores de fumaça e calor             |
| SPRINKLER    | Sistemas de sprinklers                   |
| CENTRAL      | Centrais de alarme                       |
| LIGHTING     | Iluminação de emergência                 |
| DOOR         | Portas corta-fogo                        |
| HYDRANT      | Hidrantes                                |

---

# Regras de Negócio

## Multi-Tenant

* Cada produto pertence a apenas uma empresa
* Utilizadores acessam apenas produtos da própria empresa
* Produtos de empresas diferentes podem ter dados iguais
* O isolamento é garantido por `fk_id_company`

---

## Ciclo de Vida

* **Criação:** produto nasce ativo por padrão
* **Atualização:** ‘PATCH’ semântico (campos parciais)
* **Inativação:** soft delete via status = false
* **Reativação:** permitida via ‘update’

---

## Regras de Domínio

* Nome pode ser duplicado
* Lote é obrigatório
* Validade é obrigatória
* Preço pode ser zero
* Produtos inativos não participam de vendas ou locações
* Produtos não são removidos fisicamente

---

# Arquitetura

```text
GraphQL
   │
   ▼
ProductController
   │
   ▼
ProductService
   │
   ▼
ProductRepository
   │
   ▼
JPA Entity (Product)
```

---

# Estrutura do Projeto

```text
src/main/java/com/igniscore/api

├── controller
│   └── ProductController.java

├── service
│   └── ProductService.java

├── repository
│   └── ProductRepository.java

├── dto
│   ├── ProductDTO.java
│   └── ProductUpdateDTO.java

├── model
│   └── Product.java
```

---

# Operações Disponíveis

## Query – Listar Produtos

```graphql
query {
  products(page: 0, size: 10) {
    id
    name
    type
    validity
    lot
    price
    status
    company {
      id
      name
    }
  }
}
```

---

## Mutation – Atualizar Produto

```graphql
mutation {
  updateProduct(
    id: 1,
    input: {
      price: 180.00
      status: true
    }
  ) {
    id
    name
    price
    status
  }
}
```

---

## Mutation – Inativar Produto

```graphql
mutation {
    deleteProduct(id: 1) {
    id
    name
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
if (!product.getCompany().getId().equals(companyId)) {
    throw new AccessDeniedException("Product not found");
}
```

---

# Tratamento de Erros

| Código            | Descrição              |
|-------------------|------------------------|
| PRODUCT_NOT_FOUND | Produto não encontrado |
| INVALID_TYPE      | Tipo inválido          |
| INVALID_DATE      | Data inválida          |
| UNAUTHORIZED      | Acesso negado          |
| INVALID_COMPANY   | Empresa inválida       |

---

## Exemplo

```json
{
  "errors": [
    {
      "message": "Product not found",
      "extensions": {
        "code": "PRODUCT_NOT_FOUND"
      }
    }
  ]
}
```

---

# Índices Recomendados

```sql
CREATE INDEX idx_products_company
ON products(fk_id_company);
```

```sql
CREATE INDEX idx_products_status
ON products(status);
```

```sql
CREATE INDEX idx_products_company_status
ON products(fk_id_company, status);
```

---

# Testes

## Unitários

* ProductService
* Regras de negócio
* Validações de domínio

## Integração

* GraphQL queries e mutations
* Fluxo completo CRUD
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

| Item                 | Status |
|----------------------|--------|
| Cadastro de produtos | ✅      |
| Atualização          | ✅      |
| Consulta por ID      | ✅      |
| Consulta paginada    | ✅      |
| Multi-tenancy        | ✅      |
| Soft delete          | ✅      |
| Auditoria            | ✅      |
| Histórico            | ❌      |
| Bulk operations      | ❌      |

---

**Projeto:** Igniscore API
**Módulo:** Products
**Tecnologias:** Spring Boot, GraphQL, JPA/Hibernate
**Status do Documento:** Padronizado com módulo Clients
