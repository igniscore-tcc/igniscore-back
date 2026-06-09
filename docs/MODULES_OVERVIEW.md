# Visão Geral dos Módulos Igniscore

## Objetivo

Este documento apresenta a organização dos módulos do Igniscore, os seus relacionamentos e os principais fluxos de integração entre os componentes da plataforma.

## Visão Geral

A documentação do Igniscore está organizada em módulos independentes que representam os principais domínios de negócio da plataforma.

---

## 1. Módulo Clients (Clientes)

Gestão de clientes pertencentes às empresas (tenants).

### Documentação

- **[CLIENTS_INDEX.md](clients/CLIENTS_INDEX.md)** - Índice centralizado *COMECE AQUI*
- **[CLIENTS_README.md](clients/CLIENTS_README.md)** - Documentação técnica completa
- **[CLIENTS_SECURITY.md](clients/CLIENTS_SECURITY.md)** - Segurança e multi-tenancy
- **[CLIENTS_EXAMPLES.md](clients/CLIENTS_EXAMPLES.md)** - Exemplos práticos

### Casos de Uso

- Criar, consultar, atualizar e deletar clientes
- Gerir dados de contato e endereço
- Validar CNPJ e dados fiscais
- Integrar com vendas e contatos

### Relacionamentos

- **Sales**: Um cliente pode ter múltiplas vendas
- **Contacts**: Um cliente pode ter múltiplos contatos
- **Addresses**: Um cliente pode ter múltiplos endereços
- **Company**: Cada cliente pertence a uma empresa

---

## 2. Módulo Products (Produtos)

Gestão de produtos e serviços das empresas.

### Documentação

- **[PRODUCTS_INDEX.md](products/PRODUCTS_INDEX.md)** - Índice centralizado *COMECE AQUI*
- **[PRODUCTS_README.md](products/PRODUCTS_README.md)** - Documentação técnica completa
- **[PRODUCTS_SECURITY.md](products/PRODUCTS_SECURITY.md)** - Segurança e multi-tenancy
- **[PRODUCTS_EXAMPLES.md](products/PRODUCTS_EXAMPLES.md)** - Exemplos práticos

### Casos de Uso

- Cadastrar produtos (extintores, serviços, consumíveis, etc.)
- Gerir tipos de produtos
- Controlar validade e lotes
- Ativar/inativar produtos
- Gerir preços

### Relacionamentos

- **Sales**: Produtos são vendidos por vendas
- **Sale ‘Items’**: Cada ‘item’ de venda referencia um produto
- **Company**: Cada produto pertence a uma empresa

---

## 3. Módulo Sales (Vendas)

Gestão de vendas e transações.

### Documentação

- **[SALES_INDEX.md](sales/SALES_INDEX.md)** - Índice centralizado *COMECE AQUI*
- **[SALES_README.md](sales/SALES_README.md)** - Documentação técnica completa
- **[SALES_SECURITY.md](sales/SALES_SECURITY.md)** - Segurança e multi-tenancy
- **[SALES_EXAMPLES.md](sales/SALES_EXAMPLES.md)** - Exemplos práticos

### Casos de Uso

- Criar vendas para clientes
- Adicionar múltiplos itens por venda
- Aplicar descontos
- Gerenciar métodos de pagamento
- Rastrear status de vendas
- Gerar relatórios

### Relacionamentos

- **Clients**: Cada venda é associada a um cliente
- **Products**: Itens de venda contêm produtos
- **Company**: Cada venda pertence a uma empresa

## 4. Módulo Expirations (Vencimentos)

Gestão de vencimentos.

### Documentação

- **[EXPIRATIONS_INDEX](expirations/EXPIRATIONS_INDEX.md)** - Índice centralizado *COMECE AQUI*
- **[EXPIRATIONS_README.md](expirations/EXPIRATIONS_README.md)** - Documentação técnica completa
- **[EXPIRATIONS_SECURITY.md](expirations/EXPIRATIONS_SECURITY.md)** - Segurança e multi-tenancy
- **[EXPIRATIONS_EXAMPLES.md](expirations/EXPIRATIONS_EXAMPLES.md)** - Exemplos práticos

### Casos de Uso

- Listar vencimentos

### Relacionamentos

- **Sales**: Cada vencimento é associada a uma venda

## 5. Dashboard

Responsável pela consolidação de indicadores e métricas da plataforma.

### Casos de Uso

- Visualizar indicadores comerciais
- Acompanhar faturamento
- Monitorar vencimentos
- Consultar métricas operacionais

### Dependências

- Clients
- Products
- Sales

---

## Comparação dos Módulos

| Aspecto              | Clients     | Products       | Sales             | Expirations            | 
|----------------------|-------------|----------------|-------------------|------------------------|
| **Tabela Principal** | clients     | products       | sales, sale_items | expirations            |
| **Operações CRUD**   | Completo    | Completo       | Completo          | Consultas              |
| **Multi-tenant**     | Sim         | Sim            | Sim               | Sim                    |
| **Paginação**        | Sim         | Sim            | Sim               | Não                    |
| **GraphQL**          | Sim         | Sim            | Sim               | Sim                    |
| **Filtros**          | CNPJ, Email | Tipo, Status   | Status, Cliente   | Período, cliente, dias |
| **Validações**       | CNPJ, CPF   | Tipo, Validade | Desconto, Items   | Status                 |
| **Soft Delete**      | Sim         | Sim            | Sim               | Não                    |
| **Auditoria**        | Sim         | Sim            | Sim               | Não                    |
| **Segurança**        | JWT         | JWT            | JWT               | JWT                    |

---

## Arquitetura dos Módulos
```
              Company
                 │
     ┌───────────┼───────────┐
     ▼           ▼           ▼
 Clients     Products      Sales
                               │
                               ▼
                          Dashboard
```

## Dependências

| Módulo    | Depende de                 |
|-----------|----------------------------|
| Clients   | Company                    |
| Products  | Company                    |
| Sales     | Clients, Products, Company |
| Dashboard | Sales, Clients, Products   |

## Fluxos de Integração

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

## Estrutura da Documentação

Cada módulo possui:

- INDEX.md → Guia inicial
- README.md → Documentação técnica
- SECURITY.md → Segurança e multi-tenancy
- EXAMPLES.md → Casos práticos

---

## Links Rápidos

### Índices Centralizados
- [Clientes](clients/CLIENTS_INDEX.md)
- [Produtos](products/PRODUCTS_INDEX.md)
- [Vendas](sales/SALES_INDEX.md)
- [Vencimentos](expirations/EXPIRATIONS_INDEX.md)

### Documentação Técnica
- [Clients README](clients/CLIENTS_README.md)
- [Products README](products/PRODUCTS_README.md)
- [Sales README](sales/SALES_README.md)
- [Expirations README](expirations/EXPIRATIONS_README.md)

### Segurança
- [Clients Security](clients/CLIENTS_SECURITY.md)
- [Products Security](products/PRODUCTS_SECURITY.md)
- [Sales Security](sales/SALES_SECURITY.md)
- [Expirations Security](expirations/EXPIRATIONS_SECURITY.md)

### Exemplos Práticos
- [Clients Examples](clients/CLIENTS_EXAMPLES.md)
- [Products Examples](products/PRODUCTS_EXAMPLES.md)
- [Sales Examples](sales/SALES_EXAMPLES.md)
- [Expirations Examples](expirations/EXPIRATIONS_EXAMPLES.md)

---

## Evolução Planejada

A evolução da plataforma prevê a expansão para novos módulos e funcionalidades voltadas à gestão comercial e operacional.


- Utilizadores e Permissões (RBAC)
- Financeiro
- Relatórios Avançados
- Notificações
- Integrações Externas

---

**Sistema:** Igniscore
**Versão da Documentação:** 1,0
**Status:** MVP funcional

Para sugestões ou atualizações, abra uma issue no GitHub.
