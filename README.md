![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-green)
![GraphQL](https://img.shields.io/badge/GraphQL-API-pink)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Redis](https://img.shields.io/badge/Redis-Cache-red)

# Igniscore - Plataforma de Gestão Comercial Multi-Tenant

Sistema de gestão comercial multi-tenant desenvolvido para empresas do setor de prevenção e combate a incêndio, permitindo gestão de clientes, produtos, vendas, vencimentos e indicadores de negócio por uma API GraphQL segura e escalável.

Desenvolvido como Trabalho de Conclusão de Curso utilizando Java 21, Spring Boot, PostgreSQL e GraphQL.

## Status

- ✅ MVP funcional concluído
- 🚧 Novas funcionalidades em desenvolvimento

## Público-Alvo

Empresas do setor de prevenção e combate a incêndio que necessitam:

- Gestão de clientes
- Controle de equipamentos
- Gestão comercial
- Monitoramento de vencimentos
- Indicadores operacionais e financeiros

## Funcionalidades

- Gestão de Clientes
- Gestão de Produtos
- Gestão de Vendas
- Controle de Vencimentos
- Dashboard de Indicadores

## Stack Tecnológica

### Backend
- Java 21
- Spring Boot 3
- Spring Security
- GraphQL

### Banco de Dados
- PostgreSQL
- JPA/Hibernate

### Performance
- Redis Cache

### Infraestrutura
- Docker
- Maven

### Segurança
- JWT Authentication

## Diferenciais do Projeto

- Multi-Tenancy com isolamento completo de dados por empresa
- API GraphQL tipada para consultas flexíveis e eficientes
- Cache Redis para otimização de consultas frequentes
- Dashboard analítico com indicadores de negócio em tempo real
- Controle automatizado de vencimentos de produtos e serviços
- Autenticação e autorização com JWT e Spring Security
- Soft Delete para preservação histórica de dados
- Arquitetura em camadas seguindo boas práticas de desenvolvimento
- Paginação para consultas escaláveis
- Validação de CPF, CNPJ, e-mail e regras de negócio

## Arquitetura

```
Frontend
    │
    ▼
GraphQL API
    │
    ▼
Spring Security + JWT
    │
    ▼
Resolvers
    │
    ▼
Services
    │
    ▼
Repositories
    │
 ┌──┴─────────┐
 ▼            ▼
Redis      PostgreSQL
```

## Módulos do Sistema

| Módulo         | Responsabilidade         |
|----------------|--------------------------|
| Clients        | Gestão de clientes       |
| Products       | Gestão de produtos       |
| Sales          | Gestão de vendas         |
| Expirations    | Controle de vencimentos  |
| Dashboard      | Indicadores e métricas   |
| Authentication | Segurança e autenticação |

## Características Arquiteturais

- Multi-Tenancy
- JWT Authentication
- GraphQL
- Redis Cache
- Soft Delete
- Paginação

## Performance

O sistema utiliza Redis como camada de cache para reduzir consultas repetitivas ao PostgreSQL.

Benefícios:

- Menor latência nas consultas
- Redução de carga no banco de dados
- Melhor desempenho dos dashboards
- Maior escalabilidade da aplicação

## Executando o Projeto

```bash
git clone https://github.com/seu-usuario/igniscore.git

cd igniscore

docker compose up -d

mvn clean install

mvn spring-boot:run
```

## Acesso

GraphQL Endpoint:
http://localhost:8080/graphql

GraphiQL:
http://localhost:8080/graphiql

## Início Rápido

### 1. Criar Cliente

```graphql
mutation {
    storeClient(
        input: {
            name: "Empresa Alpha LTDA"
            cnpj: "11444777000161"
            email: "contato@alpha.com"
            phone: "11999999999"
            ie: "123456782"
            ufIe: "SP"
            obs: "Cliente com CNPJ válido"
        }
    ) {
        id
        number
        name
        cnpj
        email
        phone
    }
}
```

### 2. Criar Venda

```graphql
mutation {
    storeSale(
        input: {
            clientId: 1
            paymentMethod: PIX
            items: [
                {
                    productId: 1
                    quantity: 2
                    unitPrice: 89.90
                }
            ]
        }
    ) {
        id
        quantityItems
        discount
        total
        date
        dueDate
        paymentMethod
        status
        items {
            id
            quantity
            unitPrice
            total
        }
    }
}
```

### 3. Ver KPIs

```graphql
query {
    dashboard {
        totalClients
        totalProducts
        totalSales
        monthlyRevenue
        pendingOrders
        expiringProducts
        currentMonthExpirations
        upcomingExpirations
        expiredExpirations
    }
}
```

## Estrutura do Projeto
```
    src/main/java
    ├── config
    ├── controller
    ├── service
    ├── repository
    ├── model
    ├── dto
    ├── security
    └── cache
```

## Documentação

- Clients → [docs/clients](docs/clients/CLIENTS_README.md)
- Products → [docs/products](docs/products/PRODUCTS_README.md)
- Sales → [docs/sales](docs/sales/SALES_README.md)
- Overview → [docs](docs/MODULES_OVERVIEW.md)

## Boas Práticas Aplicadas

- Arquitetura em Camadas
- Separação de Responsabilidades
- DTO Pattern
- Repository Pattern
- Soft Delete
- Multi-Tenancy
- Cache Layer
- Validações de Negócio
- Tratamento Global de Exceções

## Roadmap

### Concluído

- Gestão de Clientes
- Gestão de Produtos
- Gestão de Vendas
- Controle de Vencimentos
- Dashboard
- Redis Cache
- JWT Authentication

### Próximas Evoluções

- Testes automatizados
- CI/CD
- Deploy em Cloud
- Observabilidade

## Autores

Projeto desenvolvido como Trabalho de Conclusão de Curso por:

- Anderson Gama Silva
- Gabriel Sponchiado
- Gabriel Benedetti
- Guilherme Gonçalves Silva
- Gustavo Aristides do Carmo

### Orientação Acadêmica

- Prof. Emerson Rodrigo Baião

## Licença

Copyright © 2026 Igniscore.

Todos os direitos reservados aos autores.

Este ‘software’ foi desenvolvido inicialmente como Trabalho de Conclusão de Curso (TCC) e possui caráter proprietário.

Nenhuma parte deste ‘software’ pode ser reproduzida, distribuída, modificada ou utilizada para fins comerciais sem autorização expressa dos autores.