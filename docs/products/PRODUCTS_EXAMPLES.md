# Guia Prático – Exemplos de Uso do Módulo Products

## Índice

1. [Setup e Autenticação](#setup-e-autenticação)
2. [Criação de Produtos](#criação-de-produtos)
3. [Consultas e Buscas](#consultas-e-buscas)
4. [Atualizações](#atualizações)
5. [Inativação](#inativação)
6. [Casos de Erro Comuns](#casos-de-erro-comuns)
7. [Integração com Outros Módulos](#integração-com-outros-módulos)

---

## Setup e Autenticação

### 1. Obtenha um Token JWT

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { login(input: { email: \"admin@empresa.com\", password: \"senha123\" }) { token } }"
  }'
```

**Resposta:**

```json
{
  "data": {
    "login": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
  }
}
```

### 2. Configure o Token em Requisições Subsequentes

Use o token no header `Authorization`:

```bash
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Criação de Produtos

### Exemplo 1: Criar Produto Simples (Extintor)

**Requisição:**

```graphql
mutation CreateExtinguisher {
  storeProduct(
    input: {
      name: "Extintor de Pó ABC 1kg"
      type: EXTINGUISHER
      lot: "LOTE-2024-001"
      validity: "2026-12-31"
      price: 85.50
    }
  ) {
    id
    name
    type
    lot
    validity
    price
    status
    company {
      id
      name
    }
  }
}
```

**Resposta (Sucesso 200):**

```json
{
  "data": {
    "storeProduct": {
      "id": 1,
      "name": "Extintor de Pó ABC 1kg",
      "type": "EXTINGUISHER",
      "lot": "LOTE-2024-001",
      "validity": "2026-12-31",
      "price": 85.5,
      "status": true,
      "company": {
        "id": 5,
        "name": "Proteção Total Ltda"
      }
    }
  }
}
```

---

### Exemplo 2: Criar Produto do Tipo Serviço

**Requisição:**

```graphql
mutation CreateService {
  storeProduct(
    input: {
      name: "Recarga de Extintor ABC"
      type: SERVICE
      lot: "SRV-2024-001"
      validity: "2026-06-30"
      price: 35.00
    }
  ) {
    id
    name
    type
    price
  }
}
```

**Resposta:**

```json
{
  "data": {
    "storeProduct": {
      "id": 2,
      "name": "Recarga de Extintor ABC",
      "type": "SERVICE",
      "price": 35.0
    }
  }
}
```

---

### Exemplo 3: Criar Produto de Sistema (Sprinkler)

**Requisição:**

```graphql
mutation CreateSprinkler {
  storeProduct(
    input: {
      name: "Sprinkler Cabeça Padrão 1/2 NFG"
      type: SPRINKLER
      lot: "SPR-2024-150"
      validity: "2028-11-30"
      price: 125.75
    }
  ) {
    id
    name
    type
    lot
    validity
    price
    status
  }
}
```

**Resposta:**

```json
{
  "data": {
    "storeProduct": {
      "id": 3,
      "name": "Sprinkler Cabeça Padrão 1/2 NFG",
      "type": "SPRINKLER",
      "lot": "SPR-2024-150",
      "validity": "2028-11-30",
      "price": 125.75,
      "status": true
    }
  }
}
```

---

### Exemplo 4: Criar Produto com Preço Zero (Promocional)

**Requisição:**

```graphql
mutation CreatePromotion {
  storeProduct(
    input: {
      name: "Mangueira de Aço Inox 10m (Brinde)"
      type: HOSE
      lot: "PROM-2024-05"
      validity: "2025-12-31"
      price: 0
    }
  ) {
    id
    name
    type
    price
  }
}
```

**Resposta:**

```json
{
  "data": {
    "storeProduct": {
      "id": 4,
      "name": "Mangueira de Aço Inox 10m (Brinde)",
      "type": "HOSE",
      "price": 0
    }
  }
}
```

---

## Consultas e Buscas

### Exemplo 1: Listar Todos os Produtos (Paginado)

**Requisição:**

```graphql
query ListAllProducts {
  products(page: 0, size: 10) {
    id
    name
    type
    lot
    validity
    price
    status
    company {
      id
      name
    }
  }
}
```

**Resposta:**

```json
{
  "data": {
    "products": [
      {
        "id": 1,
        "name": "Extintor de Pó ABC 1kg",
        "type": "EXTINGUISHER",
        "lot": "LOTE-2024-001",
        "validity": "2026-12-31",
        "price": 85.5,
        "status": true,
        "company": {
          "id": 5,
          "name": "Proteção Total Ltda"
        }
      },
      {
        "id": 2,
        "name": "Recarga de Extintor ABC",
        "type": "SERVICE",
        "lot": "SRV-2024-001",
        "validity": "2026-06-30",
        "price": 35.0,
        "status": true,
        "company": {
          "id": 5,
          "name": "Proteção Total Ltda"
        }
      }
    ]
  }
}
```

---

### Exemplo 2: Buscar Produto Específico por ID

**Requisição:**

```graphql
query GetProductById {
  productById(id: 1) {
    id
    name
    type
    lot
    validity
    price
    status
  }
}
```

**Resposta:**

```json
{
  "data": {
    "productById": {
      "id": 1,
      "name": "Extintor de Pó ABC 1kg",
      "type": "EXTINGUISHER",
      "lot": "LOTE-2024-001",
      "validity": "2026-12-31",
      "price": 85.5,
      "status": true
    }
  }
}
```

---

### Exemplo 3: Listar Apenas Produtos Ativos

**Requisição:**

```graphql
query ListActiveProducts {
  activeProducts(page: 0, size: 20) {
    id
    name
    type
    price
  }
}
```

**Resposta:**

```json
{
  "data": {
    "activeProducts": [
      {
        "id": 1,
        "name": "Extintor de Pó ABC 1kg",
        "type": "EXTINGUISHER",
        "price": 85.5
      },
      {
        "id": 2,
        "name": "Recarga de Extintor ABC",
        "type": "SERVICE",
        "price": 35.0
      },
      {
        "id": 3,
        "name": "Sprinkler Cabeça Padrão 1/2 NFG",
        "type": "SPRINKLER",
        "price": 125.75
      }
    ]
  }
}
```

---

### Exemplo 4: Paginação - Segunda Página

**Requisição:**

```graphql
query ListPage2 {
  products(page: 1, size: 10) {
    id
    name
    type
    price
  }
}
```

**Resposta:**

```json
{
  "data": {
    "products": [
      {
        "id": 11,
        "name": "Detector de Fumaça",
        "type": "DETECTOR",
        "price": 75.0
      },
      {
        "id": 12,
        "name": "Central de Alarme 8 Zonas",
        "type": "CENTRAL",
        "price": 450.0
      }
    ]
  }
}
```

---

## Atualizações

### Exemplo 1: Atualizar Campo Único (Nome)

**Requisição:**

```graphql
mutation UpdateProductName {
  updateProduct(id: 1, input: { name: "Extintor de Pó ABC Premium 1kg" }) {
    id
    name
  }
}
```

**Resposta:**

```json
{
  "data": {
    "updateProduct": {
      "id": 1,
      "name": "Extintor de Pó ABC Premium 1kg"
    }
  }
}
```

---

### Exemplo 2: Atualizar Preço

**Requisição:**

```graphql
mutation UpdatePrice {
  updateProduct(id: 2, input: { price: 42.50 }) {
    id
    name
    price
  }
}
```

**Resposta:**

```json
{
  "data": {
    "updateProduct": {
      "id": 2,
      "name": "Recarga de Extintor ABC",
      "price": 42.5
    }
  }
}
```

---

### Exemplo 3: Atualizar Data de Validade

**Requisição:**

```graphql
mutation UpdateValidity {
  updateProduct(id: 3, input: { validity: "2029-06-30" }) {
    id
    name
    validity
  }
}
```

**Resposta:**

```json
{
  "data": {
    "updateProduct": {
      "id": 3,
      "name": "Sprinkler Cabeça Padrão 1/2 NFG",
      "validity": "2029-06-30"
    }
  }
}
```

---

### Exemplo 4: Atualizar Múltiplos Campos

**Requisição:**

```graphql
mutation UpdateMultiple {
  updateProduct(
    id: 1
    input: {
      name: "Extintor de Pó ABC 2kg"
      price: 145.00
      lot: "LOTE-2024-002"
      validity: "2027-12-31"
    }
  ) {
    id
    name
    price
    lot
    validity
  }
}
```

**Resposta:**

```json
{
  "data": {
    "updateProduct": {
      "id": 1,
      "name": "Extintor de Pó ABC 2kg",
      "price": 145.0,
      "lot": "LOTE-2024-002",
      "validity": "2027-12-31"
    }
  }
}
```

---

## Inativação

### Exemplo 1: Inativar Produto

**Requisição:**

```graphql
mutation DeactivateProduct {
  deactivateProduct(id: 4) {
    id
    name
    status
  }
}
```

**Resposta:**

```json
{
  "data": {
    "deactivateProduct": {
      "id": 4,
      "name": "Mangueira de Aço Inox 10m (Brinde)",
      "status": false
    }
  }
}
```

---

### Exemplo 2: Reativar Produto (Via Update)

**Requisição:**

```graphql
mutation ReactivateProduct {
  updateProduct(id: 4, input: { status: true }) {
    id
    name
    status
  }
}
```

**Resposta:**

```json
{
  "data": {
    "updateProduct": {
      "id": 4,
      "name": "Mangueira de Aço Inox 10m (Brinde)",
      "status": true
    }
  }
}
```

---

## Casos de Erro Comuns

### Erro 1: Produto Não Encontrado

**Requisição:**

```graphql
query {
  productById(id: 9999) {
    id
    name
  }
}
```

**Resposta (404 - Not Found):**

```json
{
  "errors": [
    {
      "message": "Product not found",
      "extensions": {
        "code": "NOT_FOUND"
      }
    }
  ]
}
```

---

### Erro 2: Tipo de Produto Inválido

**Requisição:**

```graphql
mutation {
  storeProduct(
    input: {
      name: "Teste"
      type: INVALID_TYPE
      lot: "TEST"
      validity: "2026-12-31"
      price: 10
    }
  ) {
    id
  }
}
```

**Resposta (400 - Bad Request):**

```json
{
  "errors": [
    {
      "message": "Invalid product type. Valid types: EXTINGUISHER, SERVICE, CONSUMABLE, ACCESSORY, HOSE, DETECTOR, SPRINKLER, CENTRAL, LIGHTING, DOOR, HYDRANT",
      "extensions": {
        "code": "BAD_REQUEST"
      }
    }
  ]
}
```

---

### Erro 3: Campos Obrigatórios Faltando

**Requisição:**

```graphql
mutation {
  storeProduct(
    input: { name: "Produto Incompleto", type: EXTINGUISHER, price: 50 }
  ) {
    id
  }
}
```

**Resposta (400 - Bad Request):**

```json
{
  "errors": [
    {
      "message": "Missing required fields: lot, validity",
      "extensions": {
        "code": "BAD_REQUEST"
      }
    }
  ]
}
```

---

### Erro 4: Data de Validade Inválida

**Requisição:**

```graphql
mutation {
  storeProduct(
    input: {
      name: "Produto"
      type: EXTINGUISHER
      lot: "LOT001"
      validity: "data-invalida"
      price: 50
    }
  ) {
    id
  }
}
```

**Resposta (400 - Bad Request):**

```json
{
  "errors": [
    {
      "message": "Invalid validity date format. Use YYYY-MM-DD",
      "extensions": {
        "code": "BAD_REQUEST"
      }
    }
  ]
}
```

---

### Erro 5: Preço Negativo

**Requisição:**

```graphql
mutation {
  storeProduct(
    input: {
      name: "Produto"
      type: EXTINGUISHER
      lot: "LOT001"
      validity: "2026-12-31"
      price: -50
    }
  ) {
    id
  }
}
```

**Resposta (400 - Bad Request):**

```json
{
  "errors": [
    {
      "message": "Price must be greater than or equal to 0",
      "extensions": {
        "code": "BAD_REQUEST"
      }
    }
  ]
}
```

---

### Erro 6: Acesso Negado (Outro Tenant)

**Scenario**: Usuário da Empresa B tenta acessar produto da Empresa A

**Requisição:**

```graphql
query {
  productById(id: 1) {
    id
    name
  }
}
```

**Resposta (403 - Forbidden):**

```json
{
  "errors": [
    {
      "message": "Forbidden",
      "extensions": {
        "code": "FORBIDDEN"
      }
    }
  ]
}
```

---

### Erro 7: Token JWT Expirado

**Requisição (sem token ou token inválido):**

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "query { products(page: 0, size: 10) { id } }"}'
```

**Resposta (401 - Unauthorized):**

```json
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
```

---

## Integração com Outros Módulos

### Integração com Vendas (Sales)

#### Criar Venda com Produto

**Pré-requisito**: Produto deve estar ativo

**Requisição:**

```graphql
mutation CreateSaleWithProduct {
  storeSale(
    input: {
      clientId: 5
      items: [{ productId: 1, quantity: 2, unitPrice: 85.50 }]
    }
  ) {
    id
    total
    items {
      product {
        id
        name
      }
      quantity
      unitPrice
    }
  }
}
```

**Resposta:**

```json
{
  "data": {
    "storeSale": {
      "id": 1,
      "total": 171.0,
      "items": [
        {
          "product": {
            "id": 1,
            "name": "Extintor de Pó ABC 1kg"
          },
          "quantity": 2,
          "unitPrice": 85.5
        }
      ]
    }
  }
}
```

---

### Integração com Locações (Rentals)

#### Criar Locação com Produto

**Pré-requisito**: Produto deve estar ativo

**Requisição:**

```graphql
mutation CreateRentalWithProduct {
  storeRental(
    input: {
      clientId: 5
      items: [
        {
          productId: 2
          quantity: 1
          dailyRate: 50.00
          startDate: "2026-04-26"
          endDate: "2026-05-26"
        }
      ]
    }
  ) {
    id
    startDate
    endDate
    items {
      product {
        id
        name
        type
      }
      dailyRate
    }
  }
}
```

**Resposta:**

```json
{
  "data": {
    "storeRental": {
      "id": 1,
      "startDate": "2026-04-26",
      "endDate": "2026-05-26",
      "items": [
        {
          "product": {
            "id": 2,
            "name": "Extintor de Pó ABC 1kg",
            "type": "EXTINGUISHER"
          },
          "dailyRate": 50.0
        }
      ]
    }
  }
}
```

---

### Impacto de Inativar Produto

**Cenário**: Inativar produto que tem vendas ativas

**Observação**: Produto pode ser inativado mesmo com vendas

- Vendas existentes mantêm referência ao produto
- Novo produto não pode ser incluído em vendas
- Histórico de vendas é mantido para auditoria

---

## Dicas de Performance

### 1. Utilize Paginação

```graphql
query OptimizedList {
  products(page: 0, size: 50) {
    id
    name
    type
  }
}
```

- Máximo recomendado: 50 registros por página
- Reduz tempo de resposta
- Economiza banda

### 2. Solicite Apenas Campos Necessários

```graphql
query OptimizedFields {
  products(page: 0, size: 10) {
    id
    name
    price
  }
}
```

- GraphQL retorna apenas campos solicitados
- Reduz tamanho de resposta
- Melhora performance

### 3. Filtre por Status para Operações

```graphql
query ListActiveOnly {
  activeProducts(page: 0, size: 20) {
    id
    name
  }
}
```

- Já filtra inativos automaticamente
- Mais eficiente que filtrar em aplicação
- Reduz volume de dados

---
