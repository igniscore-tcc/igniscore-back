# Guia Prático – Exemplos de Uso do Módulo Clients

## Índice

1. Autenticação
2. Criar Cliente
3. Listar Clientes
4. Buscar Cliente por ‘ID’
5. Atualizar Cliente
6. Casos de Erro Comuns
7. Boas Práticas
8. Resumo das Operações

---

# Autenticação

Todas as operações exigem autenticação via JWT.

## Obter ‘Token’

```bash
{
  "email": "",
  "password": ""
}
````

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

## Utilizar ‘Token’

```bash
Authorization: Bearer jwt_token
```

---

# Criar Cliente

## Exemplo Completo

```graphql
mutation {
    storeClient(
        input: {
            name: "Empresa Alpha LTDA"
            cnpj: "11444777000161"
            email: "contato@alpha.com"
            phone: "11999999999"
            ie: "123456782"
            ufIe: "SP"
            obs: "Cliente com CNPJ válido"
        }
    ) {
        id
        number
        name
        cnpj
        email
        phone
    }
}
```

### Resposta

```json
{
  "data": {
    "storeClient": {
      "id": 1,
      "name": "Acme Corp",
      "cnpj": "12.345.678/0001-99",
      "email": "contato@acme.com",
      "phone": "(11) 3000-0000",
      "number": 1
    }
  }
}
```

---

# Listar Clientes

## Primeira Página

```graphql
query {
    clients(page: 0, size: 10) {
        clients {
            id
            number
            name
            email
            phone
            cpf
            cnpj
        }

        totalPages
        totalClients
    }
}
```

### Resposta

```json
{
  "data": {
    "clients": [
      {
        "id": 1,
        "name": "Acme Corp",
        "cnpj": "12.345.678/0001-99",
        "email": "contato@acme.com",
        "number": 1
      }
    ]
  }
}
```

---

# Buscar Cliente por ‘ID’

```graphql
query {
  client(id: 1) {
    id
    name
    cnpj
    email
    phone
    number
    ie
    ufIe
    cpf
    obs
  }
}
```

### Resposta

```json
{
  "data": {
    "clientById": {
      "id": 1,
      "name": "Acme Corp",
      "cnpj": "12.345.678/0001-99",
      "email": "contato@acme.com",
      "phone": "(11) 3000-0000",
      "number": 1,
      "ie": "123456789",
      "uf_ie": "SP",
      "cpf": "123.456.789-10",
      "obs": "Cliente preferencial"
    }
  }
}
```

---

# Atualizar Cliente

## Atualização Parcial

A mutation atualiza apenas os campos enviados.

```graphql
mutation {
  updateClient(input: {
    id: 1
    phone: "(11) 99999-9999"
    email: "novoemail@acme.com"
  }) {
    id
    name
    email
    phone
  }
}
```

### Resposta

```json
{
  "data": {
    "updateClient": {
      "id": 1,
      "name": "Acme Corp",
      "email": "novoemail@acme.com",
      "phone": "(11) 99999-9999"
    }
  }
}
```

---

# Casos de Erro Comuns

## CNPJ Inválido

```graphql
mutation {
  storeClient(input: {
      name: "Teste"
      cnpj: "123"
      email: "teste@email.com"
      phone: ""
  }) {
    id
  }
}
```

### Resposta

```json
{
  "errors": [
    {
      "message": "Invalid CNPJ",
      "extensions": {
        "code": "INVALID_CNPJ"
      }
    }
  ]
}
```

---

## Email Inválido

```graphql
mutation {
  storeClient(input: {
      name: "Teste"
      cnpj: "12.345.678/0001-99"
      email: "email-invalido"
      phone: ""
  }) {
    id
  }
}
```

### Resposta

```json
{
  "errors": [
    {
      "message": "Invalid email format",
      "extensions": {
        "code": "INVALID_EMAIL"
      }
    }
  ]
}
```

---

## Cliente Não Encontrado

```graphql
query {
  client(id: 99999) {
    id
  }
}
```

### Resposta

```json
{
  "errors": [
    {
      "message": "Client not found",
      "extensions": {
        "code": "CLIENT_NOT_FOUND"
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

Evite buscar todos os registros de uma vez.

```graphql
query {
    clients(page: 0, size: 10) {
        clients {
            id
            name
        }
        totalPages
        totalClients
    }
}
```

## Solicite Apenas os Campos Necessários

Prefira:

```graphql
query {
    clients(page: 0, size: 10) {
        clients {
            id
            name
        }
        totalPages
        totalClients
    }
}
```

Ao invés de solicitar todos os campos quando não forem necessários.

## Respeite o Isolamento Multi-Tenant

Todas as consultas retornam apenas clientes pertencentes à empresa do utilizador autenticado.

---

# Resumo das Operações

| Operação     | Tipo     |
|--------------|----------|
| storeClient  | Mutation |
| updateClient | Mutation |
| clients      | Query    |
| clientById   | Query    |

---

## Campos do Cliente

| Campo | Obrigatório |
|-------|-------------|
| name  | Sim         |
| cnpj  | Não         |
| email | Sim         |
| phone | Não         |
| ie    | Não         |
| uf_ie | Não         |
| cpf   | Não         |
| obs   | Não         |

---

**Módulo:** Clients
**Projeto:** Igniscore
**Tecnologia:** Spring Boot + GraphQL
**Status:** Produção

