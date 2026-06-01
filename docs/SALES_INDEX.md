# Documentação Completa – Módulo Sales (Igniscore)

## Índice de Documentação

Este é um índice centralizado da documentação do módulo **Sales** do projeto Igniscore. Abaixo você encontrará todas as documentações disponíveis, organizadas por tema.

---

## Documentos Disponíveis

### 1. [README Técnico – Módulo Sales](./SALES_README.md)

**Tipo**: Documentação Técnica Completa  
**Público Alvo**: Desenvolvedores, Arquitetos, Product Managers

Documentação técnica abrangente cobrindo:

- Visão geral e objetivos do módulo
- Modelo de dados (tabelas `sales` e `sale_items`, campos, constraints)
- Enums de métodos de pagamento e status de vendas
- Regras de negócio e ciclo de vida
- Arquitetura de camadas (Controller → Service → Repository)
- Operações GraphQL disponíveis (Queries e Mutations)
- Integrações com outros módulos
- Segurança e isolamento multi-tenant
- Tratamento de erros
- Configuração e deployment
- Testes

**Seções Principais**:

- [Modelo de Dados](./SALES_README.md#modelo-de-dados)
- [Enums](./SALES_README.md#enums)
- [Operações GraphQL](./SALES_README.md#operações-disponíveis-graphql)
- [Integrações](./SALES_README.md#integrações)
- [Segurança](./SALES_README.md#segurança-e-multi-tenancy)

---

### 2. [Guia Prático – Exemplos de Uso](./SALES_EXAMPLES.md)

**Tipo**: Guia de Referência com Exemplos  
**Público Alvo**: Desenvolvedores Frontend, Testers, Integradores

Exemplos práticos e prontos para usar cobrindo:

- Setup e autenticação (obtenção de JWT)
- Criação de vendas
- Adição e remoção de itens
- Aplicação de descontos
- Transições de status (completar, cancelar)
- Consultas e buscas (paginação, vendas por cliente)
- Casos de erro comuns com respostas reais
- Integração com módulos de clientes e produtos
- Dicas de performance
- Exemplos de relatórios básicos

**Seções Principais**:

- [Setup e Autenticação](./SALES_EXAMPLES.md#setup-e-autenticação)
- [Criação de Vendas](./SALES_EXAMPLES.md#criação-de-vendas)
- [Operações com Itens](./SALES_EXAMPLES.md#operações-com-itens)
- [Consultas e Buscas](./SALES_EXAMPLES.md#consultas-e-buscas)
- [Casos de Erro](./SALES_EXAMPLES.md#casos-de-erro-comuns)

---

### 3. [Guia de Segurança e Multi-Tenancy](./SALES_SECURITY.md)

**Tipo**: Documentação de Segurança  
**Público Alvo**: Arquitetos, Especialistas de Segurança, Code Reviewers

Documentação detalhada sobre segurança, isolamento de dados e conformidade:

- Arquitetura multi-tenant (database-per-schema)
- Fluxos de autenticação e autorização
- Filtragem de dados por tenant
- Validações de segurança por operação (CREATE, READ, UPDATE, DELETE)
- Tratamento seguro de erros
- Auditoria e logging
- Proteção contra ataques comuns (SQL Injection, CSRF, XSS)
- Testes de segurança
- Checklist de deploy

**Seções Principais**:

- [Arquitetura Multi-Tenant](./SALES_SECURITY.md#1-arquitetura-multi-tenant)
- [Fluxo de Autenticação](./SALES_SECURITY.md#2-fluxo-de-autenticação-e-autorização)
- [Filtragem por Tenant](./SALES_SECURITY.md#3-filtragem-de-dados-por-tenant)
- [Validações por Operação](./SALES_SECURITY.md#4-validações-de-segurança-por-operação)
- [Proteção contra Ataques](./SALES_SECURITY.md#7-proteção-contra-ataques-comuns)
- [Testes de Segurança](./SALES_SECURITY.md#8-testes-de-segurança)

---

## Guia Rápido por Caso de Uso

### Para Front-End Developer

1. Leia [Setup e Autenticação](./SALES_EXAMPLES.md#setup-e-autenticação)
2. Veja [Exemplos de Criação de Vendas](./SALES_EXAMPLES.md#criação-de-vendas)
3. Explore [Operações com Itens](./SALES_EXAMPLES.md#operações-com-itens)
4. Consulte [Casos de Erro](./SALES_EXAMPLES.md#casos-de-erro-comuns)

### Para Back-End Developer

1. Comece com [Visão Geral](./SALES_README.md#visão-geral)
2. Estude [Modelo de Dados](./SALES_README.md#modelo-de-dados)
3. Entenda os [Enums](./SALES_README.md#enums)
4. Analise [Arquitetura da Camada](./SALES_README.md#arquitetura-da-camada-de-sales)
5. Estude [Operações GraphQL](./SALES_README.md#operações-disponíveis-graphql)
6. Verifique [Segurança](./SALES_SECURITY.md)

### Para QA / Tester

1. Leia [Operações Disponíveis](./SALES_README.md#operações-disponíveis-graphql)
2. Execute [Exemplos de Teste](./SALES_EXAMPLES.md)
3. Teste [Casos de Erro](./SALES_EXAMPLES.md#casos-de-erro-comuns)
4. Valide [Isolamento Multi-Tenant](./SALES_SECURITY.md#8-testes-de-segurança)
5. Teste [Fluxo Completo](./SALES_EXAMPLES.md#fluxo-completo-criar-venda-com-itens-e-completar)

### Para Product Manager / Business Analyst

1. Leia [Visão Geral](./SALES_README.md#visão-geral)
2. Entenda [Modelo de Dados](./SALES_README.md#modelo-de-dados)
3. Conheça [Enums](./SALES_README.md#enums)
4. Explore [Regras de Negócio](./SALES_README.md#regras-de-negócio)
5. Conheça [Operações Disponíveis](./SALES_README.md#operações-disponíveis-graphql)

### Para Security Architect

1. Comece com [Arquitetura Multi-Tenant](./SALES_SECURITY.md#1-arquitetura-multi-tenant)
2. Estude [Fluxo de Autenticação](./SALES_SECURITY.md#2-fluxo-de-autenticação-e-autorização)
3. Analise [Filtragem por Tenant](./SALES_SECURITY.md#3-filtragem-de-dados-por-tenant)
4. Revise [Validações de Segurança](./SALES_SECURITY.md#4-validações-de-segurança-por-operação)
5. Estude [Testes de Segurança](./SALES_SECURITY.md#8-testes-de-segurança)
6. Acompanhe [Checklist de Deploy](./SALES_SECURITY.md#9-checklist-de-deploy)

---

## Resumo do Módulo

| Aspecto                | Descrição                                                                |
| ---------------------- | ------------------------------------------------------------------------ |
| **Tabelas Principais** | `sales`, `sale_items`                                                    |
| **Campos Principais**  | id, quantityItems, discount, total, date, paymentMethod, status, dueDate |
| **Modelo**             | Multi-tenant (1 venda : 1 empresa, 1 venda : 1+ clientes)                |
| **Isolamento**         | Por `fk_id_company`                                                      |
| **Autenticação**       | JWT Bearer Token                                                         |
| **API**                | GraphQL (queries + mutations)                                            |
| **Integrações**        | Clients, Products, Companies, Auth                                       |
| **Operações CRUD**     | Create, Read, Update, Delete (com restrições)                            |
| **Status**             | Produção                                                                 |

---

## Enums Disponíveis

### PaymentMethod (Métodos de Pagamento)

- `CASH` - Dinheiro
- `CREDIT_CARD` - Cartão de crédito
- `DEBIT_CARD` - Cartão de débito
- `BANK_TRANSFER` - Transferência bancária
- `CHECK` - Cheque
- `INSTALLMENT` - Parcelado
- `PAYMENT_PENDING` - Pendente de pagamento

### SaleStatus (Status da Venda)

- `PENDING` - Venda criada mas não finalizada
- `COMPLETED` - Venda finalizada e paga
- `CANCELED` - Venda cancelada

---

## Fluxo Rápido: Criar e Finalizar uma Venda

### 1. Autenticar

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { login(input: {email: \"admin@empresa.com\", password: \"senha\"}) { token } }"}'
```

### 2. Criar Venda

```graphql
mutation {
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
    total
  }
}
```

### 3. Adicionar Itens

```graphql
mutation {
  addSaleItem(
    input: { saleId: 1, productId: 10, quantity: 5, unitPrice: 85.50 }
  ) {
    id
    quantityItems
    total
  }
}
```

### 4. Aplicar Desconto (Opcional)

```graphql
mutation {
  applySaleDiscount(input: { saleId: 1, discountAmount: 50.00 }) {
    id
    total
    discount
  }
}
```

### 5. Completar Venda

```graphql
mutation {
  completeSale(saleId: 1) {
    id
    status
    total
  }
}
```

**Para exemplos completos, veja**: [Exemplos de Uso](./SALES_EXAMPLES.md)

---

## Operações GraphQL Disponíveis

### Queries

| Nome            | Descrição                         | Paginação |
| --------------- | --------------------------------- | --------- |
| `sales`         | Listar todas as vendas da empresa | Sim       |
| `saleById`      | Buscar uma venda por ID           | Não       |
| `salesByClient` | Listar vendas de um cliente       | Sim       |

### Mutations

| Nome                | Descrição                      | Retorno |
| ------------------- | ------------------------------ | ------- |
| `storeSale`         | Criar nova venda               | Sale    |
| `updateSale`        | Atualizar informações da venda | Sale    |
| `addSaleItem`       | Adicionar item à venda         | Sale    |
| `removeSaleItem`    | Remover item da venda          | Sale    |
| `applySaleDiscount` | Aplicar desconto à venda       | Sale    |
| `completeSale`      | Finalizar venda                | Sale    |
| `cancelSale`        | Cancelar venda                 | Sale    |

---

## Notas Importantes

### Multi-Tenancy

- CADA operação deve validar que a venda pertence à empresa autenticada
- Nunca retornar vendas de outras empresas
- Logs de auditoria são obrigatórios
- Cliente deve pertencer à mesma empresa

### Segurança

- Sempre usar JWT para autenticação
- Validações obrigatórias em todas as operações
- Mensagens de erro não devem revelar detalhes
- Produtos devem estar ativos ao adicionar à venda

### Performance

- Use paginação (máximo 100 registros por página)
- Implemente cache para queries frequentes
- Use índices em `fk_id_company`, `fk_id_client`, `status_sale`
- Considere cursor-based pagination para grandes datasets

### Validações

- Cliente deve existir e estar ativo
- Produtos devem estar ativos
- Quantidade de itens > 0
- Preço unitário > 0
- Desconto >= 0 e <= total
- Data de vencimento >= data de venda

---

## Integrações com Outros Módulos

### Módulo Clients

- Cada venda requer um cliente válido e ativo
- Clientes vêm do módulo de gestão de clientes
- Filtro: cliente deve pertencer à mesma empresa

### Módulo Products

- Itens de venda usam produtos do módulo de produtos
- Produtos devem estar ativos para serem adicionados
- Preço pode ser sobrescrito na venda

### Módulo Companies

- Cada venda pertence a uma empresa
- Deleção em cascata quando empresa é deletada
- Isolamento completo entre empresas

### Módulo Auth

- Validação JWT para todas as operações
- Contexto de empresa extraído do token
- Permissões por role (gerenciador de vendas, etc.)

---

## Checklist Inicial

Novo desenvolvedor no módulo Sales? Siga este checklist:

- [ ] Leia [Visão Geral do Módulo](./SALES_README.md#visão-geral)
- [ ] Estude o [Modelo de Dados](./SALES_README.md#modelo-de-dados)
- [ ] Entenda os [Enums](./SALES_README.md#enums)
- [ ] Explore as [Regras de Negócio](./SALES_README.md#regras-de-negócio)
- [ ] Analise a [Arquitetura de Camadas](./SALES_README.md#arquitetura-da-camada-de-sales)
- [ ] Estude as seguintes classes:
  - [ ] `Sale.java` (entity principal)
  - [ ] `SaleItem.java` (items da venda)
  - [ ] `SaleStatus.java` e `PaymentMethod.java` (enums)
  - [ ] `SaleRepository.java` (data access)
  - [ ] `SaleService.java` (business logic)
  - [ ] `SaleController.java` (API GraphQL)
- [ ] Execute os [Exemplos de Teste](./SALES_EXAMPLES.md)
- [ ] Leia a seção de [Segurança](./SALES_SECURITY.md)
- [ ] Teste [Casos de Erro](./SALES_EXAMPLES.md#casos-de-erro-comuns)
- [ ] Execute um [Fluxo Completo](./SALES_EXAMPLES.md#fluxo-completo-criar-venda-com-itens-e-completar)

---

## Histórico de Versões

| Versão | Data       | Atualizações                  |
| ------ | ---------- | ----------------------------- |
| 1.0    | 01/06/2026 | Documentação inicial completa |

---

## Contatos e Suporte

| Função        | Contato                |
| ------------- | ---------------------- |
| Desenvolvedor | dev@igniscore.com      |
| Segurança     | security@igniscore.com |
| DevOps        | ops@igniscore.com      |
| Issue Tracker | GitHub Issues          |

---

## Recursos Adicionais

### Especificações

- [GraphQL Spec](https://spec.graphql.org/)
- [Spring GraphQL](https://spring.io/projects/spring-graphql)
- [JPA/Hibernate](https://hibernate.org/)

### Segurança

- [OWASP Top 10](https://owasp.org/Top10/)
- [OWASP GraphQL Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/GraphQL_Cheat_Sheet.html)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

### Multi-Tenancy

- [Multi-Tenancy Patterns](https://www.postgresql.org/docs/current/ddl-schemas.html)
- [SaaS Architecture](https://aws.amazon.com/pt/solutions/saas-architecture/)

---

**Documentação Versão**: 1.0  
**Data**: 1º de junho de 2026  
**Status**: Completo e em Produção

Para dúvidas ou contribuições, consulte o [README técnico principal](./SALES_README.md).
