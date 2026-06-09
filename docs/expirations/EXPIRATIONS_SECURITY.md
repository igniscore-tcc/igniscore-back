Entendido — o conteúdo anterior acabou a descrever “Clients” no formato de segurança. Abaixo está a versão correta e consistente do **Guia de Segurança e Multi-Tenancy – Módulo Products**, alinhado ao mesmo padrão técnico.

---

# Guia de Segurança e Multi-Tenancy – Módulo Expirations

## Visão Geral

O módulo **Expirations** opera em arquitetura multi-tenant, garantindo isolamento completo de dados entre empresas.

Cada vencimento pertence obrigatoriamente a um único tenant (`companyId`) e todas as operações são executadas estritamente dentro desse contexto.

O módulo implementa:

* Isolamento por tenant (company-based isolation)
* RBAC (Role-Based Access Control)
* Autorização via JWT
* Soft delete (inativação)
* Auditoria de operações críticas
* Proteção contra OWASP Top 10

Nenhuma operação de vencimento pode ser executada fora do escopo da empresa autenticada.

---

## 1. Arquitetura Multi-Tenant

### Modelo adotado

**Shared Database + Shared Schema + Tenant Isolation**

Todos os registros de vencimentos possuem vínculo obrigatório com vendas:

```text id="p1m8qk"
companies
   └── sales (fk_id_company)
        └── expirations (fk_id_sale)
```

Relacionamento:

```sql id="c7xq2v"
sales.fk_id_company → companies.pk_id_company
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
public interface ExpirationRepository extends JpaRepository<Expiration, Integer> {

    @Query("""
            SELECT new com.igniscore.api.dto.expiration.ExpirationProjectionDTO(
                s.id,
                c.name,
                s.date,
                s.dueDate,
                s.total,
                e.status
            )
            FROM Expiration e
            JOIN e.sale s
            JOIN s.client c
            WHERE s.company.id = :companyId
            ORDER BY s.dueDate ASC
            """)
    List<ExpirationProjectionDTO> findExpirationsByCompanyId(
            @Param("companyId") Integer companyId
    );

    @Query("""
            SELECT new com.igniscore.api.dto.expiration.ExpirationProjectionDTO(
                s.id,
                c.name,
                s.date,
                s.dueDate,
                s.total,
                e.status
            )
            FROM Expiration e
            JOIN e.sale s
            JOIN s.client c
            WHERE s.company.id = :companyId
              AND s.dueDate BETWEEN :startDate AND :endDate
            ORDER BY s.dueDate ASC
            """)
    List<ExpirationProjectionDTO> findExpirationsByPeriod(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT new com.igniscore.api.dto.expiration.ExpirationProjectionDTO(
                s.id,
                c.name,
                s.date,
                s.dueDate,
                s.total,
                e.status
            )
            FROM Expiration e
            JOIN e.sale s
            JOIN s.client c
            WHERE s.company.id = :companyId
              AND s.dueDate BETWEEN :startDate AND :endDate
            ORDER BY s.dueDate ASC
            """)
    List<ExpirationProjectionDTO> findUpcomingExpirations(
            @Param("companyId") Integer companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT new com.igniscore.api.dto.expiration.ExpirationProjectionDTO(
                s.id,
                c.name,
                s.date,
                s.dueDate,
                s.total,
                e.status
            )
            FROM Expiration e
            JOIN e.sale s
            JOIN s.client c
            WHERE s.company.id = :companyId
              AND c.id = :clientId
            ORDER BY s.dueDate ASC
            """)
    List<ExpirationProjectionDTO> findExpirationsByClient(
            @Param("companyId") Integer companyId,
            @Param("clientId") Integer clientId
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
* status inicial = NORMAL

---

### READ (products, productById, activeProducts)

Todos os SELECTS são filtrados por tenant:

```java id="k3xq8m"
repository.findByCompanyId(companyId);
```

Regra crítica:

> Vencimentos de outra empresa nunca pode ser retornado, mesmo que o ‘ID’ exista.

---

## 7. Controle de Acesso (RBAC)

| Operação           | USER | MANAGER | ADMIN |
|--------------------|------|---------|-------|
| Listar vencimentos | ✅    | ✅       | ✅     |

---

## 8. Proteção contra OWASP Top 10

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

## 9. Rate Limiting

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

## 10. Testes de Segurança

### Isolamento

```text id="i9q1ld"
Empresa A nunca acessa vencimentos da Empresa B
```

---

### Autorização

* ‘USER’ não inativa
* ‘GESTOR’ e ADMIN inativam

---

## 11. Checklist de Produção

* [ ] JWT validado
* [ ] companyId nunca vindo do request
* [ ] filtros de tenant em todas queries
* [ ] RBAC aplicado
* [ ] rate limit ativo
* [ ] HTTPS obrigatório
* [ ] ‘logs’ sem dados sensíveis
* [ ] testes de isolamento aprovados
* [ ] backup configurado

---

## Garantias do Módulo

O módulo Expirations garante:

* Isolamento total entre empresas
* Controle rígido por tenant
* Proteção contra acesso cruzado
* Conformidade com OWASP Top 10
* Arquitetura segura por padrão
* Escalabilidade multi-tenant

---

**Módulo:** Expirations
**Projeto:** Igniscore API
**Arquitetura:** Spring Boot + GraphQL + JPA
**Modelo:** JWT + RBAC + Multi-Tenancy
**Status:** Produção
