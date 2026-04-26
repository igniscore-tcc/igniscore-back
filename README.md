# Igniscore - API GraphQL

Plataforma de gestão de clientes e produtos com suporte completo a multi-tenancy, autenticação JWT e operações GraphQL.

## Visão Geral

O **Igniscore** é uma aplicação Spring Boot que fornece uma API GraphQL robusta para gerenciar:

- **Clients**: Gestão de clientes com isolamento por empresa (tenant)
- **Companies**: Gerenciamento de empresas contratantes
- **Products**: Catálogo de produtos com tipos e categorias
- **Users**: Autenticação e autorização com JWT
- **Authentication**: Sistema seguro de login e controle de acesso

## Documentação Modular

Toda a documentação está organizada em `/docs/` com índices e exemplos para cada módulo:

### Documentação do Módulo Clientes

| Documento                                                     | Descrição                                   | Público            |
| ------------------------------------------------------------- | ------------------------------------------- | ------------------ |
| [README_CLIENTS_OVERVIEW.md](docs/README_CLIENTS_OVERVIEW.md) | Ponto de entrada rápido - Guia de navegação | Todos              |
| [CLIENTS_README.md](docs/CLIENTS_README.md)                   | Documentação técnica completa               | Devs, Arquitetos   |
| [CLIENTS_EXAMPLES.md](docs/CLIENTS_EXAMPLES.md)               | Exemplos práticos prontos para usar         | Frontend, Testers  |
| [CLIENTS_SECURITY.md](docs/CLIENTS_SECURITY.md)               | Segurança e multi-tenancy                   | Arquitetos, DevOps |
| [CLIENTS_INDEX.md](docs/CLIENTS_INDEX.md)                     | Índice centralizado                         | Referência         |

### Documentação do Módulo Products

| Documento                                         | Descrição                              | Público            |
| ------------------------------------------------- | -------------------------------------- | ------------------ |
| [PRODUCTS_INDEX.md](docs/PRODUCTS_INDEX.md)       | Índice centralizado e ponto de entrada | Todos              |
| [PRODUCTS_README.md](docs/PRODUCTS_README.md)     | Documentação técnica completa          | Devs, Arquitetos   |
| [PRODUCTS_EXAMPLES.md](docs/PRODUCTS_EXAMPLES.md) | Exemplos práticos prontos para usar    | Frontend, Testers  |
| [PRODUCTS_SECURITY.md](docs/PRODUCTS_SECURITY.md) | Segurança e multi-tenancy              | Arquitetos, DevOps |

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

### 4. Listar Produtos (Paginado)

```graphql
query {
  products(page: 0, size: 10) {
    id
    name
    type
    price
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
- **Catalogo de Produtos**: Tipos predefinidos (extintor, sprinkler, etc)
- **Soft Delete**: Inativação lógica sem exclusão física

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

## Guia por Perfil

### Frontend Developer

Atualizar dados de clientes e produtos:

1. Leia [Setup Clientes](docs/CLIENTS_EXAMPLES.md#setup-e-autenticação)
2. Leia [Setup Products](docs/PRODUCTS_EXAMPLES.md#setup-e-autenticação)
3. Veja [Exemplos de Clientes](docs/CLIENTS_EXAMPLES.md#criação-de-clientes)
4. Veja [Exemplos de Products](docs/PRODUCTS_EXAMPLES.md#criação-de-produtos)
5. Consulte [Casos de Erro](docs/PRODUCTS_EXAMPLES.md#casos-de-erro-comuns)

### Backend Developer

Implementar novas features:

1. Comece com [Visão Geral Clientes](docs/CLIENTS_README.md#visão-geral)
2. Comece com [Visão Geral Products](docs/PRODUCTS_README.md#visão-geral)
3. Estude [Modelo Clientes](docs/CLIENTS_README.md#modelo-de-dados)
4. Estude [Modelo Products](docs/PRODUCTS_README.md#modelo-de-dados)
5. Analise [Arquitetura](docs/PRODUCTS_README.md#arquitetura-da-camada-de-products)
6. Entenda [Operações GraphQL](docs/PRODUCTS_README.md#operações-disponíveis-graphql)

### QA / Tester

Validar funcionalidades:

1. Leia [Operações Clientes](docs/CLIENTS_README.md#operações-disponíveis-graphql)
2. Leia [Operações Products](docs/PRODUCTS_README.md#operações-disponíveis-graphql)
3. Execute [Exemplos Clientes](docs/CLIENTS_EXAMPLES.md)
4. Execute [Exemplos Products](docs/PRODUCTS_EXAMPLES.md)
5. Teste [Casos de Erro](docs/PRODUCTS_EXAMPLES.md#casos-de-erro-comuns)
6. Valide [Isolamento Multi-Tenant](docs/PRODUCTS_SECURITY.md#8-testes-de-segurança)

### Product Manager / Business Analyst

Entender funcionalidades:

1. Leia [Visão Geral Clientes](docs/CLIENTS_README.md#visão-geral)
2. Leia [Visão Geral Products](docs/PRODUCTS_README.md#visão-geral)
3. Entenda [Regras Clientes](docs/CLIENTS_README.md#regras-de-negócio)
4. Entenda [Regras Products](docs/PRODUCTS_README.md#regras-de-negócio)
5. Conheça [Tipos de Products](docs/PRODUCTS_README.md#enum-producttype)
6. Explore [Operações Disponíveis](docs/PRODUCTS_README.md#operações-disponíveis-graphql)

### Security Architect

Revisar segurança:

1. Comece com [Multi-Tenant Clientes](docs/CLIENTS_SECURITY.md#1-arquitetura-multi-tenant)
2. Comece com [Multi-Tenant Products](docs/PRODUCTS_SECURITY.md#1-arquitetura-multi-tenant)
3. Estude [Autenticação Clientes](docs/CLIENTS_SECURITY.md#2-fluxo-de-autenticação-e-autorização)
4. Estude [Autenticação Products](docs/PRODUCTS_SECURITY.md#2-fluxo-de-autenticação-e-autorização)
5. Revise [Testes de Segurança](docs/PRODUCTS_SECURITY.md#8-testes-de-segurança)
6. Acompanhe [Checklist Deploy](docs/PRODUCTS_SECURITY.md#9-checklist-de-deploy)

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

### Módulo Clientes

- **README_CLIENTS_OVERVIEW.md** → Onde começar (leia primeiro!)
- **CLIENTS_INDEX.md** → Índice centralizado da documentação
- **CLIENTS_README.md** → Documentação técnica detalhada
- **CLIENTS_EXAMPLES.md** → Exemplos práticos prontos para usar
- **CLIENTS_SECURITY.md** → Tudo sobre segurança e multi-tenancy
- **CLIENTS_QUICKSTART.md** → Guia rápido de início

### Módulo Products

- **PRODUCTS_INDEX.md** → Índice centralizado (leia primeiro!)
- **PRODUCTS_README.md** → Documentação técnica detalhada
- **PRODUCTS_EXAMPLES.md** → Exemplos práticos prontos para usar
- **PRODUCTS_SECURITY.md** → Tudo sobre segurança e multi-tenancy

## Status do Projeto

| Componente      | Status             |
| --------------- | ------------------ |
| Módulo Clientes | Completo           |
| Módulo Products | Completo           |
| Documentação    | Completo           |
| Testes          | Em desenvolvimento |
| Segurança       | Auditado           |
| Produção        | Ativo              |

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

Veja [Operações Products](docs/PRODUCTS_README.md#operações-disponíveis-graphql) e [Operações Clientes](docs/CLIENTS_README.md#operações-disponíveis-graphql) para detalhes completos.

## Licença

TCC - Trabalho de Conclusão de Curso
