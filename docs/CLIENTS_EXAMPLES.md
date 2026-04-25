# Guia Prático – Exemplos de Uso do Módulo Clients

## Índice
1. [Setup e Autenticação](#setup-e-autenticação)
2. [Criação de Clientes](#criação-de-clientes)
3. [Consultas e Buscas](#consultas-e-buscas)
4. [Atualizações](#atualizações)
5. [Deleção e Inativação](#deleção-e-inativação)
6. [Casos de Erro Comuns](#casos-de-erro-comuns)
7. [Integração com Outros Módulos](#integração-com-outros-módulos)

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

## Criação de Clientes

### Exemplo 1: Criar Cliente Simples

**Req uisição:**
```graphql
mutation CreateSimpleClient {
  storeClient(input: {
    name: "João Silva Serviços"
    cnpj: "12.345.678/0001-99"
    email: "joao@servicos.com.br"
    phone: "(11) 3000-0000"
  }) {
    id
    name
    cnpj
    email
    phone
    number
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
    "storeClient": {
      "id": 1,
      "name": "João Silva Serviços",
      "cnpj": "12.345.678/0001-99",
      "email": "joao@servicos.com.br",
      "phone": "(11) 3000-0000",
      "number": 1,
      "company": {
        "id": 5,
        "name": "Acme Corporation"
      }
    }
  }
}
```

---

### Exemplo 2: Criar Cliente com Dados Completos

**Requisição:**
```graphql
mutation CreateFullClient {
  storeClient(input: {
    name: "Construtora Alfa Ltda."
    cnpj: "98.765.432/0001-11"
    email: "contato@construtoraalfa.com.br"
    phone: "(21) 2500-1234"
    ie: "123.456.789.012"
    uf_ie: "RJ"
    cpf: "123.456.789-10"
    obs: "Cliente desde 2020. Desconto corporativo 10%. Faturamento mensal ~R$ 50k"
  }) {
    id
    name
    cnpj
    email
    phone
    ie
    uf_ie
    cpf
    obs
    number
    createdAt
  }
}
```

**Resposta:**
```json
{
  "data": {
    "storeClient": {
      "id": 2,
      "name": "Construtora Alfa Ltda.",
      "cnpj": "98.765.432/0001-11",
      "email": "contato@construtoraalfa.com.br",
      "phone": "(21) 2500-1234",
      "ie": "123.456.789.012",
      "uf_ie": "RJ",
      "cpf": "123.456.789-10",
      "obs": "Cliente desde 2020. Desconto corporativo 10%. Faturamento mensal ~R$ 50k",
      "number": 2,
      "createdAt": "2026-04-25T10:30:00Z"
    }
  }
}
```

---

### Exemplo 3: Criar Múltiplos Clientes em Sequência

**Script Python para criar 5 clientes:**

```python
import requests
import json

URL = "http://localhost:8080/graphql"
TOKEN = "seu_token_jwt_aqui"
HEADERS = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {TOKEN}"
}

clients_data = [
    {
        "name": "Cliente A Ltda.",
        "cnpj": "11.111.111/0001-11",
        "email": "contato@clientea.com"
    },
    {
        "name": "Cliente B Ltda.",
        "cnpj": "22.222.222/0002-22",
        "email": "contato@clienteb.com"
    },
    {
        "name": "Cliente C Ltda.",
        "cnpj": "33.333.333/0003-33",
        "email": "contato@clientec.com"
    }
]

for client in clients_data:
    query = f"""
    mutation {{
      storeClient(input: {{
        name: "{client['name']}"
        cnpj: "{client['cnpj']}"
        email: "{client['email']}"
      }}) {{
        id
        name
        cnpj
      }}
    }}
    """
    
    response = requests.post(
        URL,
        json={"query": query},
        headers=HEADERS
    )
    
    if response.status_code == 200:
        print(f"✓ {client['name']} criado com sucesso")
    else:
        print(f"✗ Erro ao criar {client['name']}: {response.json()}")
```

---

## Consultas e Buscas

### Exemplo 1: Listar Todos os Clientes (Primeira Página)

**Requisição:**
```graphql
query ListClients {
  clients(page: 0, size: 10) {
    id
    name
    cnpj
    email
    number
  }
}
```

**Resposta:**
```json
{
  "data": {
    "clients": [
      {
        "id": 1,
        "name": "João Silva Serviços",
        "cnpj": "12.345.678/0001-99",
        "email": "joao@servicos.com.br",
        "number": 1
      },
      {
        "id": 2,
        "name": "Construtora Alfa Ltda.",
        "cnpj": "98.765.432/0001-11",
        "email": "contato@construtoraalfa.com.br",
        "number": 2
      }
    ]
  }
}
```

---

### Exemplo 2: Buscar Cliente Específico por ID

**Requisição:**
```graphql
query GetClientDetails {
  clientById(id: 1) {
    id
    name
    cnpj
    email
    phone
    ie
    uf_ie
    cpf
    obs
    number
    createdAt
    updatedAt
    company {
      id
      name
      cnpj
    }
  }
}
```

**Resposta:**
```json
{
  "data": {
    "clientById": {
      "id": 1,
      "name": "João Silva Serviços",
      "cnpj": "12.345.678/0001-99",
      "email": "joao@servicos.com.br",
      "phone": "(11) 3000-0000",
      "ie": null,
      "uf_ie": null,
      "cpf": null,
      "obs": null,
      "number": 1,
      "createdAt": "2026-04-23T08:15:00Z",
      "updatedAt": "2026-04-23T08:15:00Z",
      "company": {
        "id": 5,
        "name": "Acme Corporation",
        "cnpj": "00.000.000/0000-00"
      }
    }
  }
}
```

---

### Exemplo 3: Listar Clientes com Paginação Avançada

**Requisição (Página 2, 20 registros por página):**
```graphql
query PaginationExample {
  clients(page: 2, size: 20) {
    id
    name
    cnpj
    number
  }
}
```

**Resposta:**
```json
{
  "data": {
    "clients": [
      {
        "id": 21,
        "name": "Client 21",
        "cnpj": "21.111.111/0001-11",
        "number": 21
      },
      // ... mais 19 registros
    ]
  }
}
```

---

## Atualizações

### Exemplo 1: Atualizar Telefone do Cliente

**Requisição:**
```graphql
mutation UpdatePhone {
  updateClient(input: {
    id: 1
    phone: "(11) 9999-9999"
  }) {
    id
    name
    phone
    updatedAt
  }
}
```

**Resposta:**
```json
{
  "data": {
    "updateClient": {
      "id": 1,
      "name": "João Silva Serviços",
      "phone": "(11) 9999-9999",
      "updatedAt": "2026-04-25T14:30:00Z"
    }
  }
}
```

---

### Exemplo 2: Atualizar Email e Observações

**Requisição:**
```graphql
mutation UpdateContactInfo {
  updateClient(input: {
    id: 2
    email: "novo-contato@construtoraalfa.com.br"
    obs: "Cliente desde 2020. Desconto corporativo 15% (atualizado). Faturamento mensal ~R$ 75k"
  }) {
    id
    name
    email
    obs
    updatedAt
  }
}
```

**Resposta:**
```json
{
  "data": {
    "updateClient": {
      "id": 2,
      "name": "Construtora Alfa Ltda.",
      "email": "novo-contato@construtoraalfa.com.br",
      "obs": "Cliente desde 2020. Desconto corporativo 15% (atualizado). Faturamento mensal ~R$ 75k",
      "updatedAt": "2026-04-25T14:35:00Z"
    }
  }
}
```

---

### Exemplo 3: Atualização Por Lote (Estrutura de Cliente)

```json
// Dados para atualizar múltiplos clientes
[
  {
    "id": 1,
    "obs": "Nova categoria: Premium"
  },
  {
    "id": 2,
    "obs": "Nova categoria: Premium"
  },
  {
    "id": 3,
    "phone": "(11) 8888-8888"
  }
]
```

**Script para processar atualizações em lote:**

```python
import requests

URL = "http://localhost:8080/graphql"
TOKEN = "seu_token_jwt_aqui"
HEADERS = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {TOKEN}"
}

updates = [
    {"id": 1, "obs": "Nova categoria: Premium"},
    {"id": 2, "obs": "Nova categoria: Premium"},
    {"id": 3, "phone": "(11) 8888-8888"}
]

for update in updates:
    query = f"""
    mutation {{
      updateClient(input: {{
        id: {update['id']}
        {', '.join([f'{k}: "{v}"' for k, v in update.items() if k != 'id'])}
      }}) {{
        id
        name
      }}
    }}
    """
    
    response = requests.post(URL, json={"query": query}, headers=HEADERS)
    print(response.json())
```

---

## Deleção e Inativação

### Exemplo 1: Inativar Cliente (Soft Delete)

**Requisição:**
```graphql
mutation DeactivateClient {
  deactivateClient(id: 3) {
    id
    name
    isActive
    deactivatedAt
  }
}
```

**Resposta:**
```json
{
  "data": {
    "deactivateClient": {
      "id": 3,
      "name": "Cliente C Ltda.",
      "isActive": false,
      "deactivatedAt": "2026-04-25T15:00:00Z"
    }
  }
}
```

---

### Exemplo 2: Verificar Clientes Inativos

**Requisição:**
```graphql
query ListInactiveClients {
  inactiveClients(page: 0, size: 10) {
    id
    name
    cnpj
    isActive
    deactivatedAt
  }
}
```

---

## Casos de Erro Comuns

### Erro 1: CNPJ Inválido

**Requisição:**
```graphql
mutation {
  storeClient(input: {
    name: "Teste"
    cnpj: "12.345.678/0001-99"  # CNPJ com dígitos verificadores inválidos
    email: "teste@example.com"
  }) {
    id
  }
}
```

**Resposta de Erro (400):**
```json
{
  "errors": [
    {
      "message": "Invalid CNPJ. Check digits do not match.",
      "extensions": {
        "code": "INVALID_CNPJ",
        "classification": "BadUserInput",
        "timestamp": "2026-04-25T10:30:00Z"
      }
    }
  ]
}
```

---

### Erro 2: CNPJ Duplicado na Mesma Empresa

**Requisição:**
```graphql
mutation {
  storeClient(input: {
    name: "Outro Cliente"
    cnpj: "12.345.678/0001-99"  # CNPJ já existe nesta empresa
    email: "outro@example.com"
  }) {
    id
  }
}
```

**Resposta de Erro (409):**
```json
{
  "errors": [
    {
      "message": "Client with CNPJ 12.345.678/0001-99 already exists in this company",
      "extensions": {
        "code": "DUPLICATE_CNPJ",
        "classification": "DataIntegrityViolation"
      }
    }
  ]
}
```

---

### Erro 3: Usuário Tenta Acessar Cliente de Outra Empresa

**Requisição:**
```graphql
query {
  clientById(id: 999) {  # ID que pertence a outra empresa
    name
  }
}
```

**Resposta de Erro (403):**
```json
{
  "errors": [
    {
      "message": "You do not have permission to access this client",
      "extensions": {
        "code": "UNAUTHORIZED",
        "classification": "Forbidden"
      }
    }
  ]
}
```

---

### Erro 4: Email Inválido

**Requisição:**
```graphql
mutation {
  storeClient(input: {
    name: "Teste"
    cnpj: "11.111.111/0001-11"
    email: "email-invalido"  # Formato de email inválido
  }) {
    id
  }
}
```

**Resposta de Erro (400):**
```json
{
  "errors": [
    {
      "message": "Invalid email format",
      "extensions": {
        "code": "INVALID_EMAIL",
        "classification": "BadUserInput"
      }
    }
  ]
}
```

---

### Erro 5: Cliente Não Encontrado

**Requisição:**
```graphql
query {
  clientById(id: 99999) {
    name
  }
}
```

**Resposta de Erro (404):**
```json
{
  "errors": [
    {
      "message": "Client with ID 99999 not found",
      "extensions": {
        "code": "NOT_FOUND",
        "classification": "NotFound"
      }
    }
  ]
}
```

---

## Integração com Outros Módulos

### Exemplo 1: Consultar Vendas de um Cliente

```graphql
query ClientWithSales {
  clientById(id: 1) {
    id
    name
    sales {
      id
      totalAmount
      saleDate
      status
    }
  }
}
```

---

### Exemplo 2: Listar Contatos Associados a um Cliente

```graphql
query ClientWithContacts {
  clientById(id: 1) {
    id
    name
    contacts {
      id
      name
      email
      phone
      role
    }
  }
}
```

---

### Exemplo 3: Associar Novo Contato a um Cliente

```graphql
mutation AddContactToClient {
  addContact(input: {
    clientId: 1
    name: "Maria Silva"
    email: "maria@example.com"
    phone: "(11) 9999-8888"
    role: "Finance Manager"
  }) {
    id
    name
    contacts {
      id
      name
      email
    }
  }
}
```

---

### Exemplo 4: Listar Endereços de um Cliente

```graphql
query ClientAddresses {
  clientById(id: 1) {
    id
    name
    addresses {
      id
      type
      street
      number
      neighborhood
      city
      state
      zipCode
    }
  }
}
```

---

### Exemplo 5: Adicionar Endereço a um Cliente

```graphql
mutation AddAddressToClient {
  addAddress(input: {
    clientId: 1
    type: "COMMERCIAL"
    street: "Avenida Paulista"
    number: "1000"
    neighborhood: "Bela Vista"
    city: "São Paulo"
    state: "SP"
    zipCode: "01311-100"
  }) {
    id
    name
    addresses {
      type
      street
      city
    }
  }
}
```

---

## Dicas de Performance

### 1. Use Paginação
Sempre solicite apenas os campos necessários e use paginação:

```graphql
query OptimizedQuery {
  clients(page: 0, size: 20) {
    id
    name
    cnpj
    # Não solicite campos desnecessários
  }
}
```

### 2. Cache de Resultados
Implemente cache no lado do cliente para queries frequentes:

```python
from functools import lru_cache

@lru_cache(maxsize=100)
def get_client(client_id):
    # Função com cache
    pass
```

### 3. Agregação de Queries (Query Batching)
Se precisar de múltiplos clientes, use uma única requisição:

```graphql
query GetMultipleClients {
  client1: clientById(id: 1) { id name }
  client2: clientById(id: 2) { id name }
  client3: clientById(id: 3) { id name }
}
```

---

## Resumo de Operações

| Operação | Tipo | Endpoint | Autenticação | Paginação |
|----------|------|----------|--------------|-----------|
| Criar cliente | Mutation | `storeClient` | ✅ Obrigatória | ❌ N/A |
| Atualizar cliente | Mutation | `updateClient` | ✅ Obrigatória | ❌ N/A |
| Inativar cliente | Mutation | `deactivateClient` | ✅ Obrigatória | ❌ N/A |
| Listar clientes | Query | `clients` | ✅ Obrigatória | ✅ Sim |
| Buscar por ID | Query | `clientById` | ✅ Obrigatória | ❌ N/A |

---

**Última Atualização**: 25 de abril de 2026  
**Versão**: 1.0
