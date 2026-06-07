# Guia de Segurança e Multi-Tenancy – Módulo Clients

## Visão Geral

O módulo **Clients** foi projetado para operar num ambiente multi-tenant, garantindo isolamento lógico entre empresas e impedindo qualquer acesso cruzado de dados.

Todo cliente pertence obrigatoriamente a uma única empresa e somente utilizadores autenticados vinculados a essa empresa podem visualizar ou manipular os seus registros.

Os princípios adotados são:

- Isolamento de dados por tenant
- Menor privilégio possível
- Autorização baseada em contexto
- Auditoria completa das operações
- Proteção contra vulnerabilidades OWASP
- Rastreabilidade de alterações
- Segurança por padrão (Secure by ‘Default’)

---

# Arquitetura de Multi-Tenancy

## Estratégia Utilizada

O sistema utiliza o modelo:

**Shared Database + Shared Schema + Tenant Isolation**

Todos os dados ficam armazenados no mesmo banco de dados, porém cada registro possui vínculo explícito com uma empresa.

```text
Company
   │
   ├── Client 1
   ├── Client 2
   ├── Client 3
   └── Client N
````

Relacionamento:

```sql
clients.fk_id_company
        ↓
companies.pk_id_company
```

Toda a operação realizada no módulo Clients considera automaticamente o tenant autenticado.

---

# Fluxo de Segurança

## Processo de Autenticação

```text
Request
   ↓
JWT Filter
   ↓
Validação do Token
   ↓
Recuperação do Usuário
   ↓
Recuperação da Empresa
   ↓
Criação do Contexto de Segurança
   ↓
Execução da Operação
```

O JWT contém:

```json
{
  "sub": "user@email.com",
  "userId": 10,
  "companyId": 3,
  "role": "ADMIN",
  "exp": 1780729200
}
```

O `companyId` extraído do ‘token’ torna-se a base de todas as validações do módulo.

---

# Contexto de Segurança

A empresa autenticada é carregada uma única vez durante a requisição.

Exemplo:

```bash
@Service
public class AuthenticatedUserService {

    public User getAuthenticatedUser() { ... }

    public Company getAuthenticatedCompany() { ... }

}
```

Uso:

```java
Company company =
    authenticatedUserService.getAuthenticatedCompany();
```

A partir desse momento todas as consultas são limitadas à empresa autenticada.

---

# Regra Fundamental de Isolamento

Nenhuma operação deve acessar clientes sem considerar o tenant.

## Correto

```bash
repository.findByIdAndCompanyId(
    clientId,
    companyId
);
```

## Incorreto

```bash
repository.findById(clientId);
```

O segundo exemplo pode permitir acesso indevido entre empresas.

---

# Repositórios Seguros

Todos os métodos do repository devem considerar o companyId.

```java
@Repository
public interface ClientRepository
        extends JpaRepository<Client, Integer> {

    Optional<Client> findByIdAndCompanyId(
        Integer id,
        Integer companyId
    );

    Optional<Client> findByCnpjAndCompanyId(
        String cnpj,
        Integer companyId
    );

    Page<Client> findByCompanyId(
        Integer companyId,
        Pageable pageable
    );

}
```

Métodos proibidos:

```java
findAll();

findById();

findByCnpj();
```

Sem filtro de tenant.

---

# Controle de Acesso

## Matriz de Permissões

| Operação            | USER | MANAGER | ADMIN |
|---------------------|------|---------|-------|
| Consultar clientes  | ✅    | ✅       | ✅     |
| Criar clientes      | ✅    | ✅       | ✅     |
| Atualizar clientes  | ✅    | ✅       | ✅     |
| Inativar clientes   | ❌    | ✅       | ✅     |
| Excluir clientes    | ❌    | ❌       | ✅     |
| Consultar auditoria | ❌    | ❌       | ✅     |

---

# Validações Obrigatórias

Todas as operações passam pelas seguintes validações:

```text
Usuário autenticado?
      ↓
Token válido?
      ↓
Empresa válida?
      ↓
Permissão suficiente?
      ↓
Cliente pertence à empresa?
      ↓
Validações de negócio?
      ↓
Executar operação
```

---

# Segurança por Operação

## CREATE

Validações:

* Utilizador autenticado
* Empresa ativa
* Nome obrigatório
* Email válido
* CNPJ válido
* CNPJ único dentro da empresa

Fluxo:

```text
Validar JWT
 ↓
Validar DTO
 ↓
Validar Tenant
 ↓
Validar CNPJ
 ↓
Persistir
 ↓
Auditar
```

---

## READ

Ao buscar um cliente:

```java
Client client =
    repository.findByIdAndCompanyId(
        id,
        companyId
    )
    .orElseThrow(
        ClientNotFoundException::new
    );
```

Mesmo que o ‘ID’ exista em outra empresa, o utilizador não terá acesso.

---

## UPDATE

Validações adicionais:

* Cliente pertence ao tenant
* Novo CNPJ não está duplicado
* Email continua válido
* Campos sensíveis são auditados

---

## DEACTIVATE

Validações:

```bash
if (!user.isAdmin()) {
    throw new ForbiddenException();
}
```

Após validação:

```bash
client.setActive(false);
client.setDeactivatedAt(LocalDateTime.now());
```

Nenhum dado é removido fisicamente.

---

# Proteção Contra Escalada de Privilégios

Um utilizador nunca pode informar o tenant manualmente.

Exemplo incorreto:

```bash
mutation {
  storeClient((
    companyId: 5
  )
}
```

O tenant sempre é obtido do contexto autenticado:

```java
Company company =
    authService.getAuthenticatedCompany();
```

Isso elimina tentativas de acesso cruzado.

---

# Tratamento Seguro de Erros

## Nunca Retornar

```json
{
  "message": "Client 123 belongs to Company ABC"
}
```

```json
{
  "message": "Foreign key fk_company violated"
}
```

```json
{
  "message": "SQLSyntaxErrorException ..."
}
```

---

## Sempre Retornar

```json
{
  "errors": [
    {
      "message": "Access denied",
      "extensions": {
        "code": "UNAUTHORIZED"
      }
    }
  ]
}
```

Ou:

```json
{
  "errors": [
    {
      "message": "Client not found"
    }
  ]
}
```

Sem exposição de detalhes internos.

---

# Auditoria

## Objetivos

Registrar:

* Quem executou
* Quando executou
* O que executou
* Em qual empresa
* Em qual cliente
* Endereço IP
* Resultado da operação

---

## Eventos Auditados

| Evento              | Auditoria |
|---------------------|-----------|
| CREATE_CLIENT       | Sim       |
| UPDATE_CLIENT       | Sim       |
| DEACTIVATE_CLIENT   | Sim       |
| DELETE_CLIENT       | Sim       |
| LOGIN               | Sim       |
| FAILED_LOGIN        | Sim       |
| UNAUTHORIZED_ACCESS | Sim       |

---

## Estrutura Recomendada

```sql
audit_logs
```

| Campo       | Tipo      |
|-------------|-----------|
| id          | BIGINT    |
| timestamp   | TIMESTAMP |
| user_id     | BIGINT    |
| company_id  | BIGINT    |
| action      | VARCHAR   |
| entity_type | VARCHAR   |
| entity_id   | BIGINT    |
| ip_address  | VARCHAR   |
| details     | TEXT      |

---

# Proteção Contra Vulnerabilidades OWASP

## Broken Access Control

Mitigação:

* Filtro obrigatório por companyId
* RBAC
* Contexto autenticado

---

## SQL Injection

Mitigação:

```bash
JpaRepository
```

e parâmetros bindados.

Nunca:

```bash
"SELECT * FROM clients WHERE id = " + id
```

---

## Authentication Failures

Mitigação:

* BCrypt
* JWT
* Expiração de token
* Refresh Token
* Bloqueio por tentativas excessivas

---

## Information Disclosure

Mitigação:

* Sem stack traces
* Sem SQL errors
* Sem IDs internos
* Sem nomes de tenants

---

## Security Misconfiguration

Mitigação:

* HTTPS obrigatório
* Secrets externos
* CORS restrito
* Headers de segurança

---

# Rate Limiting

Recomendado:

| Operação  | Limite  |
|-----------|---------|
| Login     | 10/min  |
| Queries   | 300/min |
| Mutations | 100/min |

Exemplo:

```http
429 TOO MANY REQUESTS
```

---

# Logs de Segurança

Registrar:

```text
Tentativas de acesso negado

Falhas de autenticação

Tokens inválidos

Operações administrativas

Alterações críticas

Desativações de clientes
```

Jamais registrar:

```text
Senhas

JWT completos

Dados financeiros

Informações sensíveis
```

---

# Testes de Segurança Obrigatórios

## Tenant Isolation

Validar:

```text
Empresa A
não consegue acessar
clientes da Empresa B
```

---

## Duplicate CNPJ

Validar:

```text
Mesmo CNPJ
permitido em empresas diferentes

Mesmo CNPJ
bloqueado dentro da mesma empresa
```

---

## Authorization

Validar:

```text
USER
não pode desativar cliente

ADMIN
pode desativar cliente
```

---

## Auditoria

Validar:

```text
Toda alteração gera log
```

---

# Checklist de Produção

Antes de publicar:

* [ ] JWT configurado com chave forte
* [ ] HTTPS obrigatório
* [ ] Auditoria habilitada
* [ ] Filtro de tenant aplicado em todas as queries
* [ ] Filtro de tenant aplicado em todas as mutations
* [ ] Rate limiting habilitado
* [ ] CORS configurado
* [ ] ‘Logs’ revistos
* [ ] Testes de isolamento executados
* [ ] Testes de autorização executados
* [ ] ‘Backup’ configurado
* [ ] Monitoramento configurado

---

# Garantias de Segurança do Módulo

O módulo Clients garante:

✅ Isolamento completo entre empresas

✅ Controle de acesso baseado em tenant

✅ Auditoria de operações críticas

✅ Proteção contra acesso cruzado

✅ Compatibilidade com OWASP Top 10

✅ Rastreabilidade de alterações

✅ Segurança baseada em JWT + RBAC

✅ Escalabilidade para múltiplos tenants

---

**Módulo:** Clients
**Projeto:** Igniscore API
**Arquitetura:** Spring Boot + GraphQL + JPA
**Modelo de Segurança:** JWT + RBAC + Multi-Tenancy
**Status:** Produção
