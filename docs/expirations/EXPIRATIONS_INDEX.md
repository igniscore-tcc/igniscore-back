# Expirations Module

## Objetivo

O módulo Expirations é responsável pelos vencimentos de acordo com as vendas da plataforma Igniscore, incluindo consultas de vencimento com isolamento multi-tenant por empresa.

## Documentação Disponível

| Documento                                          | Descrição                     |
|----------------------------------------------------|-------------------------------|
| [EXPIRATIONS_README.md](EXPIRATIONS_README.md)     | Documentação técnica completa |
| [EXPIRATIONS_EXAMPLES.md](EXPIRATIONS_EXAMPLES.md) | Exemplos práticos de uso      |
| [EXPIRATIONS_SECURITY.md](EXPIRATIONS_SECURITY.md) | Segurança e multi-tenancy     |

---

## Guia Rápido

### Desenvolvedor Frontend

1. Consulte [EXPIRATIONS_EXAMPLES.md](EXPIRATIONS_EXAMPLES.md)
2. Realize autenticação JWT
3. Utilize as queries e mutations disponíveis

### Desenvolvedor Backend

1. Leia [EXPIRATIONS_README.md](EXPIRATIONS_README.md)
2. Analise o modelo de dados
3. Reveja as regras de negócio
4. Consulte [EXPIRATIONS_SECURITY.md](EXPIRATIONS_SECURITY.md)

### QA

1. Execute os exemplos disponíveis
2. Valide casos de erro
3. Teste isolamento multi-tenant

---

## Resumo do Módulo

| Aspecto            | Descrição  |
|--------------------|------------|
| Entidade Principal | Expiration |
| Banco de Dados     | PostgreSQL |
| API                | GraphQL    |
| Autenticação       | JWT        |
| Multi-Tenancy      | Sim        |
| Paginação          | Não        |
| Soft Delete        | Não        |
| Integrações        | Sales      |

---

## Tipos de Expirations suportados

| Tipo    | Descrição          |
|---------|--------------------|
| EXPIRED | Vencido            |
| NEXT    | Vencimento próximo |
| NORMAL  | Vencimento normal  |

---

## Operações Disponíveis

### Queries

- `expirations`
- `expirationsByPeriod`
- `upcomingExpirations`
- `expirationsByClient`

---

## Fluxo Básico

### Listar Vencimentos

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

### Listar Vencimentos por período

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

---

## Principais Regras

* Todo o vencimento pertence a uma empresa.
* O acesso é isolado por tenant.
* O status do vencimento deve ser válido.

---

## Estrutura

```text
products/
├── EXPIRATIONS_INDEX.md
├── EXPIRATIONS_README.md
├── EXPIRATIONS_EXAMPLES.md
└── EXPIRATIONS_SECURITY.md
```