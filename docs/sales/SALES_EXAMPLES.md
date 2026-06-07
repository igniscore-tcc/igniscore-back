# Guia Prático – Exemplos de Uso do Módulo Sales

## Índice

1. [Setup e Autenticação](#setup-e-autenticação)
2. [Criação de Vendas](#criação-de-vendas)
3. [Operações com Itens](#operações-com-itens)
4. [Operações com Descontos](#operações-com-descontos)
5. [Transições de Status](#transições-de-status)
6. [Consultas e Buscas](#consultas-e-buscas)
7. [Casos de Erro Comuns](#casos-de-erro-comuns)
8. [Fluxo Completo](#fluxo-completo-criar-venda-com-itens-e-completar)
9. [Integração com Outros Módulos](#integração-com-outros-módulos)
10. [Dicas de Performance](#dicas-de-performance)

---

## Setup e Autenticação

### 1. Obtenha um Token JWT

Primeiramente, faça login para obter um token JWT:

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
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBlbXByZXNhLmNvbSIsImNvbXBhbnlfaWQiOjUsInJvbGVzIjpbIkFETUlOIl0sImlhdCI6MTcxNzI0NTAwMCwiZXhwIjoxNzE3MzMxNDAwfQ.abc123..."
    }
  }
}
```

### 2. Configure o Token em Requisições Subsequentes

Use o token no header `Authorization`:

```bash
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 3. Salve o Token em uma Variável

```bash
export JWT_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Depois use assim:
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query": "query { sales(page: 0, size: 10) { id } }"}'
```

---

## Criação de Vendas

### Exemplo 1: Criar Venda Simples

**Requisição:**

```graphql
mutation CreateSimpleSale {
  storeSale(
    input: {
      clientId: 5
      date: "2026-06-01"
      paymentMethod: CREDIT_CARD
      dueDate: "2026-07-01"
    }
  ) {
    id
    status
    quantityItems
    total
    date
    paymentMethod
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

**Resposta (Sucesso 200):**

```json
{
  "data": {
    "storeSale": {
      "id": 1,
      "status": "PENDING",
      "quantityItems": 0,
      "total": "0.00",
      "date": "2026-06-01",
      "paymentMethod": "CREDIT_CARD",
      "dueDate": "2026-07-01",
      "client": {
        "id": 5,
        "name": "Acme Corporation",
        "cnpj": "12.345.678/0001-99"
      },
      "company": {
        "id": 10,
        "name": "Proteção Total Ltda"
      }
    }
  }
}
```

---

### Exemplo 2: Criar Venda com Método de Pagamento Diferente

**Requisição:**

```graphql
mutation CreateSaleWithTransfer {
  storeSale(
    input: {
      clientId: 5
      date: "2026-06-01"
      paymentMethod: BANK_TRANSFER
      dueDate: "2026-06-15"
    }
  ) {
    id
    paymentMethod
    dueDate
  }
}
```

**Resposta:**

```json
{
  "data": {
    "storeSale": {
      "id": 2,
      "paymentMethod": "BANK_TRANSFER",
      "dueDate": "2026-06-15"
    }
  }
}
```

---

### Exemplo 3: Criar Venda com Vencimento Estendido

**Requisição:**

```graphql
mutation CreateSaleWithExtendedDueDate {
  storeSale(
    input: {
      clientId: 8
      date: "2026-06-01"
      paymentMethod: INSTALLMENT
      dueDate: "2026-12-01"
    }
  ) {
    id
    date
    dueDate
    paymentMethod
  }
}
```

---

## Operações com Itens

### Exemplo 1: Adicionar Item Simples

Depois de criar uma venda (veja ID 1 do exemplo anterior):

**Requisição:**

```graphql
mutation AddSingleItem {
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
      product {
        id
        name
        type
      }
    }
  }
}
```

**Resposta (Sucesso 200):**

```json
{
  "data": {
    "addSaleItem": {
      "id": 1,
      "quantityItems": 5,
      "total": "427.50",
      "items": [
        {
          "id": 1,
          "quantity": 5,
          "unitPrice": "85.50",
          "total": "427.50",
          "product": {
            "id": 10,
            "name": "Extintor de Pó ABC 1kg",
            "type": "EXTINGUISHER"
          }
        }
      ]
    }
  }
}
```

---

### Exemplo 2: Adicionar Múltiplos Itens à Mesma Venda

**Primeira chamada:**

```graphql
mutation AddFirstItem {
  addSaleItem(
    input: { saleId: 1, productId: 10, quantity: 5, unitPrice: 85.50 }
  ) {
    id
    quantityItems
    total
  }
}
```

**Segunda chamada:**

```graphql
mutation AddSecondItem {
  addSaleItem(
    input: { saleId: 1, productId: 15, quantity: 2, unitPrice: 150.00 }
  ) {
    id
    quantityItems
    total
    items {
      id
      product {
        id
        name
      }
      quantity
      total
    }
  }
}
```

**Resposta:**

```json
{
  "data": {
    "addSecondItem": {
      "id": 1,
      "quantityItems": 7,
      "total": "727.50",
      "items": [
        {
          "id": 1,
          "product": {
            "id": 10,
            "name": "Extintor de Pó ABC 1kg"
          },
          "quantity": 5,
          "total": "427.50"
        },
        {
          "id": 2,
          "product": {
            "id": 15,
            "name": "Serviço de Inspeção Anual"
          },
          "quantity": 2,
          "total": "300.00"
        }
      ]
    }
  }
}
```

---

### Exemplo 3: Remover Item da Venda

**Requisição:**

```graphql
mutation RemoveItem {
  removeSaleItem(saleItemId: 2) {
    id
    quantityItems
    total
    items {
      id
      product {
        name
      }
    }
  }
}
```

**Resposta:**

```json
{
  "data": {
    "removeSaleItem": {
      "id": 1,
      "quantityItems": 5,
      "total": "427.50",
      "items": [
        {
          "id": 1,
          "product": {
            "name": "Extintor de Pó ABC 1kg"
          }
        }
      ]
    }
  }
}
```

---

## Operações com Descontos

### Exemplo 1: Aplicar Desconto Simples

**Requisição:**

```graphql
mutation ApplyDiscount {
  applySaleDiscount(input: { saleId: 1, discountAmount: 50.00 }) {
    id
    total
    discount
    quantityItems
  }
}
```

**Resposta (Sucesso 200):**

```json
{
  "data": {
    "applySaleDiscount": {
      "id": 1,
      "total": "377.50",
      "discount": "50.00",
      "quantityItems": 5
    }
  }
}
```

---

### Exemplo 2: Aplicar Desconto Percentual

Para aplicar um desconto de 10% em uma venda de 427.50:

```
desconto = 427.50 × 0.10 = 42.75
```

**Requisição:**

```graphql
mutation ApplyPercentageDiscount {
  applySaleDiscount(input: { saleId: 1, discountAmount: 42.75 }) {
    id
    total
    discount
  }
}
```

---

### Exemplo 3: Remover Desconto

Para remover um desconto, aplique 0:

**Requisição:**

```graphql
mutation RemoveDiscount {
  applySaleDiscount(input: { saleId: 1, discountAmount: 0.00 }) {
    id
    total
    discount
  }
}
```

---

## Transições de Status

### Exemplo 1: Completar Venda

**Requisição:**

```graphql
mutation CompleteSale {
  completeSale(saleId: 1) {
    id
    status
    total
    quantityItems
    date
  }
}
```

**Resposta (Sucesso 200):**

```json
{
  "data": {
    "completeSale": {
      "id": 1,
      "status": "COMPLETED",
      "total": "427.50",
      "quantityItems": 5,
      "date": "2026-06-01"
    }
  }
}
```

---

### Exemplo 2: Cancelar Venda

**Requisição:**

```graphql
mutation CancelSale {
  cancelSale(saleId: 1) {
    id
    status
  }
}
```

**Resposta:**

```json
{
  "data": {
    "cancelSale": {
      "id": 1,
      "status": "CANCELED"
    }
  }
}
```

---

## Consultas e Buscas

### Exemplo 1: Listar Todas as Vendas da Empresa

**Requisição:**

```graphql
query ListAllSales {
  sales(page: 0, size: 10) {
    id
    total
    date
    status
    paymentMethod
    client {
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
    "sales": [
      {
        "id": 1,
        "total": "427.50",
        "date": "2026-06-01",
        "status": "PENDING",
        "paymentMethod": "CREDIT_CARD",
        "client": {
          "id": 5,
          "name": "Acme Corporation"
        }
      },
      {
        "id": 2,
        "total": "1250.00",
        "date": "2026-06-02",
        "status": "COMPLETED",
        "paymentMethod": "BANK_TRANSFER",
        "client": {
          "id": 8,
          "name": "Tech Solutions Ltd"
        }
      }
    ]
  }
}
```

---

### Exemplo 2: Buscar Venda por ID com Todos os Detalhes

**Requisição:**

```graphql
query GetSaleDetails {
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
      cnpj
      email
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
        validity
        lot
        price
      }
    }
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
    "saleById": {
      "id": 1,
      "quantityItems": 5,
      "discount": "0.00",
      "total": "427.50",
      "date": "2026-06-01",
      "paymentMethod": "CREDIT_CARD",
      "status": "PENDING",
      "dueDate": "2026-07-01",
      "client": {
        "id": 5,
        "name": "Acme Corporation",
        "cnpj": "12.345.678/0001-99",
        "email": "contato@acme.com.br"
      },
      "items": [
        {
          "id": 1,
          "quantity": 5,
          "unitPrice": "85.50",
          "total": "427.50",
          "product": {
            "id": 10,
            "name": "Extintor de Pó ABC 1kg",
            "type": "EXTINGUISHER",
            "validity": "2026-12-31",
            "lot": "LOTE-2024-001",
            "price": "85.50"
          }
        }
      ],
      "company": {
        "id": 10,
        "name": "Proteção Total Ltda"
      }
    }
  }
}
```

---

### Exemplo 3: Listar Vendas de um Cliente Específico

**Requisição:**

```graphql
query GetClientSales {
  salesByClient(clientId: 5, page: 0, size: 20) {
    id
    total
    date
    status
    paymentMethod
  }
}
```

**Resposta:**

```json
{
  "data": {
    "salesByClient": [
      {
        "id": 1,
        "total": "427.50",
        "date": "2026-06-01",
        "status": "PENDING",
        "paymentMethod": "CREDIT_CARD"
      },
      {
        "id": 5,
        "total": "850.00",
        "date": "2026-05-20",
        "status": "COMPLETED",
        "paymentMethod": "BANK_TRANSFER"
      }
    ]
  }
}
```

---

## Casos de Erro Comuns

### Erro 1: Cliente Inativo

**Requisição:**

```graphql
mutation {
  storeSale(
    input: {
      clientId: 99
      date: "2026-06-01"
      paymentMethod: CREDIT_CARD
      dueDate: "2026-07-01"
    }
  ) {
    id
  }
}
```

**Resposta (Erro 400):**

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

### Erro 2: Venda Não Encontrada

**Requisição:**

```graphql
query {
  saleById(id: 9999) {
    id
    total
  }
}
```

**Resposta (Erro 404):**

```json
{
  "errors": [
    {
      "message": "Sale not found",
      "extensions": {
        "code": "SALE_NOT_FOUND",
        "timestamp": "2026-06-01T15:30:00Z"
      }
    }
  ]
}
```

---

### Erro 3: Desconto Inválido

**Requisição:**

```graphql
mutation {
  applySaleDiscount(input: { saleId: 1, discountAmount: -50.00 }) {
    id
  }
}
```

**Resposta (Erro 400):**

```json
{
  "errors": [
    {
      "message": "Discount cannot be negative",
      "extensions": {
        "code": "INVALID_DISCOUNT",
        "timestamp": "2026-06-01T15:30:00Z"
      }
    }
  ]
}
```

---

### Erro 4: Desconto Excede Total

**Requisição:**

```graphql
mutation {
  applySaleDiscount(input: { saleId: 1, discountAmount: 500.00 }) {
    id
  }
}
```

**Resposta (Erro 400):**

```json
{
  "errors": [
    {
      "message": "Discount cannot exceed total",
      "extensions": {
        "code": "INVALID_DISCOUNT",
        "timestamp": "2026-06-01T15:30:00Z"
      }
    }
  ]
}
```

---

### Erro 5: Venda Vazia ao Completar

**Requisição:**

```graphql
mutation {
  completeSale(saleId: 3) {
    id
    status
  }
}
```

**Resposta (Erro 400):**

```json
{
  "errors": [
    {
      "message": "Cannot complete sale without items",
      "extensions": {
        "code": "EMPTY_SALE",
        "timestamp": "2026-06-01T15:30:00Z"
      }
    }
  ]
}
```

---

### Erro 6: Sem Autorização

**Requisição (com token de outra empresa):**

```graphql
query {
  saleById(id: 1) {
    id
  }
}
```

**Resposta (Erro 403):**

```json
{
  "errors": [
    {
      "message": "Unauthorized",
      "extensions": {
        "code": "UNAUTHORIZED",
        "timestamp": "2026-06-01T15:30:00Z"
      }
    }
  ]
}
```

---

## Fluxo Completo: Criar Venda com Itens e Completar

Este é um exemplo realista de um fluxo completo:

### Passo 1: Criar a Venda

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
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
      "total": "0.00"
    }
  }
}
```

### Passo 2: Adicionar Primeiro Item

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { addSaleItem(input: { saleId: 1, productId: 10, quantity: 5, unitPrice: 85.50 }) { id quantityItems total } }"
  }'
```

**Resposta:**

```json
{
  "data": {
    "addSaleItem": {
      "id": 1,
      "quantityItems": 5,
      "total": "427.50"
    }
  }
}
```

### Passo 3: Adicionar Segundo Item

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { addSaleItem(input: { saleId: 1, productId: 15, quantity: 2, unitPrice: 150.00 }) { id quantityItems total } }"
  }'
```

**Resposta:**

```json
{
  "data": {
    "addSaleItem": {
      "id": 1,
      "quantityItems": 7,
      "total": "727.50"
    }
  }
}
```

### Passo 4: Aplicar Desconto (Opcional)

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { applySaleDiscount(input: { saleId: 1, discountAmount: 50.00 }) { id total discount } }"
  }'
```

**Resposta:**

```json
{
  "data": {
    "applySaleDiscount": {
      "id": 1,
      "total": "677.50",
      "discount": "50.00"
    }
  }
}
```

### Passo 5: Completar Venda

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { completeSale(saleId: 1) { id status total discount } }"
  }'
```

**Resposta:**

```json
{
  "data": {
    "completeSale": {
      "id": 1,
      "status": "COMPLETED",
      "total": "677.50",
      "discount": "50.00"
    }
  }
}
```

### Passo 6: Verificar Venda Finalizada

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { saleById(id: 1) { id status total quantityItems discount items { quantity product { name } } } }"
  }'
```

**Resposta:**

```json
{
  "data": {
    "saleById": {
      "id": 1,
      "status": "COMPLETED",
      "total": "677.50",
      "quantityItems": 7,
      "discount": "50.00",
      "items": [
        {
          "quantity": 5,
          "product": {
            "name": "Extintor de Pó ABC 1kg"
          }
        },
        {
          "quantity": 2,
          "product": {
            "name": "Serviço de Inspeção Anual"
          }
        }
      ]
    }
  }
}
```

---

## Integração com Outros Módulos

### Exemplo 1: Criar Venda com Dados do Módulo Clients

Primeiro, busque clientes disponíveis:

```graphql
query GetClients {
  clients(page: 0, size: 5) {
    id
    name
    cnpj
    email
    number
  }
}
```

Depois, crie uma venda usando um cliente retornado:

```graphql
mutation CreateSaleFromClient {
  storeSale(
    input: {
      clientId: 5
      date: "2026-06-01"
      paymentMethod: CREDIT_CARD
      dueDate: "2026-07-01"
    }
  ) {
    id
    client {
      id
      name
      cnpj
    }
  }
}
```

---

### Exemplo 2: Adicionar Produtos do Módulo Products

Primeiro, liste produtos ativos:

```graphql
query GetActiveProducts {
  activeProducts(page: 0, size: 10) {
    id
    name
    type
    price
    validity
  }
}
```

Depois, adicione um produto à venda:

```graphql
mutation AddProductToSale {
  addSaleItem(
    input: { saleId: 1, productId: 10, quantity: 5, unitPrice: 85.50 }
  ) {
    id
    total
    items {
      product {
        id
        name
        type
      }
    }
  }
}
```

---

### Exemplo 3: Relatório: Total de Vendas por Cliente

Combinar múltiplas queries:

```graphql
query GetSalesReport {
  clients(page: 0, size: 100) {
    id
    name
    sales {
      id
      total
      date
      status
    }
  }
}
```

---

## Dicas de Performance

### 1. Use Paginação Sempre

❌ **Ineficiente:**

```graphql
query {
  sales {
    id
    total
  }
}
```

✅ **Eficiente:**

```graphql
query {
  sales(page: 0, size: 50) {
    id
    total
  }
}
```

### 2. Selecione Apenas Campos Necessários

❌ **Ineficiente (traz dados desnecessários):**

```graphql
query {
  saleById(id: 1) {
    id
    total
    client {
      id
      name
      cnpj
      email
      phone
      obs
      company {
        id
        name
      }
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
  }
}
```

✅ **Eficiente (traz apenas o necessário):**

```graphql
query {
  saleById(id: 1) {
    id
    total
    client {
      name
    }
    items {
      quantity
      unitPrice
      product {
        name
        type
      }
    }
  }
}
```

### 3. Cache de Queries Frequentes

Para queries que são executadas frequentemente, considere armazenar em cache:

```bash
# Cache por 5 minutos
-H "Cache-Control: max-age=300"
```

### 4. Use Índices Apropriados

Certifique-se de que os índices estão criados:

```sql
CREATE INDEX idx_sale_company ON sales(fk_id_company);
CREATE INDEX idx_sale_client ON sales(fk_id_client);
CREATE INDEX idx_sale_status ON sales(status_sale);
CREATE INDEX idx_sale_date ON sales(date_sale);
```

---

**Última Atualização**: 1º de junho de 2026  
**Versão do Documento**: 1.0  
**Status**: Produção
