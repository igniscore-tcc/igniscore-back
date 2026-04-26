# README Técnico – Módulo Products (API GraphQL / Spring Boot)

## Visão Geral

O módulo **products** é responsável pelo gerenciamento de produtos e serviços pertencentes às empresas (tenants) usuárias da plataforma. Cada produto está estritamente vinculado a uma única empresa, garantindo isolamento completo dos dados em ambiente multi-tenant.

O módulo atende empresas do segmento de prevenção e combate a incêndio, permitindo o cadastro de:

- Mercadorias
- Equipamentos
- Consumíveis
- Serviços especializados

### Objetivos Principais

- Centralizar o gerenciamento de produtos por empresa
- Garantir isolamento de dados entre empresas (tenants)
- Fornecer operações CRUD completas via GraphQL
- Manter histórico e rastreabilidade de produtos
- Integrar-se com módulos de vendas e locações

---

## Modelo de Dados

### Tabela: `products`

| Campo           | Tipo          | Constraint             | Descrição                       |
| --------------- | ------------- | ---------------------- | ------------------------------- |
| `pk_id_prod`    | INT           | PK, Auto-increment     | Identificador único do produto  |
| `name_prod`     | VARCHAR(150)  | NOT NULL               | Nome do produto                 |
| `type_prod`     | VARCHAR(50)   | NOT NULL               | Tipo do produto (enum)          |
| `validity_prod` | DATE          | NOT NULL               | Data de validade                |
| `lot_prod`      | VARCHAR(50)   | NOT NULL               | Lote do produto                 |
| `price_prod`    | DECIMAL(10,2) | NOT NULL               | Preço padrão de venda           |
| `status`        | BOOLEAN       | NOT NULL, DEFAULT true | Status lógico (ativo/inativo)   |
| `fk_id_company` | INT           | FK, NOT NULL           | Empresa proprietária do produto |
| `created_at`    | TIMESTAMP     | NOT NULL               | Data de criação                 |
| `updated_at`    | TIMESTAMP     | -                      | Data da última atualização      |

### Relacionamentos

```sql
ALTER TABLE products ADD CONSTRAINT fk_products_company
    FOREIGN KEY (fk_id_company) REFERENCES companies(pk_id_company)
    ON DELETE CASCADE;
```

#### Integrações com Outros Módulos

- **Sales**: Produtos podem ser incluídos em itens de venda
- **Rentals**: Produtos podem ser alugados através de locações
- **Companies**: Cada produto pertence exclusivamente a uma empresa

---

## Enum ProductType

Tipos de produtos suportados pelo sistema:

| Tipo           | Descrição                                 | Segmento    |
| -------------- | ----------------------------------------- | ----------- |
| `EXTINGUISHER` | Extintor de incêndio (portátil/móvel)     | Equipamento |
| `SERVICE`      | Serviços de manutenção, recarga, inspeção | Serviço     |
| `CONSUMABLE`   | Materiais consumíveis                     | Consumível  |
| `ACCESSORY`    | Acessórios e complementos                 | Acessório   |
| `HOSE`         | Mangueiras especializadas                 | Equipamento |
| `DETECTOR`     | Detectores de fumaça e calor              | Equipamento |
| `SPRINKLER`    | Sistema de sprinklers automáticos         | Equipamento |
| `CENTRAL`      | Central de alarme/controle                | Equipamento |
| `LIGHTING`     | Iluminação de emergência                  | Equipamento |
| `DOOR`         | Portas corta-fogo e painéis               | Equipamento |
| `HYDRANT`      | Hidrantes e conexões                      | Equipamento |

---

## Regras de Negócio

### Isolamento Multi-Tenant

- Cada produto pertence exclusivamente a uma empresa
- Usuários podem acessar apenas produtos da própria empresa
- Nenhum usuário pode acessar produtos de outra empresa
- Controle de acesso baseado em contexto de empresa (tenant ID)

### Ciclo de Vida do Produto

- **Criação**: Requer nome, tipo, validade, lote, preço e empresa válida
- **Atualização**: Apenas campos fornecidos são atualizados (PATCH semântico)
- **Inativação**: Produtos são marcados como inativos sem exclusão física
- **Deleção**: Produtos não são excluídos fisicamente; apenas inativados

### Dados e Valores

- Nomes de produtos podem ser duplicados, inclusive dentro da mesma empresa
- Produtos podem existir em múltiplas empresas simultaneamente
- O lote é obrigatório para todos os produtos
- A validade é obrigatória para todos os produtos
- O preço pode ser igual a zero (produtos promocionais ou serviços)
- Preço padrão de venda em decimal (até 2 casas decimais)

### Operacionalidade

- Produtos inativos não são exibidos em consultas operacionais padrão
- Produtos inativos não podem ser incluídos em vendas
- Produtos inativos não podem ser locados
- Produtos podem ser inativados mesmo possuindo vendas ou locações vinculadas
- A validade pode ser alterada após o cadastro do produto
- O status é alterável independentemente de outras condições

### Acesso e Autorização

- Acesso restrito a usuários autenticados
- Filtragem obrigatória por `fk_id_company`
- Isolamento completo entre tenants garantido em todas as operações

---

## Arquitetura da Camada de Products

```
┌─────────────────────────────────────┐
│     GraphQL Schema (schema.graphqls) │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│ ProductController (Spring GraphQL)  │
│ - storeProduct (Mutation)           │
│ - updateProduct (Mutation)          │
│ - products (Query - paginado)       │
│ - productById (Query)               │
│ - deactivateProduct (Mutation)      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   ProductService (Business Layer)   │
│ - store(ProductDTO)                 │
│ - update(ProductUpdateDTO)          │
│ - findById(Integer)                 │
│ - findByCompany(Integer, Pageable)  │
│ - deactivate(Integer)               │
│ - findActive(Integer, Pageable)     │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   ProductRepository (Data Access)   │
│ - findByIdAndCompanyId()            │
│ - findByCompanyId(Pageable)         │
│ - findByCompanyIdAndStatus()        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│    Product (JPA Entity Model)       │
│ @Entity @Table("products")          │
└─────────────────────────────────────┘
```

---

## Operações Disponíveis (GraphQL)

### Queries

#### 1. Listar Produtos (Paginado)

```graphql
query {
  products(page: Int!, size: Int!) {
    id
    name
    type
    validity
    lot
    price
    status
    company { id name }
  }
}
```

- Retorna produtos da empresa autenticada
- Pagina resultados com offset/limit
- Filtra automaticamente por tenant (fk_id_company)
- Pode incluir ou excluir inativos (dependendo do filtro)

#### 2. Buscar Produto por ID

```graphql
query {
  productById(id: Int!) {
    id
    name
    type
    validity
    lot
    price
    status
    company { id name }
  }
}
```

- Retorna um produto específico
- Valida que o produto pertence à empresa autenticada
- Retorna erro 404 se não encontrado

#### 3. Listar Produtos Ativos

```graphql
query {
  activeProducts(page: Int!, size: Int!) {
    id
    name
    type
    price
    validity
  }
}
```

- Retorna apenas produtos com `status = true`
- Útil para operações de venda e locação
- Pagina resultados

### Mutations

#### 1. Criar Produto

```graphql
mutation {
  storeProduct(input: ProductDTO!) {
    id
    name
    type
    lot
    validity
    price
    status
    number
  }
}
```

**Input (ProductDTO)**:

```graphql
input ProductDTO {
  name: String!
  type: ProductType!
  validity: Date!
  lot: String!
  price: BigDecimal!
}
```

**Regras na Criação**:

- Todos os campos são obrigatórios
- Tipo deve estar em ProductType enum
- Validade deve ser data válida (futura recomendada)
- Lot não pode ser vazio
- Preço deve ser >= 0
- Status é definido como `true` (ativo) por padrão
- Empresa é inferida do usuário autenticado

#### 2. Atualizar Produto

```graphql
mutation {
  updateProduct(id: Int!, input: ProductUpdateDTO!) {
    id
    name
    type
    validity
    lot
    price
    status
  }
}
```

**Input (ProductUpdateDTO)**:

```graphql
input ProductUpdateDTO {
  name: String
  type: ProductType
  validity: Date
  lot: String
  price: BigDecimal
  status: Boolean
}
```

**Regras na Atualização**:

- Apenas campos fornecidos são atualizados (PATCH semântico)
- Valida que o produto pertence à empresa autenticada
- Atualiza `updated_at` automaticamente
- Pode alterar qualquer campo, inclusivamente status

#### 3. Inativar Produto

```graphql
mutation {
  deactivateProduct(id: Int!) {
    id
    name
    status
  }
}
```

- Altera `status` para `false`
- Produto permanece no banco (soft delete)
- Produto não pode ser vendido ou alugado após inativação
- Pode ser reativado através de `updateProduct`

---

## Validações e Regras

### Validações de Segurança por Operação

#### CREATE (storeProduct)

- Autenticação: Requer JWT válido
- Autorização: Usuário logado pode criar
- Tenant: Automaticamente associado à empresa do usuário
- CNPJ-equivalente: Empresa deve estar ativa
- Validações de formato: Todos os campos obrigatórios

#### READ (products, productById)

- Autenticação: Requer JWT válido
- Autorização: Apenas clientes da empresa
- Tenant: Filtra automaticamente por fk_id_company
- Paginação: Máximo de registros por página configurável

#### UPDATE (updateProduct)

- Autenticação: Requer JWT válido
- Autorização: Produto deve pertencer à empresa
- Tenant: Valida propriedade antes de atualizar
- Status: Pode ser alterado sem restrições

#### DELETE (Soft Delete via status)

- Autenticação: Requer JWT válido
- Autorização: Produto deve pertencer à empresa
- Estratégia: Soft delete (apenas marca status como false)
- Reversibilidade: Pode ser reativado

### Validações de Formato

- **type_prod**: Deve estar no enum ProductType
- **validity_prod**: Formato DATE válido
- **lot_prod**: String não vazia, máximo 50 caracteres
- **price_prod**: Decimal com 2 casas, >= 0
- **name_prod**: String não vazia, máximo 150 caracteres

---

## Tratamento de Erros

### Erros Comuns

| Código | Mensagem                | Cenário                                        |
| ------ | ----------------------- | ---------------------------------------------- |
| 404    | Product not found       | Produto não existe ou não pertence à empresa   |
| 400    | Invalid product type    | Type fornecido não está no enum ProductType    |
| 400    | Invalid validity date   | Validade em formato inválido                   |
| 400    | Missing required fields | Campo obrigatório não fornecido                |
| 401    | Unauthorized            | Token JWT inválido ou expirado                 |
| 403    | Forbidden               | Usuário tenta acessar produto de outra empresa |
| 500    | Internal Server Error   | Erro no servidor (nunca expor detalhes)        |

### Mensagens de Erro Seguras

- Erros não revelam detalhes internos
- Não expõem IDs de outras empresas
- Não divulgam estrutura de banco de dados
- Mensagens genéricas para erros de segurança

---

## Segurança e Multi-Tenancy

### Isolamento de Dados

- Filtro obrigatório `WHERE fk_id_company = :companyId`
- Aplicado em todas as queries
- Implementado no nível da camada de serviço
- Testado em testes de segurança

### Autenticação

- JWT Bearer Token obrigatório
- Token extraído do header `Authorization`
- Validação de assinatura do token
- Expiração configurável

### Autorização

- Contexto de empresa extraído do JWT
- Cada operação valida propriedade do recurso
- Nenhuma exceção a regra multi-tenant
- Logs de acesso negado

### Proteção contra Ataques

- **SQL Injection**: Queries parametrizadas via JPA
- **CSRF**: Proteção nativa do Spring Security
- **XSS**: Validação de entrada padrão
- **Broken Authorization**: Validação em toda operação
- **Information Disclosure**: Mensagens genéricas de erro

---

## Performance e Otimizações

### Indexação

```sql
CREATE INDEX idx_products_company ON products(fk_id_company);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_type ON products(type_prod);
CREATE INDEX idx_products_company_status ON products(fk_id_company, status);
```

### Paginação

- Padrão: Máximo 50 registros por página
- Configurável em `application.yaml`
- Evita transferência de dados em massa
- Melhora resposta de listagens grandes

### Lazy Loading

- Relacionamentos utilizados com `FetchType.LAZY`
- Evita N+1 queries
- GraphQL retorna apenas campos solicitados

---

## Testes

### Testes Unitários

- ProductService: Lógica de negócio
- ProductRepository: Queries customizadas
- Validações: Constraints de modelo

### Testes de Integração

- GraphQL Queries e Mutations
- Fluxo completo CREATE-READ-UPDATE-DELETE
- Isolamento multi-tenant

### Testes de Segurança

- Acesso negado entre tenants
- Autenticação obrigatória
- Validação de JWT
- Casos de erro

---

## Configuração e Deployment

### Environment Variables

```yaml
# application.yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/igniscore
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

app:
  jwt:
    secret: your-secret-key
    expiration: 86400000 # 24 horas
  pagination:
    max-size: 50
    default-size: 20
```

### Build

```bash
mvn clean package
```

### Execução

```bash
java -jar target/igniscore-api.jar
```

### Docker

```dockerfile
FROM openjdk:21-slim
COPY target/igniscore-api.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

---

## Integrações com Outros Módulos

### Vendas (Sales)

- Produtos são incluídos em itens de venda
- Apenas produtos ativos podem ser vendidos
- Preço do produto pode ser sobrescrito na venda
- Histórico de preço mantido na tabela de vendas

### Locações (Rentals)

- Produtos podem ser alugados
- Apenas produtos ativos disponíveis para locação
- Período de aluguel não afeta validade original
- Controle de inventário operacional

### Empresas (Companies)

- Produto vinculado 1:N com empresa
- Exclusão de empresa deleta todos os produtos (CASCADE)
- Isolamento garantido não permite acesso cruzado

---

## Sumário

| Aspecto                 | Detalhes                                     |
| ----------------------- | -------------------------------------------- |
| **Tabela Principal**    | `products`                                   |
| **Campos Principais**   | id, name, type, validity, lot, price, status |
| **Modelo**              | Multi-tenant (1 produto : 1 empresa)         |
| **Isolamento**          | Por `fk_id_company`                          |
| **Autenticação**        | JWT Bearer Token                             |
| **API**                 | GraphQL (queries + mutations)                |
| **Operações CRUD**      | Create, Read, Update, Soft Delete            |
| **Status**              | Ativo em produção                            |
| **Versão Documentação** | 1.0 - 26/04/2026                             |
