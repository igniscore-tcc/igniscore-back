# Igniscore - API GraphQL

Plataforma de gestão de clientes e produtos com suporte completo a multi-tenancy, autenticação JWT e operações GraphQL.

## 🎯 Visão Geral

O **Igniscore** é uma aplicação Spring Boot que fornece uma API GraphQL robusta para gerenciar:
- **Clients**: Gestão de clientes com isolamento por empresa (tenant)
- **Companies**: Gerenciamento de empresas contratantes
- **Products**: Catálogo de produtos com tipos e categorias
- **Users**: Autenticação e autorização com JWT
- **Authentication**: Sistema seguro de login e controle de acesso

## 📚 Documentação de Clientes

Toda a documentação detalhada do módulo de clientes está em `/docs/`:

### Documentos Disponíveis

| Documento | Descrição | Público |
|-----------|-----------|---------|
| [README_CLIENTS_OVERVIEW.md](docs/README_CLIENTS_OVERVIEW.md) | **Ponto de entrada rápido** - Guia de navegação e sumário | Todos |
| [CLIENTS_README.md](docs/CLIENTS_README.md) | Documentação técnica completa - Modelo, arquitetura, operações | Devs, Arquitetos |
| [CLIENTS_EXAMPLES.md](docs/CLIENTS_EXAMPLES.md) | Exemplos práticos prontos para usar | Frontend, Testers |
| [CLIENTS_SECURITY.md](docs/CLIENTS_SECURITY.md) | Segurança, multi-tenancy, proteção contra ataques | Arquitetos, DevOps |
| [CLIENTS_INDEX.md](docs/CLIENTS_INDEX.md) | Índice centralizado de toda a documentação | Referência |

## 🚀 Início Rápido

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
  storeClient(input: {
    name: "Acme Corp"
    cnpj: "12.345.678/0001-99"
    email: "contato@acme.com"
    phone: "(11) 3000-0000"
  }) {
    id
    name
    cnpj
    number
    company { id name }
  }
}
```

### 3. Listar Clientes (Paginado)

```graphql
query {
  clients(page: 0, size: 10) {
    id
    name
    cnpj
    email
    number
  }
}
```

## 📊 Arquitetura

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

## ✨ Características Principais

- ✅ **Multi-Tenancy**: Isolamento completo de dados por empresa
- ✅ **GraphQL API**: Queries e Mutations tipadas e documentadas
- ✅ **Autenticação JWT**: Token-based com expiração configurável
- ✅ **Autorização**: Controle de acesso baseado em role (ADMIN, USER)
- ✅ **Validações**: CNPJ, Email, CPF, IE com regras de negócio
- ✅ **Paginação**: Suporte a listagens com offset/limit
- ✅ **Tratamento de Erros**: Mensagens seguras e estruturadas
- ✅ **Auditoria**: Logs de operações críticas

## 🔒 Segurança

Consulte [CLIENTS_SECURITY.md](docs/CLIENTS_SECURITY.md) para detalhes sobre:
- Arquitetura multi-tenant
- Fluxos de autenticação e autorização
- Proteção contra SQL Injection, CSRF, XSS
- Validações por operação (CREATE, READ, UPDATE, DELETE)
- Testes de segurança
- Checklist de deploy

## 📋 Guia por Perfil

### Frontend Developer
1. Leia [Setup e Autenticação](docs/CLIENTS_EXAMPLES.md#setup-e-autenticação)
2. Veja [Exemplos de Criação](docs/CLIENTS_EXAMPLES.md#criação-de-clientes)
3. Consulte [Casos de Erro](docs/CLIENTS_EXAMPLES.md#casos-de-erro-comuns)

### Backend Developer
1. Comece com [Visão Geral](docs/CLIENTS_README.md#visão-geral)
2. Estude [Modelo de Dados](docs/CLIENTS_README.md#modelo-de-dados)
3. Analise [Arquitetura da Camada](docs/CLIENTS_README.md#arquitetura-da-camada-de-clients)
4. Entenda [Operações GraphQL](docs/CLIENTS_README.md#operações-disponíveis-graphql)

### QA / Tester
1. Leia [Operações Disponíveis](docs/CLIENTS_README.md#operações-disponíveis-graphql)
2. Execute [Exemplos de Teste](docs/CLIENTS_EXAMPLES.md)
3. Teste [Casos de Erro](docs/CLIENTS_EXAMPLES.md#casos-de-erro-comuns)
4. Valide [Isolamento Multi-Tenant](docs/CLIENTS_SECURITY.md#9-testes-de-segurança)

### Product Manager / Business Analyst
1. Leia [Visão Geral](docs/CLIENTS_README.md#visão-geral)
2. Entenda [Modelo de Dados](docs/CLIENTS_README.md#modelo-de-dados)
3. Conheça [Regras de Negócio](docs/CLIENTS_README.md#regras-de-negócio)
4. Explore [Operações Disponíveis](docs/CLIENTS_README.md#operações-disponíveis-graphql)

## 🛠 Setup e Desenvolvimento

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

## 📖 Estrutura da Documentação

Todos os documentos estão em `/docs/`:

- **README_CLIENTS_OVERVIEW.md** → Onde começar (leia primeiro!)
- **CLIENTS_INDEX.md** → Índice centralizado da documentação
- **CLIENTS_README.md** → Documentação técnica detalhada
- **CLIENTS_EXAMPLES.md** → Exemplos práticos prontos para usar
- **CLIENTS_SECURITY.md** → Tudo sobre segurança e multi-tenancy
- **CLIENTS_QUICKSTART.md** → Guia rápido de início

## 📊 Status do Projeto

| Componente | Status |
|-----------|--------|
| Desenvolvimento | ✅ Completo |
| Documentação | ✅ Completo |
| Testes | ✅ Implementado |
| Segurança | ✅ Auditado |
| Produção | ✅ Ativo |

## 🤝 Contribuindo

1. Consulte a documentação em `/docs/` antes de começar
2. Siga as convenções de código existentes
3. Execute os testes antes de fazer commit
4. Mantenha a segurança multi-tenant em mente

## 📞 Contatos

| Área | Contato |
|------|---------|
| Desenvolvimento | dev@igniscore.com |
| Segurança | security@igniscore.com |
| DevOps | ops@igniscore.com |

## 📝 Licença

TCC - Trabalho de Conclusão de Curso
