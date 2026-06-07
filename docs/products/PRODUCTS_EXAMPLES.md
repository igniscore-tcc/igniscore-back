# Guia Prático – Exemplos de Uso do Módulo Products

## Índice

1. Autenticação
2. Criar Produto
3. Listar Produtos
4. Buscar Produto por ID
5. Atualizar Produto
6. Inativar Produto
7. Casos de Erro Comuns
8. Boas Práticas
9. Resumo das Operações

---

# Autenticação

Todas as operações exigem autenticação via JWT.

## Obter Token

```bash
{
  "email": "",
  "password": ""
}
```

### Resposta

```json
{
  "data": {
    "login": {
      "token": "jwt_token"
    }
  }
}
```

## Utilizar Token

```bash
Authorization: Bearer jwt_token
```

---

# Criar Produto

## Exemplo Completo

```graphql
mutation {
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
  }
}
```

### Resposta

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
      "status": true
    }
  }
}
```

---

# Listar Produtos

## Primeira Página

```graphql
query {
  products(page: 0, size: 10) {
    products {
      id
      name
      type
      price
      status
    }

    totalPages
    totalProducts
  }
}
```

### Resposta

```json
{
  "data": {
    "products": [
      {
        "id": 1,
        "name": "Extintor de Pó ABC 1kg",
        "type": "EXTINGUISHER",
        "price": 85.5,
        "status": true
      }
    ]
  }
}
```

---

# Atualizar Produto

## Atualização Parcial

A mutation atualiza apenas os campos enviados.

```graphql
mutation {
  updateProduct(
    id: 1
    input: {
      price: 95.00
      validity: "2027-12-31"
    }
  ) {
    id
    name
    price
    validity
  }
}
```

### Resposta

```json
{
  "data": {
    "updateProduct": {
      "id": 1,
      "name": "Extintor de Pó ABC 1kg",
      "price": 95.0,
      "validity": "2027-12-31"
    }
  }
}
```

---

# Inativar Produto

## Soft Delete

```graphql
mutation{
    deleteProduct(
        id: 28
    ) {
        id
        name
        status
        company {
            name
            cnpj
        }
    }
}
```

### Resposta

```json
{
  "data": {
    "deleteProduct": {
      "id": 1,
      "name": "Extintor de Pó ABC 1kg",
      "status": false
    }
  }
}
```

---

# Casos de Erro Comuns

## Produto Não Encontrado

```graphql
query {
  productById(id: 99999) {
    id
  }
}
```

### Resposta

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

## Tipo de Produto Inválido

```graphql
mutation {
  storeProduct(
    input: {
      name: "Produto Teste"
      type: INVALID_TYPE
      price: 10
    }
  ) {
    id
  }
}
```

### Resposta

```json
{
  "errors": [
    {
      "message": "Invalid product type",
      "extensions": {
        "code": "INVALID_PRODUCT_TYPE"
      }
    }
  ]
}
```

---

## Preço Inválido

```graphql
mutation {
  storeProduct(
    input: {
      name: "Produto"
      type: EXTINGUISHER
      lot: "LOT001"
      validity: "2026-12-31"
      price: -10
    }
  ) {
    id
  }
}
```

### Resposta

```json
{
  "errors": [
    {
      "message": "Price must be greater than or equal to 0",
      "extensions": {
        "code": "INVALID_PRICE"
      }
    }
  ]
}
```

---

## Acesso Não Autorizado

```json
{
  "errors": [
    {
      "message": "Access denied",
      "extensions": {
        "code": "UNAUTHORIZED"
      }
    }
  ]
}
```

---

# Boas Práticas

## Utilize Paginação

Evite buscar todos os produtos de uma vez.

```graphql
query {
  products(page: 0, size: 10) {
    products {
      id
      name
    }

    totalPages
    totalProducts
  }
}
```

## Solicite Apenas os Campos Necessários

Prefira:

```graphql
query {
  products(page: 0, size: 10) {
    products {
      id
      name
      price
    }
  }
}
```

Ao invés de solicitar todos os campos disponíveis.


## Respeite o Isolamento Multi-Tenant

Todas as consultas retornam apenas produtos pertencentes à empresa do utilizador autenticado.

---

# Resumo das Operações

| Operação          | Tipo     |
| ----------------- | -------- |
| storeProduct      | Mutation |
| updateProduct     | Mutation |
| deactivateProduct | Mutation |
| products          | Query    |
| productById       | Query    |
| activeProducts    | Query    |

---

## Campos do Produto

| Campo    | Obrigatório |
| -------- | ----------- |
| name     | Sim         |
| type     | Sim         |
| lot      | Sim         |
| validity | Sim         |
| price    | Sim         |
| status   | Não         |

---

**Módulo:** Products  
**Projeto:** Igniscore  
**Tecnologia:** Spring Boot + GraphQL  
**Status:** Produção