# README Técnico – Módulo Clients (API GraphQL / Spring Boot)

## Visão Geral

O módulo **Clients** é responsável pela gestão de clientes vinculados às empresas que utilizam a plataforma Igniscore.

Cada cliente pertence obrigatoriamente a uma empresa e todas as operações respeitam o contexto multi-tenant da aplicação, garantindo que utilizadores acessem apenas os dados da empresa à qual estão vinculados.

O módulo fornece operações de cadastro, atualização e consulta de clientes através da API GraphQL.

### Objetivos

* Centralizar o cadastro de clientes por empresa
* Garantir isolamento de dados entre tenants
* Disponibilizar operações de consulta e atualização via GraphQL
* Manter integridade dos dados cadastrais
* Servir como base para módulos relacionados ao cliente

---

# Modelo de Dados

## Tabela `clients`

| Campo         | Tipo         | Descrição                    |
|---------------|--------------|------------------------------|
| pk_id_client  | INT          | Identificador único          |
| name_client   | VARCHAR(100) | Nome do cliente              |
| cnpj_client   | VARCHAR(18)  | CNPJ                         |
| email_client  | VARCHAR(255) | Email principal              |
| phone_client  | VARCHAR(20)  | Telefone                     |
| number_client | INT          | Número sequencial do cliente |
| ie_client     | VARCHAR(14)  | Inscrição Estadual           |
| uf_ie_client  | CHAR(2)      | UF da IE                     |
| cpf_client    | VARCHAR(14)  | CPF                          |
| obs_client    | TEXT         | Observações                  |
| fk_id_company | INT          | Empresa proprietária         |

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
ADD CONSTRAINT uq_client_per_company
UNIQUE (fk_id_company, number_client);
```

---

# Regras de Negócio

## Multi-Tenant

* Cada cliente pertence a apenas uma empresa
* Utilizadores acessam apenas clientes da própria empresa
* Clientes de empresas diferentes podem possuir dados semelhantes
* O número do cliente deve ser único dentro da empresa

## Cadastro

Para criação de um cliente são obrigatórios:

* Nome
* CNPJ
* Email

Campos opcionais:

* Telefone
* IE
* UF da IE
* CPF
* Observações

## Atualização

A atualização é realizada em formato parcial (‘PATCH’ semântico).

Apenas os campos enviados são alterados.

## Validações

* Nome obrigatório
* Email válido
* CNPJ válido
* CPF válido quando informado
* IE consistente com a UF quando informada

---

# Arquitetura

```text
GraphQL
   │
   ▼
ClientController
   │
   ▼
ClientService
   │
   ▼
ClientRepository
   │
   ▼
MySQL
```

---

# Estrutura do Projeto

```text
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

# Operações Disponíveis

## Query – Listar Clientes

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

### Parâmetros

| Campo | Tipo    |
|-------|---------|
| page  | Integer |
| size  | Integer |

### Retorno

Lista paginada de clientes da empresa autenticada.

---

## Query – Buscar Cliente por ID

```graphql
query {
  client(id: 1) {
    id
    name
    cnpj
    email
    phone
    number
  }
}
```

### Parâmetros

| Campo | Tipo    |
|-------|---------|
| id    | Integer |

### Retorno

Cliente pertencente à empresa autenticada.

---

## Mutation – Criar Cliente

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

### DTO

```java
public class ClientRegisterDTO {

    private String name;
    private String cnpj;
    private String email;

    private String phone;
    private String ie;
    private String ufIe;
    private String cpf;
    private String obs;
}
```

---

## Mutation – Atualizar Cliente

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

### DTO

```java
public class ClientUpdateDTO {

    private Integer id;

    private String name;
    private String cnpj;
    private String email;
    private String phone;
    private String ie;
    private String ufIe;
    private String cpf;
    private String obs;
}
```

---

# Segurança

Todas as operações devem respeitar o contexto da empresa autenticada.

Regra aplicada em todas as consultas:

```sql
WHERE clients.fk_id_company = :authenticatedCompanyId
```

## Exemplo

```java
public Client findById(Integer id) {

    Integer companyId =
        authService.getAuthenticatedCompany().getId();

    return repository.findById(id)
        .filter(client ->
            client.getCompany().getId().equals(companyId))
        .orElseThrow(() ->
            new EntityNotFoundException("Client not found"));
}
```

---

# Tratamento de Erros

| Código                  | Descrição                      |
|-------------------------|--------------------------------|
| CLIENT_NOT_FOUND        | Cliente não encontrado         |
| INVALID_CNPJ            | CNPJ inválido                  |
| INVALID_EMAIL           | Email inválido                 |
| INVALID_COMPANY         | Empresa inválida               |
| UNAUTHORIZED            | Cliente não pertence à empresa |
| DUPLICATE_CLIENT_NUMBER | Número já utilizado            |

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

# Índices Recomendados

```sql
CREATE INDEX idx_client_company
ON clients(fk_id_company);
```

```sql
CREATE INDEX idx_client_cnpj
ON clients(cnpj_client);
```

```sql
CREATE INDEX idx_client_email
ON clients(email_client);
```

---

# Testes

## Testes Unitários

### ClientService

* Criação de cliente
* Atualização de cliente
* Busca por ‘ID’
* Isolamento multi-tenant
* Validação de CNPJ
* Validação de Email

## Testes de Integração

### ClientController

* Queries GraphQL
* Mutations GraphQL
* Autenticação
* Autorização
* Paginação

### Execução

```bash
mvn test
```

---

# Dependências

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

---

# Status

| Item                    | Status |
|-------------------------|--------|
| Cadastro de clientes    | ✅      |
| Atualização de clientes | ✅      |
| Consulta por ID         | ✅      |
| Consulta paginada       | ✅      |
| Multi-tenancy           | ✅      |
| Validações              | ✅      |
| Soft Delete             | ❌      |
| Auditoria               | ✅      |
| Histórico de alterações | ❌      |
| Bulk Operations         | ❌      |

---

**Projeto:** Igniscore API
**Módulo:** Clients
**Tecnologias:** Spring Boot, GraphQL, JPA/Hibernate, MySQL
**Status do Documento:** Atualizado conforme implementação atual.
