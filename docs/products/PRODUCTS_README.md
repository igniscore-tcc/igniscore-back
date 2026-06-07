# README Técnico – Módulo Clients (API GraphQL / Spring Boot)

## Visão Geral

O módulo **clients** é responsável pela gestão de clientes vinculados às empresas (tenants) da plataforma Igniscore.

Cada cliente pertence obrigatoriamente a uma empresa, garantindo isolamento completo de dados em ambiente multi-tenant.

O módulo permite operações de cadastro, consulta e atualização de clientes via API GraphQL, servindo como base para módulos como vendas e locações.

### Objetivos

- Centralizar o gerenciamento de clientes por empresa
- Garantir isolamento de dados entre tenants
- Disponibilizar operações CRUD via GraphQL
- Manter integridade e consistência dos dados
- Servir como entidade base para módulos transacionais

---

## Modelo de Dados

### Tabela `clients`


| Campo         | Tipo         | Constraint         | Descrição                    |
|---------------|--------------|--------------------|------------------------------|
| pk_id_client  | INT          | PK, Auto-increment | Identificador único          |
| name_client   | VARCHAR(100) | NOT NULL           | Nome do cliente              |
| cnpj_client   | VARCHAR(18)  | NOT NULL           | CNPJ                         |
| email_client  | VARCHAR(255) | NOT NULL           | Email principal              |
| phone_client  | VARCHAR(20)  | -                  | Telefone                     |
| number_client | INT          | NOT NULL           | Número sequencial por tenant |
| ie_client     | VARCHAR(14)  | -                  | Inscrição Estadual           |
| uf_ie_client  | CHAR(2)      | -                  | UF da IE                     |
| cpf_client    | VARCHAR(14)  | -                  | CPF                          |
| obs_client    | TEXT         | -                  | Observações                  |
| fk_id_company | INT          | FK, NOT NULL       | Empresa proprietária         |

### Constraints

```sql
ALTER TABLE clients
ADD CONSTRAINT fk_clients_company
FOREIGN KEY (fk_id_company)
REFERENCES companies(pk_id_company)
ON DELETE CASCADE;
```

```sql
ALTER TABLE clients
ADD CONSTRAINT uq_clients_company_number
UNIQUE (fk_id_company, number_client);
```

---

## Regras de Negócio

### Multi-Tenancy

- Cada cliente pertence a uma única empresa
- Acesso restrito à empresa autenticada
- Clientes podem existir com dados semelhantes entre empresas
- `number_client` é único por tenant

### Cadastro

Campos obrigatórios:

- name
- cnpj
- email

Campos opcionais:

- phone
- ie
- uf_ie
- cpf
- obs

### Atualização

- Atualização parcial (‘PATCH’ semântico)
- Apenas campos enviados são modificados
- Validações aplicadas em tempo de execução

### Validações

- Email deve ter formato válido
- CNPJ deve ser válido
- CPF válido quando informado
- IE consistente com UF quando informado
- Nome não pode ser vazio

---

## Arquitetura

```
GraphQL Controller
        ↓
   ClientService
        ↓
 ClientRepository
        ↓
      MySQL
```

---

## Estrutura do Projeto

```
src/main/java/com/igniscore/api

├── controller
│   └── ClientController.java

├── service
│   └── ClientService.java

├── repository
│   └── ClientRepository.java

├── dto
│   ├── ClientRegisterDTO.java
│   └── ClientUpdateDTO.java

├── model
│   └── Client.java
```

---

## Operações GraphQL

### Query – Listar Clientes

```graphql
query {
  clients(page: 0, size: 10) {
    clients {
      id
      number
      name
      email
      phone
      cpf
      cnpj
    }
    totalPages
    totalClients
  }
}
```

---

### Query – Cliente por ‘ID’

```graphql
query {
  clientById(id: 1) {
    id
    name
    cnpj
    email
    phone
    number
  }
}
```

---

### Mutation – Criar Cliente

```graphql
mutation {
  storeClient(
    input: {
      name: "Cliente XYZ"
      cnpj: "12.345.678/0001-99"
      email: "contato@cliente.com"
      phone: "(11) 99999-9999"
    }
  ) {
    id
    name
    cnpj
    email
  }
}
```

---

### Mutation – Atualizar Cliente

```graphql
mutation {
  updateClient(
    input: {
      id: 1
      phone: "(11) 98888-8888"
    }
  ) {
    id
    name
    phone
  }
}
```

---

## Segurança e Multi-Tenancy

Todas as operações aplicam isolamento por empresa:

```sql
WHERE fk_id_company = :authenticatedCompanyId
```

### Regra Base

- Nenhum cliente é acessível fora do tenant
- Validação ocorre na camada de serviço
- Empresa é derivada do JWT

---

## Tratamento de Erros

| Código           | Descrição                    |
|------------------|------------------------------|
| CLIENT_NOT_FOUND | Cliente não encontrado       |
| INVALID_CNPJ     | CNPJ inválido                |
| INVALID_EMAIL    | Email inválido               |
| UNAUTHORIZED     | Acesso negado ao tenant      |
| DUPLICATE_CLIENT | Cliente duplicado na empresa |

### Exemplo

```json
{
  "errors": [
    {
      "message": "Invalid CNPJ",
      "extensions": {
        "code": "INVALID_CNPJ"
      }
    }
  ]
}
```

---

## Índices Recomendados

```sql
CREATE INDEX idx_clients_company
ON clients(fk_id_company);

CREATE INDEX idx_clients_cnpj
ON clients(cnpj_client);

CREATE INDEX idx_clients_email
ON clients(email_client);
```

---

## Testes

### Unitários

- ClientService
- Validação de CNPJ/Email
- Regras de multi-tenancy

### Integração

- GraphQL queries/mutations
- Autenticação JWT
- Isolamento de dados

```bash
mvn test
```

---

## Dependências

```xml
spring-boot-starter-graphql
spring-boot-starter-data-jpa
mysql-connector-j
```

---

## Status do Módulo

| Item          | Status |
|---------------|--------|
| CRUD Clientes | ✅      |
| Multi-Tenancy | ✅      |
| Validações    | ✅      |
| Soft Delete   | ✅      |
| Auditoria     | ✅      |

---

**Projeto:** Igniscore API  
**Módulo:** Clients  
**Tecnologias:** Spring Boot, GraphQL, JPA, MySQL  
**Status:** Produção