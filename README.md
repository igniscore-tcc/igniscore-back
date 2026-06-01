# Igniscore - API GraphQL

Plataforma completa de gestão de vendas com suporte a multi-tenancy, autenticação JWT e operações GraphQL. Gerencia clientes, produtos e vendas com isolamento total por empresa.

## Visão Geral

O **Igniscore** é uma aplicação Spring Boot que fornece uma API GraphQL robusta para gerenciar:

- **Clients**: Gestão de clientes com isolamento por empresa (tenant)
- **Companies**: Gerenciamento de empresas contratantes
- **Products**: Catálogo de produtos com tipos e categorias
- **Sales**: Gerenciamento de vendas, itens e descontos
- **Users**: Autenticação e autorização com JWT
- **Authentication**: Sistema seguro de login e controle de acesso

## Documentação Modular

Toda a documentação está organizada em `/docs/` com índices e exemplos para cada módulo:

### 📄 Documentação do Módulo Clientes

| Documento                                                     | Descrição                                   | Público            |
| ------------------------------------------------------------- | ------------------------------------------- | ------------------ |
| [CLIENTS_INDEX.md](docs/CLIENTS_INDEX.md)                     | Índice centralizado ⭐ Comece aqui         | Todos              |
| [CLIENTS_README.md](docs/CLIENTS_README.md)                   | Documentação técnica completa               | Devs, Arquitetos   |
| [CLIENTS_EXAMPLES.md](docs/CLIENTS_EXAMPLES.md)               | Exemplos práticos prontos para usar         | Frontend, Testers  |
| [CLIENTS_SECURITY.md](docs/CLIENTS_SECURITY.md)               | Segurança e multi-tenancy                   | Arquitetos, DevOps |

### 📄 Documentação do Módulo Products

| Documento                                         | Descrição                              | Público            |
| ------------------------------------------------- | -------------------------------------- | ------------------ |
| [PRODUCTS_INDEX.md](docs/PRODUCTS_INDEX.md)       | Índice centralizado ⭐ Comece aqui     | Todos              |
| [PRODUCTS_README.md](docs/PRODUCTS_README.md)     | Documentação técnica completa          | Devs, Arquitetos   |
| [PRODUCTS_EXAMPLES.md](docs/PRODUCTS_EXAMPLES.md) | Exemplos práticos prontos para usar    | Frontend, Testers  |
| [PRODUCTS_SECURITY.md](docs/PRODUCTS_SECURITY.md) | Segurança e multi-tenancy              | Arquitetos, DevOps |

### 📄 Documentação do Módulo Sales (Vendas)

| Documento                                         | Descrição                              | Público            |
| ------------------------------------------------- | -------------------------------------- | ------------------ |
| [SALES_INDEX.md](docs/SALES_INDEX.md)             | Índice centralizado ⭐ Comece aqui     | Todos              |
| [SALES_README.md](docs/SALES_README.md)           | Documentação técnica completa          | Devs, Arquitetos   |
| [SALES_EXAMPLES.md](docs/SALES_EXAMPLES.md)       | Exemplos práticos prontos para usar    | Frontend, Testers  |
| [SALES_SECURITY.md](docs/SALES_SECURITY.md)       | Segurança e multi-tenancy              | Arquitetos, DevOps |

### 📋 Visão Geral dos Módulos

- [MODULES_OVERVIEW.md](docs/MODULES_OVERVIEW.md) - Comparação e integração entre os 3 módulos

## Início Rápido

### 1. Autenticação (Obter JWT Token)

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { login(input: { email: \"admin@empresa.com\", password: \"senha123\" }) { token } }"
  }'
```

### 2. Criar Cliente

```graphql
mutation {
  storeClient(
    input: {
      name: "Acme Corp"
      cnpj: "12.345.678/0001-99"
      email: "contato@acme.com"
      phone: "(11) 3000-0000"
    }
  ) {
    id
    name
    cnpj
    number
    company {
      id
      name
    }
  }
}
```

### 3. Criar Produto

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

### 4. Criar Venda

```graphql
mutation {
  storeSale(input: {
    clientId: 5
    date: "2026-06-01"
    paymentMethod: CREDIT_CARD
    dueDate: "2026-07-01"
  }) {
    id
    status
    total
  }
}
```

### 5. Adicionar Item à Venda

```graphql
mutation {
  addSaleItem(input: {
    saleId: 1
    productId: 10
    quantity: 5
    unitPrice: 85.50
  }) {
    id
    quantityItems
    total
  }
}
```

### 6. Listar Vendas (Paginado)

```graphql
query {
  sales(page: 0, size: 10) {
    id
    total
    date
    status
    client {
      name
    }
  }
}
```

## Arquitetura

```
api/
├── config/          # Configurações (JWT, GraphQL, Segurança)
├── controller/      # Resolvers GraphQL (ClientController, etc)
├── service/         # Lógica de negócio
├── repository/      # Acesso a dados
├── model/           # Entidades JPA
├── dto/             # Data Transfer Objects
└── utils/           # Utilitários (Auth, Validação)
```

## Características Principais

- **Multi-Tenancy**: Isolamento completo de dados por empresa
- **GraphQL API**: Queries e Mutations tipadas e documentadas
- **Autenticação JWT**: Token-based com expiração configurável
- **Autorização**: Controle de acesso baseado em role (ADMIN, USER)
- **Validações**: CNPJ, Email, CPF, IE com regras de negócio
- **Paginação**: Suporte a listagens com offset/limit
- **Tratamento de Erros**: Mensagens seguras e estruturadas
- **Auditoria**: Logs de operações críticas
- **Catálogo de Produtos**: Tipos predefinidos (extintor, sprinkler, etc)
- **Soft Delete**: Inativação lógica sem exclusão física
- **Gerenciamento de Vendas**: Criar vendas, adicionar itens, aplicar descontos
- **Transações**: Múltiplos status de venda (PENDING, COMPLETED, CANCELED)

## Segurança

Consulte as documentações de segurança para detalhes completos:

### Clientes

[CLIENTS_SECURITY.md](docs/CLIENTS_SECURITY.md) para detalhes sobre:

- Arquitetura multi-tenant
- Fluxos de autenticação e autorização
- Proteção contra SQL Injection, CSRF, XSS
- Validações por operação
- Testes de segurança

### Products

[PRODUCTS_SECURITY.md](docs/PRODUCTS_SECURITY.md) para detalhes sobre:

- Arquitetura multi-tenant
- Fluxos de autenticação e autorização
- Filtragem de dados por tenant
- Proteção contra ataques comuns
- Checklist de deploy

### Sales (Vendas)

[SALES_SECURITY.md](docs/SALES_SECURITY.md) para detalhes sobre:

- Arquitetura multi-tenant
- Fluxos de autenticação e autorização
- Validações por operação (CREATE, READ, UPDATE, DELETE)
- Tratamento seguro de erros
- Proteção contra ataques
- Checklist de deploy

## Guia por Perfil

### Frontend Developer

Desenvolver interfaces para clientes, produtos e vendas:

1. Leia [Setup Clientes](docs/CLIENTS_EXAMPLES.md#setup-e-autenticação)
2. Leia [Setup Products](docs/PRODUCTS_EXAMPLES.md#setup-e-autenticação)
3. Leia [Setup Sales](docs/SALES_EXAMPLES.md#setup-e-autenticação)
4. Veja exemplos: [Clientes](docs/CLIENTS_EXAMPLES.md), [Products](docs/PRODUCTS_EXAMPLES.md), [Sales](docs/SALES_EXAMPLES.md)
5. Consulte [Casos de Erro](docs/SALES_EXAMPLES.md#casos-de-erro-comuns)

### Backend Developer

Implementar novas features ou otimizações:

1. Comece com [Visão Geral Clientes](docs/CLIENTS_README.md#visão-geral)
2. Comece com [Visão Geral Products](docs/PRODUCTS_README.md#visão-geral)
3. Comece com [Visão Geral Sales](docs/SALES_README.md#visão-geral)
4. Estude os modelos de dados
5. Analise a arquitetura de camadas
6. Entenda as operações GraphQL

### QA / Tester

Validar todas as funcionalidades:

1. Leia [Operações Clientes](docs/CLIENTS_README.md#operações-disponíveis-graphql)
2. Leia [Operações Products](docs/PRODUCTS_README.md#operações-disponíveis-graphql)
3. Leia [Operações Sales](docs/SALES_README.md#operações-disponíveis-graphql)
4. Execute os exemplos de cada módulo
5. Teste [Casos de Erro](docs/SALES_EXAMPLES.md#casos-de-erro-comuns)
6. Valide isolamento multi-tenant

### Product Manager / Business Analyst

Entender funcionalidades e regras de negócio:

1. Leia [Visão Geral dos Módulos](docs/MODULES_OVERVIEW.md)
2. Conheça [Tipos de Products](docs/PRODUCTS_README.md#enum-producttype)
3. Entenda [Regras de Negócio Sales](docs/SALES_README.md#regras-de-negócio)
4. Explore [Operações Disponíveis](docs/SALES_README.md#operações-disponíveis-graphql)
5. Estude os [Fluxos de Integração](docs/MODULES_OVERVIEW.md#-fluxos-de-integração)

### Security Architect

Revisar segurança de todos os módulos:

1. Estude [Multi-Tenant Clientes](docs/CLIENTS_SECURITY.md#1-arquitetura-multi-tenant)
2. Estude [Multi-Tenant Products](docs/PRODUCTS_SECURITY.md#1-arquitetura-multi-tenant)
3. Estude [Multi-Tenant Sales](docs/SALES_SECURITY.md#1-arquitetura-multi-tenant)
4. Revise [Autenticação](docs/SALES_SECURITY.md#2-fluxo-de-autenticação-e-autorização)
5. Valide [Testes de Segurança](docs/SALES_SECURITY.md#8-testes-de-segurança)
6. Acompanhe [Checklist Deploy](docs/SALES_SECURITY.md#9-checklist-de-deploy)

## Setup e Desenvolvimento

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 17+
- (Opcional) Docker Compose

### Executar Localmente

```bash
# Build
mvn clean install

# Executar
mvn spring-boot:run

# A API estará disponível em:
# GraphQL Playground: http://localhost:8080/graphiql
# GraphQL Endpoint: http://localhost:8080/graphql
```

## Estrutura da Documentação

Todos os documentos estão em `/docs/`:

### 📍 Comece Aqui (Índices Centralizados)

- **[DOCUMENTATION_INDEX.md](docs/DOCUMENTATION_INDEX.md)** → Mapa completo de toda a documentação ⭐
- **[MODULES_OVERVIEW.md](docs/MODULES_OVERVIEW.md)** → Visão geral dos 3 módulos
- **[README_CLIENTS_OVERVIEW.md](docs/README_CLIENTS_OVERVIEW.md)** → Guia de navegação

### 👥 Módulo Clientes

### 👥 Módulo Clientes

- **[CLIENTS_INDEX.md](docs/CLIENTS_INDEX.md)** → Índice centralizado ⭐ Comece aqui!
- **[CLIENTS_README.md](docs/CLIENTS_README.md)** → Documentação técnica detalhada
- **[CLIENTS_EXAMPLES.md](docs/CLIENTS_EXAMPLES.md)** → Exemplos práticos prontos para usar
- **[CLIENTS_SECURITY.md](docs/CLIENTS_SECURITY.md)** → Tudo sobre segurança e multi-tenancy
- **[CLIENTS_ARCHITECTURE.md](docs/CLIENTS_ARCHITECTURE.md)** → Fluxos e arquitetura
- **[CLIENTS_QUICKSTART.md](docs/CLIENTS_QUICKSTART.md)** → 5 minutos para começar

### 📦 Módulo Products

- **[PRODUCTS_INDEX.md](docs/PRODUCTS_INDEX.md)** → Índice centralizado ⭐ Comece aqui!
- **[PRODUCTS_README.md](docs/PRODUCTS_README.md)** → Documentação técnica detalhada
- **[PRODUCTS_EXAMPLES.md](docs/PRODUCTS_EXAMPLES.md)** → Exemplos práticos prontos para usar
- **[PRODUCTS_SECURITY.md](docs/PRODUCTS_SECURITY.md)** → Tudo sobre segurança e multi-tenancy

### 💳 Módulo Sales (Vendas) ⭐ NOVO

- **[SALES_INDEX.md](docs/SALES_INDEX.md)** → Índice centralizado ⭐ Comece aqui!
- **[SALES_README.md](docs/SALES_README.md)** → Documentação técnica detalhada
- **[SALES_EXAMPLES.md](docs/SALES_EXAMPLES.md)** → Exemplos práticos prontos para usar
- **[SALES_SECURITY.md](docs/SALES_SECURITY.md)** → Tudo sobre segurança e multi-tenancy

### 📊 Visão Geral e Configuração

- **[MODULES_OVERVIEW.md](docs/MODULES_OVERVIEW.md)** → Visão geral e comparação dos 3 módulos
- **[REDIS_DOCKER.md](docs/REDIS_DOCKER.md)** → Configuração de Redis e Docker

## Status do Projeto

| Componente      | Status             | Documentação |
| --------------- | ------------------ | ------------ |
| Módulo Clientes | ✅ Completo        | ✅ Completo  |
| Módulo Products | ✅ Completo        | ✅ Completo  |
| Módulo Sales    | ✅ Completo        | ✅ Completo  |
| Testes          | Em desenvolvimento | -            |
| Segurança       | ✅ Teste           | ✅ Completo  |
| Produção        | Ativo              | -            |

## Contribuindo

1. Consulte a documentação em `/docs/` antes de começar
2. Siga as convenções de código existentes
3. Execute os testes antes de fazer commit
4. Mantenha a segurança multi-tenant em mente

## Tipos de Produtos Suportados

O sistema oferece suporte aos seguintes tipos de produtos para o segmento de prevenção e combate a incêndio:

EXTINGUISHER, SERVICE, CONSUMABLE, ACCESSORY, HOSE, DETECTOR, SPRINKLER, CENTRAL, LIGHTING, DOOR, HYDRANT

Veja [Enum ProductType](docs/PRODUCTS_README.md#enum-producttype) para detalhes completos.

## Operações GraphQL Disponíveis

### Módulo Clientes

- Listar clientes com paginação
- Buscar cliente por ID
- Criar novo cliente
- Atualizar cliente existente

### Módulo Products

- Listar produtos com paginação
- Buscar produto por ID
- Listar produtos ativos
- Criar novo produto
- Atualizar produto existente
- Inativar/desativar produto

### Módulo Sales (Vendas)

- Listar vendas com paginação
- Buscar venda por ID
- Listar vendas por cliente
- Criar nova venda
- Atualizar venda
- Adicionar item à venda
- Remover item da venda
- Aplicar desconto
- Completar venda
- Cancelar venda

Veja [Operações Products](docs/PRODUCTS_README.md#operações-disponíveis-graphql), [Operações Clientes](docs/CLIENTS_README.md#operações-disponíveis-graphql) e [Operações Sales](docs/SALES_README.md#operações-disponíveis-graphql) para detalhes completos.

## 🚀 Documentação Rápida por Módulo

### Comece com Sales (Vendas) ⭐ NOVO

1. Leia [SALES_INDEX.md](docs/SALES_INDEX.md) - Índice com guia rápido
2. Veja [Fluxo Rápido de Venda](docs/SALES_INDEX.md#fluxo-rápido-criar-e-finalizar-uma-venda)
3. Execute [Exemplos Práticos](docs/SALES_EXAMPLES.md)
4. Estude [Segurança](docs/SALES_SECURITY.md)

### Comece com Products

1. Leia [PRODUCTS_INDEX.md](docs/PRODUCTS_INDEX.md) - Índice com guia rápido
2. Veja exemplos em [PRODUCTS_EXAMPLES.md](docs/PRODUCTS_EXAMPLES.md)
3. Estude [Segurança](docs/PRODUCTS_SECURITY.md)

### Comece com Clientes

1. Leia [CLIENTS_INDEX.md](docs/CLIENTS_INDEX.md) - Índice com guia rápido
2. Veja exemplos em [CLIENTS_EXAMPLES.md](docs/CLIENTS_EXAMPLES.md)
3. Estude [Segurança](docs/CLIENTS_SECURITY.md)

## 📚 Índice Completo da Documentação

Para um mapa completo de toda a documentação, recursos por perfil e sequências de aprendizado, consulte:

👉 **[DOCUMENTATION_INDEX.md](docs/DOCUMENTATION_INDEX.md)** - Índice centralizado de toda a documentação

Este arquivo contém:
- Guia por perfil (Frontend, Backend, Arquiteto, QA, etc.)
- Sequência recomendada de aprendizado
- Buscador rápido de informações
- Tempo estimado para cada documento
- Checklist de requisitos

## 📞 Suporte e Contribuições

- 📖 Documentação: [`/docs`](docs)
- 🐛 Issues: GitHub Issues
- 💡 Sugestões: Abra uma issue com label `documentation`
- 🔧 Contribuindo: Consulte [DOCUMENTATION_INDEX.md](docs/DOCUMENTATION_INDEX.md#contribuindo-com-documentação)

## Licença

TCC - Trabalho de Conclusão de Curso
