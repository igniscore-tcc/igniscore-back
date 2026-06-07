# Visão Geral dos Módulos Igniscore

## 📚 Documentação Disponível

A documentação do Igniscore é organizada em três módulos principais:

---

## 1. Módulo Clients (Clientes)

Gerenciamento de clientes pertencentes às empresas (tenants).

### 📄 Documentação

- **[CLIENTS_INDEX.md](clients/CLIENTS_INDEX.md)** - Índice centralizado ⭐ *COMECE AQUI*
- **[CLIENTS_README.md](clients/CLIENTS_README.md)** - Documentação técnica completa
- **[CLIENTS_SECURITY.md](clients/CLIENTS_SECURITY.md)** - Segurança e multi-tenancy
- **[CLIENTS_EXAMPLES.md](clients/CLIENTS_EXAMPLES.md)** - Exemplos práticos

### 🎯 Casos de Uso

- Criar, consultar, atualizar e deletar clientes
- Gerenciar dados de contato e endereço
- Validar CNPJ e dados fiscais
- Integrar com vendas e contatos

### 🔗 Relacionamentos

- ↔️ **Sales**: Um cliente pode ter múltiplas vendas
- ↔️ **Contacts**: Um cliente pode ter múltiplos contatos
- ↔️ **Addresses**: Um cliente pode ter múltiplos endereços
- ↔️ **Company**: Cada cliente pertence a uma empresa

---

## 2. Módulo Products (Produtos)

Gerenciamento de produtos e serviços das empresas.

### 📄 Documentação

- **[PRODUCTS_INDEX.md](products/PRODUCTS_INDEX.md)** - Índice centralizado ⭐ *COMECE AQUI*
- **[PRODUCTS_README.md](products/PRODUCTS_README.md)** - Documentação técnica completa
- **[PRODUCTS_SECURITY.md](products/PRODUCTS_SECURITY.md)** - Segurança e multi-tenancy
- **[PRODUCTS_EXAMPLES.md](products/PRODUCTS_EXAMPLES.md)** - Exemplos práticos

### 🎯 Casos de Uso

- Cadastrar produtos (extintores, serviços, consumíveis, etc.)
- Gerenciar tipos de produtos
- Controlar validade e lotes
- Ativar/inativar produtos
- Gerenciar preços

### 🔗 Relacionamentos

- ↔️ **Sales**: Produtos são vendidos através de vendas
- ↔️ **Sale Items**: Cada item de venda referencia um produto
- ↔️ **Company**: Cada produto pertence a uma empresa

---

## 3. Módulo Sales (Vendas)

Gerenciamento de vendas e transações.

### 📄 Documentação

- **[SALES_INDEX.md](sales/SALES_INDEX.md)** - Índice centralizado ⭐ *COMECE AQUI*
- **[SALES_README.md](sales/SALES_README.md)** - Documentação técnica completa
- **[SALES_SECURITY.md](sales/SALES_SECURITY.md)** - Segurança e multi-tenancy
- **[SALES_EXAMPLES.md](sales/SALES_EXAMPLES.md)** - Exemplos práticos

### 🎯 Casos de Uso

- Criar vendas para clientes
- Adicionar múltiplos itens por venda
- Aplicar descontos
- Gerenciar métodos de pagamento
- Rastrear status de vendas
- Gerar relatórios

### 🔗 Relacionamentos

- ↔️ **Clients**: Cada venda é associada a um cliente
- ↔️ **Products**: Itens de venda contêm produtos
- ↔️ **Company**: Cada venda pertence a uma empresa

---

## 📊 Comparação dos Módulos

| Aspecto | Clients | Products | Sales |
|---------|---------|----------|-------|
| **Tabela Principal** | clients | products | sales, sale_items |
| **Operações CRUD** | ✅ Completo | ✅ Completo | ✅ Completo |
| **Multi-tenant** | ✅ Sim | ✅ Sim | ✅ Sim |
| **Paginação** | ✅ Sim | ✅ Sim | ✅ Sim |
| **GraphQL** | ✅ Sim | ✅ Sim | ✅ Sim |
| **Filtros** | CNPJ, Email | Tipo, Status | Status, Cliente |
| **Validações** | CNPJ, CPF, IE | Tipo, Validade | Desconto, Items |
| **Soft Delete** | ✅ Sim | ✅ Sim | ✅ Sim |
| **Auditoria** | ✅ Sim | ✅ Sim | ✅ Sim |
| **Segurança** | ✅ JWT | ✅ JWT | ✅ JWT |

---

## 🎯 Guia Rápido por Perfil

### 👨‍💻 Para Front-End Developer

1. Leia [CLIENTS_INDEX.md](clients/CLIENTS_INDEX.md) → Setup e Autenticação
2. Leia [PRODUCTS_INDEX.md](products/PRODUCTS_INDEX.md) → Exemplos de Criação
3. Leia [SALES_INDEX.md](sales/SALES_INDEX.md) → Fluxo Rápido
4. Estude os exemplos em EXAMPLES.md

### 👨‍🔬 Para Back-End Developer

1. Comece com cada módulo README (visão geral + arquitetura)
2. Estude o modelo de dados (tabelas, fields, constraints)
3. Analise as classes principais (Entity, Service, Controller)
4. Revise os exemplos GraphQL
5. Leia o guia de segurança

### 🧪 Para QA / Tester

1. Revise as operações GraphQL disponíveis
2. Execute os exemplos dos EXAMPLES.md
3. Teste casos de erro comuns
4. Valide isolamento multi-tenant
5. Execute fluxos completos

### 📊 Para Product Manager

1. Leia a Visão Geral de cada módulo
2. Estude o Modelo de Dados
3. Conheça as Regras de Negócio
4. Explore as Operações Disponíveis
5. Revise as Integrações

### 🔒 Para Security Architect

1. Revise SECURITY.md de cada módulo
2. Estude a Arquitetura Multi-Tenant
3. Analise o Fluxo de Autenticação
4. Revise Validações por Operação
5. Execute Testes de Segurança
6. Acompanhe Checklist de Deploy

---

## 🔄 Fluxos de Integração

### Fluxo 1: Criar Venda para um Cliente com Produtos

```
1. Criar Cliente (Módulo Clients)
   └─ Retorna: Client ID

2. Buscar Produtos Ativos (Módulo Products)
   └─ Retorna: Product IDs

3. Criar Venda (Módulo Sales)
   ├─ Input: Client ID
   └─ Retorna: Sale ID

4. Adicionar Itens à Venda (Módulo Sales)
   ├─ Input: Sale ID + Product IDs
   └─ Retorna: Updated Sale

5. Aplicar Desconto (opcional)
   └─ Retorna: Updated Sale

6. Completar Venda
   └─ Retorna: Finalized Sale
```

### Fluxo 2: Relatório de Vendas por Cliente

```
1. Listar Clientes (Módulo Clients)
2. Para cada cliente:
   └─ Listar Vendas do Cliente (Módulo Sales)
3. Agregar dados
4. Gerar relatório
```

### Fluxo 3: Análise de Produtos Vendidos

```
1. Buscar Vendas COMPLETED (Módulo Sales)
2. Extrair Itens de cada Venda
3. Agrupar por Produto (Módulo Products)
4. Calcular totais
5. Gerar análise
```

---

## 📝 Estrutura de Documentação Padrão

Cada módulo segue a mesma estrutura:

```
📁 MÓDULO_NOME/
├── MÓDULO_INDEX.md          ⭐ Comece aqui
├── MÓDULO_README.md         📚 Técnico completo
├── MÓDULO_SECURITY.md       🔒 Segurança
└── MÓDULO_EXAMPLES.md       💡 Exemplos práticos
```

### Seções em cada INDEX.md

1. **Documentos Disponíveis** - Descrição de cada doc
2. **Guia Rápido por Caso de Uso** - Para cada perfil
3. **Resumo do Módulo** - Tabela com info principal
4. **Fluxo Rápido** - Exemplo básico
5. **Operações Disponíveis** - Queries e Mutations
6. **Checklist Inicial** - Para novos devs

### Seções em cada README.md

1. **Visão Geral** - O que é o módulo
2. **Modelo de Dados** - Tabelas e relacionamentos
3. **Regras de Negócio** - Lógica operacional
4. **Arquitetura** - Estrutura de camadas
5. **Operações GraphQL** - Queries e Mutations
6. **Integrações** - Relacionamento com outros módulos
7. **Segurança** - Multi-tenancy e validações
8. **Tratamento de Erros** - Códigos e mensagens
9. **Performance** - Índices e otimizações
10. **Configuração** - Deployment e env vars

### Seções em cada SECURITY.md

1. **Arquitetura Multi-Tenant** - Como funciona
2. **Autenticação e Autorização** - JWT e contexto
3. **Filtragem por Tenant** - Implementação
4. **Validações por Operação** - CREATE, READ, UPDATE, DELETE
5. **Tratamento Seguro de Erros** - Não expor dados
6. **Auditoria e Logging** - Rastreabilidade
7. **Proteção contra Ataques** - SQL Injection, CSRF, XSS
8. **Testes de Segurança** - Validação
9. **Checklist de Deploy** - Verificações

### Seções em cada EXAMPLES.md

1. **Setup e Autenticação** - Como começar
2. **Criar Recurso** - Exemplos variados
3. **Consultar Recurso** - Queries
4. **Atualizar Recurso** - Mutations
5. **Deletar/Inativar** - Soft delete
6. **Casos de Erro** - Erros comuns
7. **Fluxo Completo** - Exemplo realista
8. **Integração** - Com outros módulos
9. **Performance** - Dicas e boas práticas

---

## 🔗 Links Rápidos

### Índices Centralizados
- [Clientes](clients/CLIENTS_INDEX.md)
- [Produtos](products/PRODUCTS_INDEX.md)
- [Vendas](sales/SALES_INDEX.md)

### Documentação Técnica
- [Clients README](clients/CLIENTS_README.md)
- [Products README](products/PRODUCTS_README.md)
- [Sales README](sales/SALES_README.md)

### Segurança
- [Clients Security](clients/CLIENTS_SECURITY.md)
- [Products Security](products/PRODUCTS_SECURITY.md)
- [Sales Security](sales/SALES_SECURITY.md)

### Exemplos Práticos
- [Clients Examples](clients/CLIENTS_EXAMPLES.md)
- [Products Examples](products/PRODUCTS_EXAMPLES.md)
- [Sales Examples](sales/SALES_EXAMPLES.md)

---

## 📞 Contatos

| Função | Email |
|--------|-------|
| Desenvolvedor | dev@igniscore.com |
| Segurança | security@igniscore.com |
| DevOps | ops@igniscore.com |
| Issues | GitHub Issues |

---

## 📅 Histórico de Documentação

| Módulo | Versão | Data | Status |
|--------|--------|------|--------|
| Clients | 1.0 | 25/04/2026 | ✅ Completo |
| Products | 1.0 | 26/04/2026 | ✅ Completo |
| Sales | 1.0 | 01/06/2026 | ✅ Completo |

---

## 🎓 Recursos de Aprendizado

### Novos no Igniscore?

1. Leia [Visão Geral dos Módulos](#-documentação-disponível)
2. Escolha um módulo para começar (Clients é mais simples)
3. Leia o INDEX.md do módulo
4. Estude o README.md
5. Execute os exemplos do EXAMPLES.md
6. Revise o SECURITY.md
7. Faça um teste prático

### Especificações e Standards

- [GraphQL Spec](https://spec.graphql.org/)
- [Spring GraphQL](https://spring.io/projects/spring-graphql)
- [JPA/Hibernate](https://hibernate.org/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

### Segurança

- [OWASP Top 10](https://owasp.org/Top10/)
- [OWASP GraphQL](https://cheatsheetseries.owasp.org/cheatsheets/GraphQL_Cheat_Sheet.html)
- [Multi-Tenancy](https://www.postgresql.org/docs/current/ddl-schemas.html)

---

**Documentação Versão**: 1.0  
**Data**: 01 de junho de 2026  
**Status**: Completo e em Produção

Para sugestões ou atualizações, abra uma issue no GitHub.
