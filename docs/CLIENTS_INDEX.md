# Documentação Completa – Módulo Clients (Igniscore)

## Índice de Documentação

Este é um índice centralizado da documentação do módulo **Clients** do projeto Igniscore. Abaixo você encontrará todas as documentações disponíveis, organizadas por tema.

---

## Documentos Disponíveis

### 1. [README Técnico – Módulo Clients](./CLIENTS_README.md)
**Tipo**: Documentação Técnica Completa  
**Público Alvo**: Desenvolvedores, Arquitetos, Product Managers

Documentação técnica abrangente cobrindo:
- Visão geral e objetivos do módulo
- Modelo de dados (tabela `clients`, campos, constraints)
- Regras de negócio e ciclo de vida
- Arquitetura de camadas (Controller → Service → Repository)
- Operações GraphQL disponíveis (Queries e Mutations)
- Integrações com outros módulos
- Segurança e isolamento multi-tenant
- Tratamento de erros
- Configuração e deployment
- Testes

**Seções Principais**:
- [Modelo de Dados](./CLIENTS_README.md#modelo-de-dados)
- [Operações GraphQL](./CLIENTS_README.md#operações-disponíveis-graphql)
- [Integrações](./CLIENTS_README.md#integrações)
- [Segurança](./CLIENTS_README.md#segurança-e-multi-tenancy)

---

### 2. [Guia Prático – Exemplos de Uso](./CLIENTS_EXAMPLES.md)
**Tipo**: Guia de Referência com Exemplos  
**Público Alvo**: Desenvolvedores Frontend, Testers, Integradores

Exemplos práticos e prontos para usar cobrindo:
- Setup e autenticação (obtenção de JWT)
- Criação de clientes (simples, completa, em lote)
- Consultas e buscas (paginação, busca específica)
- Atualizações parciais e em lote
- Deleção e inativação
- Casos de erro comuns com respostas reais
- Integração com módulos relacionados
- Dicas de performance

**Seções Principais**:
- [Exemplos de Criação](./CLIENTS_EXAMPLES.md#criação-de-clientes)
- [Exemplos de Consulta](./CLIENTS_EXAMPLES.md#consultas-e-buscas)
- [Casos de Erro](./CLIENTS_EXAMPLES.md#casos-de-erro-comuns)
- [Integração com Sales/Contacts/Addresses](./CLIENTS_EXAMPLES.md#integração-com-outros-módulos)

---

### 3. [Guia de Segurança e Multi-Tenancy](./CLIENTS_SECURITY.md)
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
- [Arquitetura Multi-Tenant](./CLIENTS_SECURITY.md#2-arquitetura-multi-tenant)
- [Filtragem por Tenant](./CLIENTS_SECURITY.md#4-filtragem-de-clientes-por-tenant)
- [Validações por Operação](./CLIENTS_SECURITY.md#5-validações-de-segurança)
- [Proteção contra Ataques](./CLIENTS_SECURITY.md#8-proteção-contra-ataques-comuns)
- [Testes de Segurança](./CLIENTS_SECURITY.md#9-testes-de-segurança)

---

## Guia Rápido por Caso de Uso

### Para Front-End Developer
1. Leia [Setup e Autenticação](./CLIENTS_EXAMPLES.md#setup-e-autenticação)
2. Veja [Exemplos de Criação](./CLIENTS_EXAMPLES.md#exemplo-1-criar-cliente-simples)
3. Consulte [Casos de Erro](./CLIENTS_EXAMPLES.md#casos-de-erro-comuns)

### Para Back-End Developer
1. Comece com [Visão Geral](./CLIENTS_README.md#visão-geral)
2. Estude [Modelo de Dados](./CLIENTS_README.md#modelo-de-dados)
3. Analise [Arquitetura da Camada](./CLIENTS_README.md#arquitetura-da-camada-de-clients)
4. Entenda [Operações GraphQL](./CLIENTS_README.md#operações-disponíveis-graphql)
5. Verifique [Segurança](./CLIENTS_SECURITY.md)

### Para QA / Tester
1. Leia [Operações Disponíveis](./CLIENTS_README.md#operações-disponíveis-graphql)
2. Execute [Exemplos de Teste](./CLIENTS_EXAMPLES.md)
3. Teste [Casos de Erro](./CLIENTS_EXAMPLES.md#casos-de-erro-comuns)
4. Valide [Isolamento Multi-Tenant](./CLIENTS_SECURITY.md#9-testes-de-segurança)

### Para Product Manager / Business Analyst
1. Leia [Visão Geral](./CLIENTS_README.md#visão-geral)
2. Entenda [Modelo de Dados](./CLIENTS_README.md#modelo-de-dados)
3. Conheça [Regras de Negócio](./CLIENTS_README.md#regras-de-negócio)
4. Explore [Operações Disponíveis](./CLIENTS_README.md#operações-disponíveis-graphql)

### Para Security Architect
1. Comece com [Arquitetura Multi-Tenant](./CLIENTS_SECURITY.md#2-arquitetura-multi-tenant)
2. Estude [Fluxo de Autenticação](./CLIENTS_SECURITY.md#3-fluxo-de-autenticação-e-autorização)
3. Analise [Filtragem por Tenant](./CLIENTS_SECURITY.md#4-filtragem-de-clientes-por-tenant)
4. Revise [Checklist de Deploy](./CLIENTS_SECURITY.md#10-checklist-de-deploy)

---

## Resumo do Módulo

| Aspecto | Descrição |
|--------|-----------|
| **Tabela Principal** | `clients` |
| **Campos Principais** | id, name, cnpj, email, phone, ie, cpf, obs |
| **Modelo** | Multi-tenant (1 cliente : 1 empresa) |
| **Isolamento** | Por `fk_id_company` |
| **Autenticação** | JWT Bearer Token |
| **API** | GraphQL (queries + mutations) |
| **Integração** | Sales, Contacts, Addresses, Company |
| **Operações CRUD** | Create, Read, Update, Delete (Soft) |
| **Status** | Produção |

---

## Fluxo Rápido: Criar um Cliente

### 1. Autenticar
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { login(input: {email: \"admin@empresa.com\", password: \"senha\"}) { token } }"}'
```

### 2. Criar Cliente
```graphql
mutation {
  storeClient(input: {
    name: "Acme Corp"
    cnpj: "12.345.678/0001-99"
    email: "contato@acme.com"
  }) {
    id
    name
    number
  }
}
```

### 3. Listar Clientes
```graphql
query {
  clients(page: 0, size: 10) {
    id
    name
    cnpj
  }
}
```

**Veja exemplos completos em**: [Exemplos de Uso](./CLIENTS_EXAMPLES.md)

---

## Contatos e Suporte

| Função | Contato |
|--------|---------|
| Desenvolvedor | dev@igniscore.com |
| Segurança | security@igniscore.com |
| DevOps | ops@igniscore.com |
| Issue Tracker | GitHub Issues |

---

## Histórico de Versões

| Versão | Data | Atualizações |
|--------|------|-------------|
| 1.0 | 25/04/2026 | Documentação inicial completa |

---

## Notas Importantes

### Multi-Tenancy
- CADA operação deve validar que o cliente pertence à empresa autenticada
- Nunca retornar clientes de outras empresas
- Logs de auditoria são obrigatórios

### Segurança
- Sempre usar JWT para autenticação
- Senhas devem ser hasheadas com bcrypt
- Mensagens de erro não devem revelar detalhes
- Validar CNPJ com dígitos verificadores

### Performance
- Use paginação (máximo 100 registros por página)
- Implemente cache para queries frequentes
- Use índices em `fk_id_company`, `cnpj`, `email`
- Considere cursor-based pagination para grandes datasets

### Validações
- CNPJ deve ser válido e único por empresa
- Email deve estar em formato válido
- CPF (se fornecido) deve ser válido
- Campos obrigatórios: name, cnpj, email

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

## Checklist Inicial

Novo desenvolvedor? Siga este checklist:

- [ ] Leia [Visão Geral do Módulo](./CLIENTS_README.md#visão-geral)
- [ ] Estude o [Modelo de Dados](./CLIENTS_README.md#modelo-de-dados)
- [ ] Entenda a [Arquitetura de Camadas](./CLIENTS_README.md#arquitetura-da-camada-de-clients)
- [ ] Analise o código das seguintes classes:
  - [ ] `Client.java` (entity)
  - [ ] `ClientRepository.java` (data access)
  - [ ] `ClientService.java` (business logic)
  - [ ] `ClientController.java` (API)
- [ ] Execute os [Exemplos de Teste](./CLIENTS_EXAMPLES.md)
- [ ] Leia a seção de [Segurança](./CLIENTS_SECURITY.md)
- [ ] Revise os [Casos de Erro](./CLIENTS_EXAMPLES.md#casos-de-erro-comuns)
- [ ] Faça um test simples (criar + listar + atualizar cliente)

---

**Documentação Versão**: 1.0  
**Data**: 25 de abril de 2026  
**Status**: Completo e em Produção

Para dúvidas ou contribuições, consulte o [README técnico principal](./CLIENTS_README.md).
