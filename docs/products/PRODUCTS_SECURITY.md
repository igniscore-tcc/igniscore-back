Entendido — o conteúdo anterior acabou a descrever “Clients” no formato de segurança. Abaixo está a versão correta e consistente do **Guia de Segurança e Multi-Tenancy – Módulo Products**, alinhado ao mesmo padrão técnico.

---

# Guia de Segurança e Multi-Tenancy – Módulo Products

## Visão Geral

O módulo **Products** opera em arquitetura multi-tenant, garantindo isolamento completo de dados entre empresas.

Cada produto pertence obrigatoriamente a um único tenant (`companyId`) e todas as operações são executadas estritamente dentro desse contexto.

O módulo implementa:

* Isolamento por tenant (company-based isolation)
* RBAC (Role-Based Access Control)
* Autorização via JWT
* Soft delete (inativação)
* Auditoria de operações críticas
* Proteção contra OWASP Top 10

Nenhuma operação de produto pode ser executada fora do escopo da empresa autenticada.

---

## 1. Arquitetura Multi-Tenant

### Modelo adotado

**Shared Database + Shared Schema + Tenant Isolation**

Todos os registros de produtos possuem vínculo obrigatório com empresa:

```text id="p1m8qk"
companies
   └── products (fk_id_company)
```

Relacionamento:

```sql id="c7xq2v"
products.fk_id_company → companies.pk_id_company
```

---

### Regra estrutural obrigatória

Toda a operação deve aplicar filtro por tenant:

```sql id="9m3kax"
fk_id_company = :authenticatedCompanyId
```

Não existe acesso a produto sem contexto de empresa.

---

## 2. Fluxo de Autenticação e Contexto

### Pipeline

```text id="h2q9vd"
Request
  ↓
JWT Filter
  ↓
Validação do token
  ↓
Extração de claims (userId, companyId, role)
  ↓
Criação do Security Context
  ↓
Execução da operação com tenant fixo
```

---

### Estrutura do JWT

```json id="x8k3p2"
{
  "sub": "user@email.com",
  "userId": 10,
  "companyId": 3,
  "role": "ADMIN",
  "exp": 1780729200
}
```

O `companyId` define o escopo absoluto de acesso.

---

## 3. Contexto de Segurança

O tenant é resolvido uma única vez por requisição:

```java id="q9v1lm"
Company company = authenticatedUserService.getAuthenticatedCompany();
```

Regras:

* Não pode ser sobrescrito pelo cliente
* Não pode ser passado via GraphQL/REST
* Não pode ser inferido de payload externo

---

## 4. Regra de Isolamento (Core Rule)

### Correto

```java id="f2k8aa"
repository.findByIdAndCompanyId(id, companyId);
```

### Incorreto

```java id="r8n1zx"
repository.findById(id);
```

Qualquer operação sem filtro de tenant é considerada vulnerabilidade crítica.

---

## 5. Repositório Seguro

Todos os acessos devem ser tenant-aware:

```java id="v5k1dq"
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByIdAndCompanyId(Integer id, Integer companyId);

    Page<Product> findByCompanyId(Integer companyId, Pageable pageable);

    Page<Product> findByCompanyIdAndStatus(
        Integer companyId,
        Boolean status,
        Pageable pageable
    );
}
```

### Métodos proibidos

* findAll()
* findById()
* Queries sem companyId

---

## 6. Segurança por Operação

### CREATE (storeProduct)

Fluxo:

```text id="c1p9xz"
JWT válido
→ validação DTO
→ companyId extraído do token
→ validação de negócio
→ persistência
→ auditoria
```

Regras:

* companyId nunca vem do request
* status inicial = true
* type deve pertencer ao enum ProductType
* price >= 0
* validade obrigatória

---

### READ (products, productById, activeProducts)

Todos os SELECTS são filtrados por tenant:

```java id="k3xq8m"
repository.findByCompanyId(companyId, pageable);
```

Regra crítica:

> Produto de outra empresa nunca pode ser retornado, mesmo que o ‘ID’ exista.

---

### UPDATE (updateProduct)

Validações obrigatórias:

* Produto pertence ao tenant
* Campos são atualizados parcialmente (‘PATCH’ semântico)
* updated_at atualizado automaticamente
* companyId não pode ser alterado

```java id="z8m2qp"
if (!product.getCompanyId().equals(companyId)) {
    throw new ForbiddenException();
}
```

---

### DEACTIVATE (soft delete)

Estratégia:

* Não remove do banco
* Apenas altera status

```java id="d8k1sn"
product.setStatus(false);
product.setUpdatedAt(LocalDateTime.now());
```

Regras:

* Produto inativo não pode ser vendido
* Produto inativo não pode ser alugado
* Pode ser reativado via ‘update’

---

## 7. Controle de Acesso (RBAC)

| Operação        | USER | MANAGER | ADMIN |
|-----------------|------|---------|-------|
| Listar produtos | ✅    | ✅       | ✅     |
| Criar produto   | ✅    | ✅       | ✅     |
| Atualizar       | ✅    | ✅       | ✅     |
| Inativar        | ❌    | ✅       | ✅     |
| Excluir físico  | ❌    | ❌       | ❌     |
| Auditoria       | ❌    | ❌       | ✅     |

---

## 8. Proteção contra Escalada de Privilégios

### Regra crítica

O cliente nunca define empresa.

### Incorreto

```graphql id="m2k9qp"
storeProduct(companyId: 999)
```

### Correto

```java id="t7x2ld"
companyId = authService.getAuthenticatedCompany().getId();
```

---

## 9. Tratamento Seguro de Erros

### Resposta correta

```json id="p9x1aa"
{
  "errors": [
    {
      "message": "Product not found",
      "extensions": {
        "code": "NOT_FOUND"
      }
    }
  ]
}
```

### Nunca expor

* fk_id_company de outros tenants
* SQL errors
* stack traces
* validações internas
* IDs de outras empresas

---

## 10. Auditoria

### Eventos

* CREATE_PRODUCT
* UPDATE_PRODUCT
* DEACTIVATE_PRODUCT
* LOGIN
* UNAUTHORIZED_ACCESS

---

### Estrutura

```sql id="a1v9mp"
audit_logs (
  id,
  timestamp,
  user_id,
  company_id,
  action,
  entity_type,
  entity_id,
  ip_address,
  details
)
```

---

## 11. Proteção contra OWASP Top 10

### Broken Access Control

* Filtro obrigatório por companyId
* validação dupla (service + repository)

### SQL Injection

* JPA + queries parametrizadas
* nenhuma concatenação de ‘string’

### Authentication Failures

* JWT com expiração
* BCrypt para senhas

### Information Disclosure

* sem stacktrace
* sem erros internos
* sem metadata de tenant

### Security Misconfiguration

* HTTPS obrigatório
* CORS restrito
* secrets externos ao código

---

## 12. Rate Limiting

| Operação | Limite  |
|----------|---------|
| Login    | 10/min  |
| Query    | 300/min |
| Mutation | 100/min |

Resposta:

```http id="r3m8kk"
429 TOO MANY REQUESTS
```

---

## 13. Testes de Segurança

### Isolamento

```text id="i9q1ld"
Empresa A nunca acessa produtos da Empresa B
```

---

### Produto inativo

* Não pode ser vendido
* Não pode ser alugado

---

### Autorização

* ‘USER’ não inativa
* ‘GESTOR’ e ADMIN inativam

---

### Auditoria

* Toda mutation gera ‘log’ obrigatório

---

## 14. Checklist de Produção

* [ ] JWT validado
* [ ] companyId nunca vindo do request
* [ ] filtros de tenant em todas queries
* [ ] RBAC aplicado
* [ ] auditoria ativa
* [ ] rate limit ativo
* [ ] HTTPS obrigatório
* [ ] ‘logs’ sem dados sensíveis
* [ ] testes de isolamento aprovados
* [ ] backup configurado

---

## Garantias do Módulo

O módulo Products garante:

* Isolamento total entre empresas
* Controle rígido por tenant
* Proteção contra acesso cruzado
* Auditoria completa de operações
* Conformidade com OWASP Top 10
* Arquitetura segura por padrão
* Escalabilidade multi-tenant

---

**Módulo:** Products
**Projeto:** Igniscore API
**Arquitetura:** Spring Boot + GraphQL + JPA
**Modelo:** JWT + RBAC + Multi-Tenancy
**Status:** Produção
