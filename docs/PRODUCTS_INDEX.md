# Documentação Completa – Módulo Products (Igniscore)

## Índice de Documentação

Este é um índice centralizado da documentação do módulo **Products** do projeto Igniscore. Abaixo você encontrará todas as documentações disponíveis, organizadas por tema.

---

## Documentos Disponíveis

### 1. Documentação Técnica Completa (PRODUCTS_README.md)

**Tipo**: Referência Técnica  
**Público Alvo**: Desenvolvedores, Arquitetos, Product Managers

Documentação técnica abrangente cobrindo:

- Visão geral e objetivos do módulo
- Modelo de dados (tabela `products`, campos, constraints)
- Enums de tipos de produtos (ProductType)
- Regras de negócio e ciclo de vida
- Arquitetura de camadas (Controller → Service → Repository)
- Operações GraphQL disponíveis (Queries e Mutations)
- Tratamento de erros
- Performance e otimizações
- Integrações com outros módulos

**Seções Principais**:

- [Modelo de Dados](./PRODUCTS_README.md#modelo-de-dados)
- [Enum ProductType](./PRODUCTS_README.md#enum-producttype)
- [Regras de Negócio](./PRODUCTS_README.md#regras-de-negócio)
- [Operações GraphQL](./PRODUCTS_README.md#operações-disponíveis-graphql)
- [Integrações](./PRODUCTS_README.md#integrações-com-outros-módulos)
- [Segurança](./PRODUCTS_README.md#segurança-e-multi-tenancy)

---

### 2. Guia Prático com Exemplos (PRODUCTS_EXAMPLES.md)

**Tipo**: Guia de Referência com Exemplos  
**Público Alvo**: Desenvolvedores Frontend, Testers, Integradores

Exemplos práticos e prontos para usar cobrindo:

- Setup e autenticação (obtenção de JWT)
- Criação de produtos (extintor, serviço, sprinkler, etc)
- Consultas e buscas (paginação, busca específica, filtros ativos)
- Atualizações parciais (campo único, múltiplos campos)
- Inativação e reativação de produtos
- Casos de erro comuns com respostas reais
- Integração com módulos de vendas e locações
- Dicas de performance

**Seções Principais**:

- [Exemplos de Criação](./PRODUCTS_EXAMPLES.md#criação-de-produtos)
- [Exemplos de Consulta](./PRODUCTS_EXAMPLES.md#consultas-e-buscas)
- [Exemplos de Atualização](./PRODUCTS_EXAMPLES.md#atualizações)
- [Casos de Erro](./PRODUCTS_EXAMPLES.md#casos-de-erro-comuns)
- [Integração com Vendas e Locações](./PRODUCTS_EXAMPLES.md#integração-com-outros-módulos)

---

### 3. Guia de Segurança e Multi-Tenancy (PRODUCTS_SECURITY.md)

**Tipo**: Documentação de Segurança  
**Público Alvo**: Arquitetos, Especialistas de Segurança, Code Reviewers, DevOps

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

- [Arquitetura Multi-Tenant](./PRODUCTS_SECURITY.md#1-arquitetura-multi-tenant)
- [Autenticação e Autorização](./PRODUCTS_SECURITY.md#2-fluxo-de-autenticação-e-autorização)
- [Filtragem por Tenant](./PRODUCTS_SECURITY.md#3-filtragem-de-dados-por-tenant)
- [Validações por Operação](./PRODUCTS_SECURITY.md#4-validações-de-segurança-por-operação)
- [Proteção contra Ataques](./PRODUCTS_SECURITY.md#7-proteção-contra-ataques-comuns)
- [Testes de Segurança](./PRODUCTS_SECURITY.md#8-testes-de-segurança)
- [Checklist de Deploy](./PRODUCTS_SECURITY.md#9-checklist-de-deploy)

---

## Guia Rápido por Caso de Uso

### Para Front-End Developer

1. Leia [Setup e Autenticação](./PRODUCTS_EXAMPLES.md#setup-e-autenticação)
2. Veja [Exemplos de Criação](./PRODUCTS_EXAMPLES.md#criação-de-produtos)
3. Consulte [Casos de Erro](./PRODUCTS_EXAMPLES.md#casos-de-erro-comuns)
4. Estude [Integração com Vendas](./PRODUCTS_EXAMPLES.md#integração-com-vendas-sales)

### Para Back-End Developer

1. Comece com [Visão Geral](./PRODUCTS_README.md#visão-geral)
2. Estude [Modelo de Dados](./PRODUCTS_README.md#modelo-de-dados)
3. Analise [Arquitetura da Camada](./PRODUCTS_README.md#arquitetura-da-camada-de-products)
4. Entenda [Operações GraphQL](./PRODUCTS_README.md#operações-disponíveis-graphql)
5. Verifique [Segurança](./PRODUCTS_SECURITY.md)

### Para QA / Tester

1. Leia [Operações Disponíveis](./PRODUCTS_README.md#operações-disponíveis-graphql)
2. Execute [Exemplos de Teste](./PRODUCTS_EXAMPLES.md)
3. Teste [Casos de Erro](./PRODUCTS_EXAMPLES.md#casos-de-erro-comuns)
4. Valide [Isolamento Multi-Tenant](./PRODUCTS_SECURITY.md#8-testes-de-segurança)

### Para Product Manager / Business Analyst

1. Leia [Visão Geral](./PRODUCTS_README.md#visão-geral)
2. Entenda [Modelo de Dados](./PRODUCTS_README.md#modelo-de-dados)
3. Conheça [Enum ProductType](./PRODUCTS_README.md#enum-producttype)
4. Explore [Regras de Negócio](./PRODUCTS_README.md#regras-de-negócio)
5. Conheça [Operações Disponíveis](./PRODUCTS_README.md#operações-disponíveis-graphql)

### Para Security Architect

1. Comece com [Arquitetura Multi-Tenant](./PRODUCTS_SECURITY.md#1-arquitetura-multi-tenant)
2. Estude [Fluxo de Autenticação](./PRODUCTS_SECURITY.md#2-fluxo-de-autenticação-e-autorização)
3. Analise [Filtragem por Tenant](./PRODUCTS_SECURITY.md#3-filtragem-de-dados-por-tenant)
4. Revise [Validações de Segurança](./PRODUCTS_SECURITY.md#4-validações-de-segurança-por-operação)
5. Estude [Testes de Segurança](./PRODUCTS_SECURITY.md#8-testes-de-segurança)
6. Acompanhe [Checklist de Deploy](./PRODUCTS_SECURITY.md#9-checklist-de-deploy)

---

## Tipos de Produtos Suportados

| Tipo         | Descrição                 | Categoria   |
| ------------ | ------------------------- | ----------- |
| EXTINGUISHER | Extintor de incêndio      | Equipamento |
| SERVICE      | Serviços de manutenção    | Serviço     |
| CONSUMABLE   | Materiais consumíveis     | Consumível  |
| ACCESSORY    | Acessórios e complementos | Acessório   |
| HOSE         | Mangueiras especializadas | Equipamento |
| DETECTOR     | Detectores de fumaça      | Equipamento |
| SPRINKLER    | Sistemas automáticos      | Equipamento |
| CENTRAL      | Central de alarme         | Equipamento |
| LIGHTING     | Iluminação de emergência  | Equipamento |
| DOOR         | Portas corta-fogo         | Equipamento |
| HYDRANT      | Hidrantes e conexões      | Equipamento |

---

## Operações GraphQL Disponíveis

### Queries

| Nome             | Descrição                           | Paginação |
| ---------------- | ----------------------------------- | --------- |
| `products`       | Listar todos os produtos da empresa | Sim       |
| `productById`    | Buscar um produto por ID            | Não       |
| `activeProducts` | Listar apenas produtos ativos       | Sim       |

### Mutations

| Nome                | Descrição                   | Retorno |
| ------------------- | --------------------------- | ------- |
| `storeProduct`      | Criar novo produto          | Product |
| `updateProduct`     | Atualizar product existente | Product |
| `deactivateProduct` | Inativar um produto         | Product |

---

## Resumo do Módulo

| Aspecto                 | Detalhes                                     |
| ----------------------- | -------------------------------------------- |
| **Tabela Principal**    | `products`                                   |
| **Campos Principais**   | id, name, type, lot, validity, price, status |
| **Modelo**              | Multi-tenant (1 produto : 1 empresa)         |
| **Isolamento**          | Por `fk_id_company`                          |
| **Autenticação**        | JWT Bearer Token                             |
| **API**                 | GraphQL (queries + mutations)                |
| **Integrações**         | Sales, Rentals, Companies                    |
| **Operações CRUD**      | Create, Read, Update, Soft Delete            |
| **Status**              | Ativo em produção                            |
| **Versão Documentação** | 1.0 - 26/04/2026                             |

---

## Fluxo Rápido: Criar um Produto

### 1. Autenticar

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { login(input: {email: \"admin@empresa.com\", password: \"senha\"}) { token } }"}'
```

### 2. Criar Produto

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

### 3. Listar Produtos Ativos

```graphql
query {
  activeProducts(page: 0, size: 20) {
    id
    name
    type
    price
  }
}
```

Veja exemplos completos em: [Exemplos de Uso](./PRODUCTS_EXAMPLES.md)

---

## Características Principais

- Isolamento multi-tenant completo
- Soft delete (inativação lógica)
- Validações de negócio robustas
- Tipos de produtos predefinidos
- Integração com vendas e locações
- Paginação eficiente
- Auditoria de operações

---

## Notas Importantes

### Multi-Tenancy

- Cada operação valida automaticamente propriedade do produto
- Nunca retorna produtos de outras empresas
- Logs de acesso são auditados

### Segurança

- Sempre usar JWT para autenticação
- Produtos podem ser inativados sem restrições
- Validação obrigatória de datas e tipos de produtos
- Preço pode ser zero (produtos promocionais)

### Operação

- Produtos inativos não aparecem em listagens padrão
- Histórico de produtos é mantido (soft delete)
- Pode ser reativado alterando o status
- Tipos de produtos são fixos (enum)

---

## Histórico de Versões

| Versão | Data       | Atualizações                  |
| ------ | ---------- | ----------------------------- |
| 1.0    | 26/04/2026 | Documentação inicial completa |

---

## Contatos e Suporte

| Função                | Contato                |
| --------------------- | ---------------------- |
| Desenvolvedor Backend | dev@igniscore.com      |
| Segurança             | security@igniscore.com |
| DevOps                | ops@igniscore.com      |
| Issue Tracker         | GitHub Issues          |

---
