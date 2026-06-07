
# Clients Module

## Objetivo

O módulo Clients é responsável pela gestão de clientes da plataforma Igniscore, incluindo cadastro, consulta, atualização e remoção lógica de registros, com isolamento multi-tenant por empresa.

## Documentação Disponível

| Documento                                  | Descrição                     |
|--------------------------------------------|-------------------------------|
| [CLIENTS_README.md](CLIENTS_README.md)     | Documentação técnica completa |
| [CLIENTS_EXAMPLES.md](CLIENTS_EXAMPLES.md) | Exemplos práticos de uso      |
| [CLIENTS_SECURITY.md](CLIENTS_SECURITY.md) | Segurança e multi-tenancy     |

---

## Guia Rápido

### Desenvolvedor Frontend

1. Consulte [CLIENTS_EXAMPLES.md](CLIENTS_EXAMPLES.md)
2. Realize autenticação JWT
3. Utilize as queries e mutations disponíveis

### Desenvolvedor Backend

1. Leia [CLIENTS_README.md](CLIENTS_README.md)
2. Analise o modelo de dados
3. Reveja as regras de negócio
4. Consulte [CLIENTS_SECURITY.md](CLIENTS_SECURITY.md)

### QA

1. Execute os exemplos disponíveis
2. Valide casos de erro
3. Teste isolamento multi-tenant

---

## Resumo do Módulo

| Aspecto            | Descrição  |
|--------------------|------------|
| Entidade Principal | Client     |
| Banco de Dados     | PostgreSQL |
| API                | GraphQL    |
| Autenticação       | JWT        |
| Multi-Tenancy      | Sim        |
| Paginação          | Sim        |
| Soft Delete        | Sim        |

---

## Fluxo Básico

### Criar Cliente

```graphql
mutation {
  storeClient(
    input: {
      name: "Acme Corp"
      cnpj: "12345678000199"
      email: "contato@acme.com"
      phone: ""
    }
  ) {
    id
    name
  }
}
````

### Consultar Clientes

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

---

## Principais Regras

* Todo cliente pertence a uma empresa.
* O acesso é isolado por tenant.
* CNPJ deve ser válido.
* E-mail deve possuir formato válido.
* Exclusão realizada por Soft Delete.

---

## Estrutura

```text
clients/
├── CLIENTS_INDEX.md
├── CLIENTS_README.md
├── CLIENTS_EXAMPLES.md
└── CLIENTS_SECURITY.md
```