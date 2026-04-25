# README Técnico – Módulo Clients (API GraphQL / Spring Boot)

## Visão Geral

O módulo **clients** é responsável pela gestão de clientes pertencentes a empresas (tenants) que utilizam o sistema Igniscore. Cada cliente está estritamente associado a uma empresa, sendo inacessível fora desse escopo.

O sistema não gerencia dados de inadimplência externa ou regras de negócio financeiras do cliente final da empresa usuária. Ele apenas armazena e organiza informações operacionais fornecidas pela empresa contratante.

### Objetivos Principais
- Centralizar a gestão de clientes por empresa (tenant)
- Garantir isolamento de dados entre empresas
- Fornecer operações CRUD via GraphQL
- Manter histórico e rastreabilidade de clientes
- Integrar-se com módulos de vendas, contatos e endereços

---

## Modelo de Dados

### Tabela: `clients`

| Campo | Tipo | Constraint | Descrição |
|-------|------|-----------|-----------|
| `pk_id_client` | INT | PK, Auto-increment | Identificador único do cliente |
| `name_client` | VARCHAR(100) | NOT NULL | Nome do cliente/razão social |
| `cnpj_client` | VARCHAR(18) | NOT NULL | CNPJ do cliente (formato: XX.XXX.XXX/XXXX-XX) |
| `email_client` | VARCHAR(255) | NOT NULL | Email de contato principal |
| `phone_client` | VARCHAR(20) | - | Telefone para contato |
| `number_client` | INT | NOT NULL, UNIQUE* | Número sequencial do cliente na empresa |
| `ie_client` | VARCHAR(14) | - | Inscrição Estadual |
| `uf_ie_client` | CHAR(2) | - | UF da Inscrição Estadual |
| `cpf_client` | VARCHAR(14) | - | CPF (para clientes pessoas físicas) |
| `obs_client` | TEXT | - | Observações gerais ou notas internas |
| `fk_id_company` | INT | FK, NOT NULL | Empresa proprietária do cliente |

*Restrição de Unicidade Composta: `UNIQUE (fk_id_company, number_client)`

### Constraints e Relacionamentos

```sql
ALTER TABLE clients ADD CONSTRAINT fk_clients_company
    FOREIGN KEY (fk_id_company) REFERENCES companies(pk_id_company)
    ON DELETE CASCADE;

ALTER TABLE clients ADD CONSTRAINT uq_client_per_company
    UNIQUE (fk_id_company, number_client);
```

---

## Regras de Negócio

### Isolamento Multi-Tenant
- ✅ Cada cliente pertence exclusivamente a uma empresa
- ✅ Clientes podem ter dados duplicados entre empresas diferentes
- ✅ Dentro de uma mesma empresa, `number_client` deve ser único
- ✅ Ao deletar uma empresa, todos seus clientes são deletados em cascata

### Ciclo de Vida do Cliente
- **Criação**: Requer nome, CNPJ, email e empresa válida
- **Atualização**: Apenas campos fornecidos são atualizados (PATCH semântico)
- **Inativação**: Clientes podem ser marcados como inativos sem exclusão física
- **Deleção**: Apenas usuários com permissão podem deletar clientes (com auditoria)

### Acesso e Autorização
- ✅ O cliente **não possui acesso** ao sistema
- ✅ Apenas usuários autenticados (proprietários e funcionários) da empresa podem acessar seus clientes
- ✅ Nenhum usuário pode acessar clientes de outra empresa
- ✅ Controle de acesso baseado em contexto de empresa (tenant ID)

### Validações
- CNPJ deve ser válido (formato e dígitos verificadores)
- Email deve estar em formato válido
- CPF (se fornecido) deve ser válido
- IE deve ser consistente com a UF informada
- `number_client` é auto-gerado e imutável por empresa

---

## Arquitetura da Camada de Clients

```
┌─────────────────────────────────────┐
│      GraphQL Schema (schema.graphqls)│
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  ClientController (Spring GraphQL)  │
│  - storeClient (Mutation)           │
│  - updateClient (Mutation)          │
│  - clients (Query - paginado)       │
│  - clientById (Query)               │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│    ClientService (Business Layer)   │
│  - store(ClientRegisterDTO)         │
│  - update(ClientUpdateDTO)          │
│  - findById(Integer)                │
│  - findByCompany(Integer, Pageable) │
│  - deactivate(Integer)              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  ClientRepository (Spring Data JPA) │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Database (clients table)     │
└─────────────────────────────────────┘
```

---

## Operações Disponíveis (GraphQL)

### Queries

#### 1. Listar Clientes (Paginado)
```graphql
query {
  clients(page: 0, size: 10) {
    id
    name
    cnpj
    email
    phone
    number
    ie
    uf_ie
    cpf
    obs
    company {
      id
      name
    }
  }
}
```

**Retorno**: Lista paginada de clientes da empresa autenticada

**Parâmetros**:
- `page` (Int, opcional): Número da página (default: 0)
- `size` (Int, opcional): Quantidade de registros por página (default: 20)

---

#### 2. Buscar Cliente por ID
```graphql
query {
  clientById(id: 1) {
    id
    name
    cnpj
    email
    phone
    number
    ie
    uf_ie
    cpf
    obs
    company {
      id
      name
    }
  }
}
```

**Retorno**: Dados completos do cliente (se existir e pertencer à empresa autenticada)

**Parâmetros**:
- `id` (Int, obrigatório): Identificador único do cliente

---

### Mutations

#### 1. Criar Cliente
```graphql
mutation {
  storeClient(input: {
    name: "Cliente XYZ Ltda."
    cnpj: "12.345.678/0001-99"
    email: "contato@clientexyz.com.br"
    phone: "(11) 3000-0000"
    ie: "123.456.789.012"
    uf_ie: "SP"
    cpf: "123.456.789-10"
    obs: "Cliente preferencial - Desconto de 5%"
  }) {
    id
    name
    cnpj
    number
    email
  }
}
```

**DTO**: `ClientRegisterDTO`
```java
{
  name: String (required)
  cnpj: String (required)
  email: String (required)
  phone: String (optional)
  ie: String (optional)
  uf_ie: String (optional)
  cpf: String (optional)
  obs: String (optional)
}
```

**Validações**:
- ✅ Nome não pode estar vazio
- ✅ CNPJ deve ser válido e único dentro da empresa
- ✅ Email deve estar em formato válido
- ✅ Empresa deve existir e estar ativa

**Retorno**: Objeto `Client` completo criado

---

#### 2. Atualizar Cliente
```graphql
mutation {
  updateClient(input: {
    id: 1
    name: "Cliente XYZ Ltda. - Atualizado"
    email: "novo@clientexyz.com.br"
    phone: "(11) 3000-0001"
  }) {
    id
    name
    email
    phone
    updatedAt
  }
}
```

**DTO**: `ClientUpdateDTO`
```java
{
  id: Integer (required)
  name: String (optional)
  cnpj: String (optional)
  email: String (optional)
  phone: String (optional)
  ie: String (optional)
  uf_ie: String (optional)
  cpf: String (optional)
  obs: String (optional)
}
```

**Comportamento**: Apenas campos fornecidos são atualizados (PATCH semântico)

**Validações**:
- ✅ Cliente deve existir
- ✅ Cliente deve pertencer à empresa autenticada
- ✅ CNPJ atualizado deve ser único (se modificado)
- ✅ Email atualizado deve ser válido (se modificado)

**Retorno**: Objeto `Client` atualizado

---

#### 3. Inativar Cliente
```graphql
mutation {
  deactivateClient(id: 1) {
    id
    name
    isActive
  }
}
```

**Comportamento**: Marca o cliente como inativo sem deletar do banco

**Validações**:
- ✅ Cliente deve existir
- ✅ Cliente deve pertencer à empresa autenticada

**Retorno**: Objeto `Client` com status atualizado

---

## Estrutura de Código

### Localizações

```
src/main/java/com/igniscore/api/
├── model/
│   └── Client.java              # Entidade JPA
├── dto/
│   ├── ClientRegisterDTO.java   # DTO para criação
│   └── ClientUpdateDTO.java     # DTO para atualização
├── repository/
│   └── ClientRepository.java    # Acesso a dados
├── service/
│   └── ClientService.java       # Lógica de negócio
├── controller/
│   └── ClientController.java    # Camada GraphQL
└── utils/
    └── ClientValidator.java     # Validações específicas

src/main/resources/
└── graphql/
    └── schema.graphqls          # Definições GraphQL
```

### Classes Principais

#### `Client.java`
Entity JPA que representa um cliente no banco de dados.

```java
@Entity
@Table(
  name = "clients",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uq_client_per_company",
      columnNames = {"fk_id_company", "number_client"}
    )
  }
)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    private String cnpj;
    private String email;
    private String phone;
    private Integer number;  // Auto-gerado
    private String ie;
    private String uf_ie;
    private String cpf;
    private String obs;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_id_company")
    private Company company;
    
    // getters, setters, e métodos auxiliares
}
```

#### `ClientService.java`
Encapsula toda lógica de negócio relacionada a clientes.

```java
@Service
public class ClientService {
    
    private final ClientRepository repository;
    private final AuthenticatedUserService authService;
    private final CompanyService companyService;
    
    // Criar cliente
    public Client store(ClientRegisterDTO input) { ... }
    
    // Atualizar cliente
    public Client update(ClientUpdateDTO input) { ... }
    
    // Buscar por ID (com validação de tenant)
    public Client findById(Integer id) { ... }
    
    // Listar por empresa (paginado)
    public Page<Client> findByCompany(Integer companyId, Pageable pageable) { ... }
    
    // Inativar cliente
    public Client deactivate(Integer id) { ... }
}
```

#### `ClientController.java`
Controller GraphQL que expõe operações para clientes.

```java
@Controller
public class ClientController {
    
    private final ClientService service;
    
    @MutationMapping
    public Client storeClient(@Argument ClientRegisterDTO input) { ... }
    
    @MutationMapping
    public Client updateClient(@Argument ClientUpdateDTO input) { ... }
    
    @QueryMapping
    public List<Client> clients(@Argument Integer page, @Argument Integer size) { ... }
    
    @QueryMapping
    public Client clientById(@Argument Integer id) { ... }
}
```

---

## Integrações

### Módulo Sales (Vendas)
- **Relação**: Um cliente pode ter múltiplas vendas associadas
- **Operação**: Listar vendas de um cliente específico
- **Endpoint**: Via query `salesByClientId` do módulo Sales

### Módulo Contacts (Contatos)
- **Relação**: Um cliente pode ter múltiplos contatos registrados
- **Operação**: Associar contatos a clientes
- **Endpoint**: Via mutation `addContactToClient` do módulo Contacts

### Módulo Addresses (Endereços)
- **Relação**: Um cliente pode ter múltiplos endereços (comercial, residencial, etc.)
- **Operação**: Registrar endereços do cliente
- **Endpoint**: Via mutation `addAddressToClient` do módulo Addresses

### Módulo Company (Empresas)
- **Relação**: Relacionamento mandatory - cada cliente pertence a uma empresa
- **Operação**: Validar existência e atividade da empresa proprietária
- **Comportamento**: Deleção em cascata quando empresa é deletada

### Módulo Auth (Autenticação)
- **Relação**: Validação de permissão do usuário para acessar cliente
- **Operação**: Verificar se usuário pertence à empresa do cliente
- **Filtro**: Todas as queries aplicam filtro automático de `company_id`

---

## Segurança e Multi-tenancy

### Isolamento de Dados
```
FILTERING RULE:
WHERE clients.fk_id_company = :authenticatedUserCompanyId
```

Toda query ou mutation aplicará automaticamente este filtro baseado no contexto de autenticação.

### Validações de Segurança

| Operação | Validação | Erro |
|----------|-----------|------|
| **CREATE** | Empresa deve existir e ser da empresa do usuário | `INVALID_COMPANY` |
| **READ** | Cliente deve pertencer à empresa do usuário | `UNAUTHORIZED` |
| **UPDATE** | Usuário deve ser da mesma empresa do cliente | `UNAUTHORIZED` |
| **DELETE** | Apenas administradores ou proprietários | `FORBIDDEN` |

### Implementação da Segurança

```java
@Service
public class ClientService {
    
    private final AuthenticatedUserService authService;
    
    private Integer getAuthenticatedCompanyId() {
        return authService.getAuthenticatedCompany().getId();
    }
    
    private void validateOwnership(Client client) {
        Integer authCompanyId = getAuthenticatedCompanyId();
        if (!client.getCompany().getId().equals(authCompanyId)) {
            throw new UnauthorizedException("Client does not belong to your company");
        }
    }
    
    public Client findById(Integer id) {
        return repository.findById(id)
            .filter(c -> c.getCompany().getId().equals(getAuthenticatedCompanyId()))
            .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }
}
```

### Auditoria
- ✅ Registrar quem criou o cliente e quando
- ✅ Registrar atualizações com timestamp
- ✅ Não revelar informações de clientes de outras empresas em erros

---

## Tratamento de Erros

| Código de Erro | HTTP Status | Descrição |
|---|---|---|
| `CLIENT_NOT_FOUND` | 404 | Cliente com ID especificado não existe |
| `INVALID_CNPJ` | 400 | CNPJ inválido ou já existe nesta empresa |
| `INVALID_EMAIL` | 400 | Formato de email inválido |
| `INVALID_COMPANY` | 400 | Empresa não existe ou não pertence ao usuário |
| `UNAUTHORIZED` | 403 | Usuário não autorizado a acessar este cliente |
| `DUPLICATE_CLIENT_NUMBER` | 409 | Número de cliente já existe nesta empresa |

**Exemplo de resposta de erro GraphQL:**
```json
{
  "errors": [
    {
      "message": "Invalid CNPJ format",
      "extensions": {
        "code": "INVALID_CNPJ",
        "timestamp": "2026-04-25T10:30:00Z"
      }
    }
  ]
}
```

---

## Exemplos de Uso

### Exemplo 1: Criar um novo cliente

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "query": "mutation { storeClient(input: { name: \"Acme Corp\", cnpj: \"12.345.678/0001-99\", email: \"contato@acme.com\" }) { id name cnpj } }"
  }'
```

**Resposta de sucesso (200):**
```json
{
  "data": {
    "storeClient": {
      "id": 42,
      "name": "Acme Corp",
      "cnpj": "12.345.678/0001-99"
    }
  }
}
```

### Exemplo 2: Listar clientes com paginação

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "query": "query { clients(page: 0, size: 5) { id name cnpj email } }"
  }'
```

### Exemplo 3: Atualizar cliente

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "query": "mutation { updateClient(input: { id: 42, phone: \"(11) 9999-9999\" }) { id name phone } }"
  }'
```

---

## Performance e Otimizações

### Índices Recomendados

```sql
-- Índice na foreign key (usado em filtros tenant)
CREATE INDEX idx_client_company ON clients(fk_id_company);

-- Índice na composite unique constraint (já criado automaticamente)
-- CREATE INDEX idx_client_number_company ON clients(fk_id_company, number_client);

-- Índice em CNPJ para buscas rápidas
CREATE INDEX idx_client_cnpj ON clients(cnpj);

-- Índice em email para validação de duplicatas
CREATE INDEX idx_client_email ON clients(email);
```

### Paginação
- Sempre use paginação ao listar clientes (máximo 100 registros por página)
- Implemente cursor-based pagination para grandes datasets

### Cache
- Cache de cliente ativo por 5 minutos (cache invalidation na atualização)
- Cache de lista paginada por 2 minutos

---

## Configuração e Deployment

### Variáveis de Ambiente

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/igniscore
SPRING_DATASOURCE_USERNAME=igniscore_user
SPRING_DATASOURCE_PASSWORD=secure_password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# GraphQL
GRAPHQL_SERVLET_ENABLED=true
GRAPHQL_SERVLET_PATH=/graphql
```

### Dependências Maven

```xml
<!-- JPA/Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- GraphQL -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>

<!-- MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

---

## Testes

### Testes Unitários (ClientService)
- ✅ Teste de criação com validações
- ✅ Teste de atualização parcial
- ✅ Teste de isolamento de tenant
- ✅ Teste de edge cases (CNPJ duplicado, email inválido)

### Testes de Integração (ClientController)
- ✅ Teste de endpoint GraphQL completo
- ✅ Teste de autenticação e autorização
- ✅ Teste de paginação

**Executar testes:**
```bash
mvn test -Dtest=ClientServiceTest
mvn test -Dtest=ClientControllerTest
```

---

## Roadmap Futuro

- [ ] Soft delete com temporal queries
- [ ] Histórico de alterações (audit log)
- [ ] API REST adicional (além de GraphQL)
- [ ] Integração com webhooks (notificar quando cliente é criado/atualizado)
- [ ] Busca full-text em clientes
- [ ] Exportação em CSV/PDF
- [ ] Bulk operations (criar/atualizar múltiplos clientes)

---

## Contribuindo

1. Siga as convenções de código do projeto
2. Faça testes para novas funcionalidades
3. Atualize este README se adicionar operações
4. Valide ISO 8601 para datas e timezones UTC

---

## Referências

- **Especificação GraphQL**: [graphql.org](https://graphql.org)
- **Spring GraphQL**: [spring.io/projects/spring-graphql](https://spring.io/projects/spring-graphql)
- **JPA/Hibernate**: [hibernate.org](https://hibernate.org)
- **Validação no Spring**: [spring.io/guides/gs/validating-form-input/](https://spring.io/guides/gs/validating-form-input/)

---

## Suporte

Dúvidas ou problemas? Entre em contato com:
- **Desenvolvedor**: [Seu Nome]
- **Email**: dev@igniscore.com
- **Issue Tracker**: GitHub Issues do projeto

---

**Última Atualização**: 25 de abril de 2026  
**Versão do Documento**: 1.0  
**Status**: Produção
