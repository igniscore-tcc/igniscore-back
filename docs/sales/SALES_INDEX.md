
# Sales Module

## Objetivo

O módulo Sales é responsável pela gestão de vendas da plataforma Igniscore, incluindo criação de vendas, consulta e remoção lógica de registros, com isolamento multi-tenant por empresa.

## Documentação Disponível

| Documento                              | Descrição                     |
|----------------------------------------|-------------------------------|
| [SALES_README.md](SALES_README.md)     | Documentação técnica completa |
| [SALES_EXAMPLES.md](SALES_EXAMPLES.md) | Exemplos práticos de uso      |
| [SALES_SECURITY.md](SALES_SECURITY.md) | Segurança e multi-tenancy     |

---

## Guia Rápido

### Desenvolvedor Frontend

1. Consulte [SALES_EXAMPLES.md](SALES_EXAMPLES.md)
2. Realize autenticação JWT
3. Utilize as queries e mutations disponíveis

### Desenvolvedor Backend

1. Leia [SALES_README.md](SALES_README.md)
2. Analise o modelo de dados
3. Reveja as regras de negócio
4. Consulte [SALES_SECURITY.md](SALES_SECURITY.md)

### QA

1. Execute os exemplos disponíveis
2. Valide casos de erro
3. Teste isolamento multi-tenant

---

## Resumo do Módulo

| Aspecto            | Descrição  |
|--------------------|------------|
| Entidade Principal | Sales      |
| Banco de Dados     | PostgreSQL |
| API                | GraphQL    |
| Autenticação       | JWT        |
| Multi-Tenancy      | Sim        |
| Paginação          | Sim        |
| Soft Delete        | Sim        |

---

## Fluxo Básico

### Criar Sales

```graphql
mutation {
  storeSale(
    input: {
      clientId: 1
      paymentMethod: PIX
      items: [
        {
          productId: 1
          quantity: 2
          unitPrice: 89.90
        },
        {
          productId: 3
          quantity: 1
          unitPrice: 250.00
        }
      ]
    }
  ) {
    id
    quantityItems
    discount
    total
    date
    dueDate
    paymentMethod
    status

    client {
      id
      name
    }

    items {
      id
      quantity
      unitPrice
      total

      product {
        id
        name
      }
    }
  }
}
````

### Consultar Sales

```graphql
query {
  sales(page: 1, size: 10) {
    sales {
      id
      total
      date
    }
    totalPages
    totalSales
  }
}
```

---

## Principais Regras

* Todas sales pertence a uma empresa.
* O acesso é isolado por tenant.

---

## Estrutura

```text
clients/
├── SALES_INDEX.md
├── SALES_README.md
├── SALES_EXAMPLES.md
└── SALES_SECURITY.md
```