# Guia de Segurança e Multi-Tenancy – Módulo Sales

## Visão Geral

O módulo **Sales** foi projetado para operar num ambiente multi-tenant, garantindo isolamento lógico entre empresas e impedindo qualquer acesso cruzado de dados.

Toda venda pertence obrigatoriamente a uma única empresa e somente utilizadores autenticados vinculados a essa empresa podem visualizar ou manipular os seus registros.

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
   ├── Sale 1
   ├── Sale 2
   ├── Sale 3
   └── Sale N
```

Relacionamento:

```sql
sales.fk_id_company
        ↓
companies.pk_id_company
```

Toda operação realizada no módulo Sales considera automaticamente o tenant autenticado.

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

```java
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

Nenhuma operação deve acessar vendas sem considerar o tenant.

## Correto

```java
repository.findByIdAndCompanyId(
    saleId,
    companyId
);
```

## Incorreto

```java
repository.findById(saleId);
```

O segundo exemplo pode permitir acesso indevido entre empresas.

---

# Repositórios Seguros

Todos os métodos do repository devem considerar o companyId.

```java
@Repository
public interface SaleRepository
        extends JpaRepository<Sale, Integer> {

    Optional<Sale> findByIdAndCompanyId(
        Integer id,
        Integer companyId
    );

    Page<Sale> findByCompanyId(
        Integer companyId,
        Pageable pageable
    );

    Page<Sale> findByClientIdAndCompanyId(
        Integer clientId,
        Integer companyId,
        Pageable pageable
    );

}
```

Métodos proibidos:

```java
findAll();

findById();

findByClientId();
```

Sem filtro de tenant.

---

# Controle de Acesso

## Matriz de Permissões

| Operação           | USER | MANAGER | ADMIN |
|--------------------|------|---------|-------|
| Consultar vendas   | ✅    | ✅       | ✅     |
| Criar vendas       | ✅    | ✅       | ✅     |
| Atualizar vendas   | ✅    | ✅       | ✅     |
| Cancelar vendas    | ❌    | ✅       | ✅     |
| Excluir vendas     | ❌    | ❌       | ✅     |
| Consultar auditoria| ❌    | ❌       | ✅     |

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
Venda pertence à empresa?
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
* Cliente válido
* Cliente pertence à empresa
* Venda possui itens
* Produtos válidos
* Produtos pertencem à empresa

Fluxo:

```text
Validar JWT
 ↓
Validar DTO
 ↓
Validar Tenant
 ↓
Validar Cliente
 ↓
Validar Produtos
 ↓
Persistir
 ↓
Auditar
```

---

## READ

Ao buscar uma venda:

```java
Sale sale =
    repository.findByIdAndCompanyId(
        id,
        companyId
    )
    .orElseThrow(
        SaleNotFoundException::new
    );
```

Mesmo que o ‘ID’ exista em outra empresa, o utilizador não terá acesso.

---

## UPDATE

Validações adicionais:

* Venda pertence ao tenant
* Venda não está finalizada
* Dados enviados são válidos
* Campos sensíveis são auditados

---

## CANCEL

Validações:

```java
if (!user.isManager() && !user.isAdmin()) {
    throw new ForbiddenException();
}
```

Após validação:

```java
sale.setStatus(
    SaleStatus.CANCELLED
);
```

Nenhum dado é removido fisicamente.

---

# Proteção Contra Escalada de Privilégios

Um utilizador nunca pode informar o tenant manualmente.

Exemplo incorreto:

```graphql
mutation {
  createSale(
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
  "message": "Sale 123 belongs to Company ABC"
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
      "message": "Sale not found"
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
* Em qual venda
* Endereço IP
* Resultado da operação

---

## Eventos Auditados

| Evento              | Auditoria |
|---------------------|-----------|
| CREATE_SALE         | Sim       |
| UPDATE_SALE         | Sim       |
| CANCEL_SALE         | Sim       |
| DELETE_SALE         | Sim       |
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

```java
JpaRepository
```

e parâmetros bindados.

Nunca:

```java
"SELECT * FROM sales WHERE id = " + id
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

Cancelamentos de vendas
```

Jamais registrar:

```text
Senhas

JWT completos

Dados financeiros sensíveis

Informações confidenciais
```

---

# Testes de Segurança Obrigatórios

## Tenant Isolation

Validar:

```text
Empresa A
não consegue acessar
vendas da Empresa B
```

---

## Authorization

Validar:

```text
USER
não pode cancelar venda

MANAGER
pode cancelar venda

ADMIN
pode cancelar venda
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

O módulo Sales garante:

✅ Isolamento completo entre empresas

✅ Controle de acesso baseado em tenant

✅ Auditoria de operações críticas

✅ Proteção contra acesso cruzado

✅ Compatibilidade com OWASP Top 10

✅ Rastreabilidade de alterações

✅ Segurança baseada em JWT + RBAC

✅ Escalabilidade para múltiplos tenants

---

**Módulo:** Sales  
**Projeto:** Igniscore API  
**Arquitetura:** Spring Boot + GraphQL + JPA  
**Modelo de Segurança:** JWT + RBAC + Multi-Tenancy  
**Status:** Produção