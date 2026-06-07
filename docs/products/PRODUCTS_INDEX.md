# Products Module

## Objetivo

O módulo Products é responsável pela gestão de produtos da plataforma Igniscore, incluindo cadastro, consulta, atualização e inativação lógica de registros, com isolamento multi-tenant por empresa.

## Documentação Disponível

| Documento                                    | Descrição                     |
|----------------------------------------------|-------------------------------|
| [PRODUCTS_README.md](PRODUCTS_README.md)     | Documentação técnica completa |
| [PRODUCTS_EXAMPLES.md](PRODUCTS_EXAMPLES.md) | Exemplos práticos de uso      |
| [PRODUCTS_SECURITY.md](PRODUCTS_SECURITY.md) | Segurança e multi-tenancy     |

---

## Guia Rápido

### Desenvolvedor Frontend

1. Consulte [PRODUCTS_EXAMPLES.md](PRODUCTS_EXAMPLES.md)
2. Realize autenticação JWT
3. Utilize as queries e mutations disponíveis

### Desenvolvedor Backend

1. Leia [PRODUCTS_README.md](PRODUCTS_README.md)
2. Analise o modelo de dados
3. Reveja as regras de negócio
4. Consulte [PRODUCTS_SECURITY.md](PRODUCTS_SECURITY.md)

### QA

1. Execute os exemplos disponíveis
2. Valide casos de erro
3. Teste isolamento multi-tenant

---

## Resumo do Módulo

| Aspecto            | Descrição                  |
|--------------------|----------------------------|
| Entidade Principal | Product                    |
| Banco de Dados     | PostgreSQL                 |
| API                | GraphQL                    |
| Autenticação       | JWT                        |
| Multi-Tenancy      | Sim                        |
| Paginação          | Sim                        |
| Soft Delete        | Sim                        |
| Integrações        | Sales, Rentals e Companies |

---

## Tipos de Produtos suportados

| Tipo         | Descrição                |
|--------------|--------------------------|
| EXTINGUISHER | Extintor de incêndio     |
| SERVICE      | Serviço                  |
| CONSUMABLE   | Material consumível      |
| ACCESSORY    | Acessório                |
| HOSE         | Mangueira                |
| DETECTOR     | Detector                 |
| SPRINKLER    | Sprinkler                |
| CENTRAL      | Central de alarme        |
| LIGHTING     | Iluminação de emergência |
| DOOR         | Porta corta-fogo         |
| HYDRANT      | Hidrante                 |

---

## Operações Disponíveis

### Queries

- `products`
- `productById`
- `activeProducts`

### Mutations

- `storeProduct`
- `updateProduct`
- `deactivateProduct`

---

## Fluxo Básico

### Criar Produto

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
    price
  }
}
```

### Consultar Produtos

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

---

## Principais Regras

* Todo o produto pertence a uma empresa.
* O acesso é isolado por tenant.
* O tipo do produto deve ser válido.
* O preço não pode ser negativo.
* A exclusão é realizada por Soft Delete.
* Produtos inativos não são exibidos em consultas de ativos.

---

## Estrutura

```text
products/
├── PRODUCTS_INDEX.md
├── PRODUCTS_README.md
├── PRODUCTS_EXAMPLES.md
└── PRODUCTS_SECURITY.md
```