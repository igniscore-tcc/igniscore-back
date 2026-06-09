# Guia Prático – Exemplos de Uso do Módulo Expirations

## Índice

1. Autenticação
2. Listar Vencimentos
3. Listar Vencimentos por período
4. Listar Vencimentos nos próximos dias
5. Listar Vencimentos por cliente
6. Boas Práticas
7. Resumo das Operações

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

# Listar Vencimentos

## Exemplo Completo

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

### Resposta

```json
{
  "data": {
    "expirations": [
      {
        "saleId": 30,
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      },
      {
        "saleId": 31,
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      },
      {
        "saleId": 29,
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NEXT"
      }
    ]
  }
}
```

---

# Listar Vencimentos por período

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

### Resposta

```json
{
  "data": {
    "expirationsByPeriod": [
      {
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      },
      {
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      },
      {
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      }
    ]
  }
}
```

---

# Listar Vencimentos nos próximos dias

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

### Resposta

```json
{
  "data": {
    "upcomingExpirations": [
      {
        "clientName": "Diogo e André Casa Noturna ME",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      },
      {
        "clientName": "Diogo e André Casa Noturna ME",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      },
      {
        "clientName": "Diogo e André Casa Noturna ME",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      }
    ]
  }
}
```

---

# Listar Vencimentos por cliente

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

### Resposta

```json
{
  "data": {
    "expirationsByClient": [
      {
        "saleId": 30,
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      },
      {
        "saleId": 31,
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NORMAL"
      },
      {
        "saleId": 29,
        "clientName": "Diogo e André Casa Noturna ME",
        "saleDate": "2026-06-08",
        "dueDate": "2027-06-08",
        "totalSale": 429.80,
        "status": "NEXT"
      }
    ]
  }
}
```

---

# Boas Práticas

## Respeite o Isolamento Multi-Tenant

Todas as consultas retornam apenas vencimentos pertencentes à empresa do utilizador autenticado.

---

# Resumo das Operações

| Operação            | Tipo  |
|---------------------|-------|
| expirations         | Query |
| expirationsByPeriod | Query |
| upcomingExpirations | Query |
| expirationsByClient | Query |

---

**Módulo:** Expirations  
**Projeto:** Igniscore  
**Tecnologia:** Spring Boot + GraphQL  
**Status:** Produção