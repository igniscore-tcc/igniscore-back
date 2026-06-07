# Guia de Segurança e Multi-Tenancy – Módulo Clients

## 1. Introdução

O módulo Clients implementa um sistema rigoroso de isolamento de dados multi-tenant, garantindo que cada empresa (tenant) acesse apenas seus próprios clientes. Este documento descreve as estratégias, implementações e boas práticas de segurança.

---

## 2. Arquitetura Multi-Tenant

### 2.1 Modelo de Isolamento

O sistema utiliza um modelo **database-per-schema** com isolamento por tabela:

```
┌─────────────────────────────────────────────────┐
│           Database Igniscore (Única)            │
├─────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────┐   │
│  │ Tabela: companies                       │   │
│  │ ├─ Empresa A (id: 1)                   │   │
│  │ ├─ Empresa B (id: 2)                   │   │
│  │ └─ Empresa C (id: 3)                   │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │ Tabela: clients                         │   │
│  │ ├─ Cliente A1 (fk_company: 1)          │   │
│  │ ├─ Cliente A2 (fk_company: 1)          │   │
│  │ ├─ Cliente B1 (fk_company: 2)          │   │
│  │ └─ Cliente B2 (fk_company: 2)          │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │ Tabela: users                           │   │
│  │ ├─ Admin A1 (fk_company: 1)            │   │
│  │ ├─ User A1 (fk_company: 1)             │   │
│  │ ├─ Admin B1 (fk_company: 2)            │   │
│  │ └─ User B1 (fk_company: 2)             │   │
│  └─────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

**Vantagens deste modelo:**
- Simplicidade operacional (um único banco de dados)
- Baixa complexidade de backup e disaster recovery
- Isolamento lógico eficiente
- Facilita migrações e escalabilidade

---

## 3. Fluxo de Autenticação e Autorização

### 3.1 Fluxo Geral

```
┌─────────────┐
│ Requisição  │
│  GraphQL    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────┐
│ SecurityFilter                  │
│ ├─ Extrai JWT do header         │
│ └─ Valida assinatura            │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│ JwtService                      │
│ ├─ Parse token                  │
│ └─ Extrai userId e companyId    │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│ AuthenticatedUserService        │
│ ├─ Recupera user do contexto    │
│ ├─ Recupera company do contexto │
│ └─ Valida permissões (RBAC)     │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│ ClientController / Service      │
│ ├─ Aplica filtros (tenant ID)   │
│ ├─ Executa operação             │
│ └─ Retorna resultado            │
└─────────────────────────────────┘
```

---

### 3.2 Implementação do Contexto de Segurança

```java
// AuthenticatedUserService.java
@Service
public class AuthenticatedUserService {
    
    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();
    private static final ThreadLocal<Company> CURRENT_COMPANY = new ThreadLocal<>();
    
    // Armazena contexto de autenticação
    public void setAuthenticatedUser(User user) {
        CURRENT_USER.set(user);
        CURRENT_COMPANY.set(user.getCompany());
    }
    
    // Recupera usuário autenticado
    public User getAuthenticatedUser() {
        User user = CURRENT_USER.get();
        if (user == null) {
            throw new UnauthorizedException("No authenticated user found");
        }
        return user;
    }
    
    // Recupera empresa do usuário autenticado
    public Company getAuthenticatedCompany() {
        Company company = CURRENT_COMPANY.get();
        if (company == null) {
            throw new UnauthorizedException("No company context found");
        }
        return company;
    }
    
    // Limpa contexto após requisição
    public void clearContext() {
        CURRENT_USER.remove();
        CURRENT_COMPANY.remove();
    }
}
```

---

## 4. Filtragem de Clientes por Tenant

### 4.1 Padrão de Filtragem

Toda query ou mutation que acessa clientes **DEVE** aplicar filtro de tenant:

```java
// Padrão CORRETO
@Service
public class ClientService {
    
    private final ClientRepository repository;
    private final AuthenticatedUserService authService;
    
    // CORRETO: Filtra por company_id
    public Client findById(Integer id) {
        Integer companyId = authService.getAuthenticatedCompany().getId();
        
        return repository.findByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    }
    
    // CORRETO: Lista apenas clientes da empresa
    public Page<Client> listByCompany(Pageable pageable) {
        Integer companyId = authService.getAuthenticatedCompany().getId();
        
        return repository.findByCompanyId(companyId, pageable);
    }
}
```

### 4.2 Repository com Queries Seguras

```java
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    
    // SEGURA: Filtra por company_id
    Optional<Client> findByIdAndCompanyId(Integer id, Integer companyId);
    
    // SEGURA: Lista com filtro de company
    Page<Client> findByCompanyId(Integer companyId, Pageable pageable);
    
    // SEGURA: Busca por CNPJ com filtro de company
    Optional<Client> findByCnpjAndCompanyId(String cnpj, Integer companyId);
    
    // INSEGURA: Não deveria existir
    // Optional<Client> findById(Integer id);  // Sem filtro de company!
    
    // INSEGURA: Não deveria existir
    // List<Client> findAll();  // Retorna TODOS os clientes!
}
```

---

## 5. Validações de Segurança

### 5.1 Checklist de Validação

Toda operação CRUD passa por este checklist:

```
┌─────────────────────────────────────────────┐
│ CHECKLIST DE SEGURANÇA PARA CADA OPERAÇÃO   │
├─────────────────────────────────────────────┤
│ ☑ Usuário está autenticado?                 │
│ ☑ Token JWT é válido?                       │
│ ☑ Usuário tem permissão para operação?      │
│ ☑ Cliente pertence à empresa do usuário?    │
│ ☑ Validações de negócio foram aplicadas?    │
│ ☑ Operação foi registrada (audit log)?      │
└─────────────────────────────────────────────┘
```

### 5.2 Validações por Operação

#### CREATE (storeClient)
```java
@Service
public class ClientService {
    
    public Client store(ClientRegisterDTO input) {
        // 1. Validar autenticação
        User user = authService.getAuthenticatedUser();
        Company company = authService.getAuthenticatedCompany();
        
        // 2. Validar entrada
        if (input.getName() == null || input.getName().isBlank()) {
            throw new ValidationException("Client name is required");
        }
        
        // 3. Validar CNPJ
        if (!isValidCNPJ(input.getCnpj())) {
            throw new ValidationException("Invalid CNPJ format");
        }
        
        // 4. Validar unicidade por empresa
        if (repository.findByCnpjAndCompanyId(input.getCnpj(), company.getId()).isPresent()) {
            throw new DuplicateClientException("CNPJ already exists in this company");
        }
        
        // 5. Validar email
        if (!isValidEmail(input.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        
        // 6. Criar cliente
        Client client = new Client();
        client.setName(input.getName());
        client.setCnpj(input.getCnpj());
        client.setEmail(input.getEmail());
        client.setPhon(input.getPhone());
        client.setCompany(company);
        
        // 7. Persistir
        Client saved = repository.save(client);
        
        // 8. Registrar auditoria
        auditService.log(user, "CREATE_CLIENT", saved.getId(), "New client created");
        
        return saved;
    }
}
```

#### READ (findById)
```java
public Client findById(Integer id) {
    // 1. Autenticado?
    Company company = authService.getAuthenticatedCompany();
    
    // 2. Existe?
    Client client = repository.findById(id)
        .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    
    // 3. Pertence à empresa autenticada?
    if (!client.getCompany().getId().equals(company.getId())) {
        // Levantar exceção sem revelar detalhes
        throw new UnauthorizedException("Access denied");
    }
    
    return client;
}
```

#### UPDATE (updateClient)
```java
public Client update(ClientUpdateDTO input) {
    // 1. Autenticado?
    User user = authService.getAuthenticatedUser();
    Company company = authService.getAuthenticatedCompany();
    
    // 2. Cliente existe e pertence à empresa?
    Client client = repository.findByIdAndCompanyId(input.getId(), company.getId())
        .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    
    // 3. Se CNPJ foi modificado, validar unicidade
    if (input.getCnpj() != null && !input.getCnpj().equals(client.getCnpj())) {
        if (repository.findByCnpjAndCompanyId(input.getCnpj(), company.getId()).isPresent()) {
            throw new DuplicateClientException("CNPJ already exists");
        }
        client.setCnpj(input.getCnpj());
    }
    
    // 4. Se email foi modificado, validar formato
    if (input.getEmail() != null) {
        if (!isValidEmail(input.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        client.setEmail(input.getEmail());
    }
    
    // 5. Atualizar outros campos...
    if (input.getName() != null) client.setName(input.getName());
    if (input.getPhone() != null) client.setPhone(input.getPhone());
    // ... etc
    
    // 6. Persistir
    Client updated = repository.save(client);
    
    // 7. Registrar auditoria
    auditService.log(user, "UPDATE_CLIENT", updated.getId(), "Client updated");
    
    return updated;
}
```

#### DELETE (deactivate)
```java
public Client deactivate(Integer id) {
    // 1. Autenticado + permissão de admin?
    User user = authService.getAuthenticatedUser();
    Company company = authService.getAuthenticatedCompany();
    
    if (!user.getRole().equals(UserRole.ADMIN)) {
        throw new ForbiddenException("Only admins can deactivate clients");
    }
    
    // 2. Cliente existe e pertence à empresa?
    Client client = repository.findByIdAndCompanyId(id, company.getId())
        .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    
    // 3. Marcar como inativo (soft delete)
    client.setActive(false);
    client.setDeactivatedAt(LocalDateTime.now());
    
    Client deactivated = repository.save(client);
    
    // 4. Registrar auditoria (importante: registrar quem e quando)
    auditService.log(user, "DEACTIVATE_CLIENT", deactivated.getId(), 
        "Client deactivated by " + user.getEmail());
    
    return deactivated;
}
```

---

## 6. Tratamento de Erros Seguro

### 6.1 Mensagens de Erro - O QUE NÃO FAZER

```java
// INSEGURO: Revela informações sensíveis
try {
    client = repository.findById(unauthorizedClientId);
} catch (EntityNotFoundException e) {
    // Mensagem revela que o cliente existe mas é de outra empresa
    throw new GraphQLException("Client with ID 123 belongs to Company ABC");
}

// INSEGURO: Revela estrutura do banco de dados
throw new GraphQLException("Column 'fk_id_company' violates unique constraint");

// INSEGURO: Stack trace exposto
e.printStackTrace();
```

### 6.2 Mensagens de Erro - CORRETO

```java
// SEGURO: Mensagem genérica, sem revelar detalhes
if (!client.getCompany().getId().equals(company.getId())) {
    throw new UnauthorizedException("Access denied");
}

// SEGURO: Não revela que cliente existe ou não
throw new UnauthorizedException("Client not found or access denied");

// SEGURO: Log detalhado no servidor, mensagem genérica ao cliente
logger.warn("Unauthorized access attempt: User {} tried to access client {} from company {}",
    user.getId(), clientId, user.getCompanyId());
    
throw new UnauthorizedException("Access denied");
```

---

## 7. Auditoria e Logging

### 7.1 Eventos Auditados

```
Tabela: audit_logs
┌──────────────┬────────────────┬────────────┬──────────────┐
│ timestamp    │ user_id        │ action     │ client_id    │
├──────────────┼────────────────┼────────────┼──────────────┤
│ 2026-04-25   │ user_1         │ CREATE     │ client_5     │
│ 10:30:00     │ (admin@a.com)  │ _CLIENT    │              │
├──────────────┼────────────────┼────────────┼──────────────┤
│ 2026-04-25   │ user_2         │ UPDATE     │ client_5     │
│ 11:00:00     │ (user@a.com)   │ _CLIENT    │              │
├──────────────┼────────────────┼────────────┼──────────────┤
│ 2026-04-25   │ user_1         │ DEACTIVATE │ client_5     │
│ 14:30:00     │ (admin@a.com)  │ _CLIENT    │              │
└──────────────┴────────────────┴────────────┴──────────────┘
```

### 7.2 Implementação de Auditoria

```java
@Service
public class AuditService {
    
    private final AuditLogRepository repository;
    
    public void log(User user, String action, Integer clientId, String details) {
        AuditLog log = new AuditLog();
        log.setTimestamp(LocalDateTime.now());
        log.setUserId(user.getId());
        log.setUserEmail(user.getEmail());
        log.setCompanyId(user.getCompany().getId());
        log.setAction(action);
        log.setEntityId(clientId);
        log.setEntityType("CLIENT");
        log.setDetails(details);
        log.setIpAddress(getClientIpAddress());  // Rastrear IP
        
        repository.save(log);
    }
}
```

### 7.3 Queries de Auditoria

```sql
-- Listar todas as operações em um cliente
SELECT * FROM audit_logs 
WHERE entity_id = ? AND entity_type = 'CLIENT'
ORDER BY timestamp DESC;

-- Listar mudanças feitas por um usuário
SELECT * FROM audit_logs 
WHERE user_id = ? 
ORDER BY timestamp DESC;

-- Detectar atividades suspeitas
SELECT * FROM audit_logs 
WHERE action = 'UNAUTHORIZED_ACCESS_ATTEMPT'
AND timestamp > DATE_SUB(NOW(), INTERVAL 24 HOUR);
```

---

## 8. Proteção contra Ataques Comuns

### 8.1 SQL Injection

**Proteção**: JPA com parametrização automática

```java
// SEGURO: JPA parameteriza automaticamente
Optional<Client> client = repository.findByIdAndCompanyId(id, companyId);

// INSEGURO: Não fazer queries com concatenação de strings
// "SELECT * FROM clients WHERE id = " + id;
```

### 8.2 CSRF (Cross-Site Request Forgery)

**Proteção**: 
- JWT tokens (stateless, sem cookies)
- Same-origin policy
- Validação de origin no SecurityConfig

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // Desabilitado para GraphQL com JWT
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

### 8.3 Broken Authentication

**Sistema de Proteção**:
- Senhas com hash bcrypt
- JWT com expiração
- Refresh token com TTL menor
- Validação de email

```java
@Service
public class AuthService {
    
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    
    public LoginResponseDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        // Compara senha com hash
        if (!encoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        // Gera JWT com expiração
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return new LoginResponseDTO(token, refreshToken, "Bearer");
    }
}
```

### 8.4 Broken Authorization

**Sistema de Controle**:
- RBAC (Role-Based Access Control)
- Validação de tenant em cada operação
- Princípio do menor privilégio

```java
// Verificar permissão em cada operação
public Client deactivate(Integer id) {
    User user = authService.getAuthenticatedUser();
    
    if (!user.getRole().equals(UserRole.ADMIN)) {
        throw new ForbiddenException("Only admins can deactivate");
    }
    
    // ... resto da lógica
}
```

### 8.5 Information Disclosure

**Proteção**:
- Não expor stack traces
- Mensagens de erro genéricas
- Sanitizar logs
- Não revelar structure do banco

```java
// SEGURO: Mensagem genérica
throw new UnauthorizedException("Access denied");

// INSEGURO: Revela estrutura
throw new UnauthorizedException("Foreign key constraint violation on column fk_id_company");
```

---

## 9. Testes de Segurança

### 9.1 Teste de Isolamento de Tenant

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ClientSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ClientRepository repository;
    
    // Teste: Usuário A não deve acessar cliente da Empresa B
    @Test
    @WithMockUser(username = "user@companyA.com")
    public void shouldNotAccessClientFromDifferentCompany() throws Exception {
        // Arrange: Cliente pertence à Empresa B
        Client clientFromCompanyB = createClientForCompany(2);
        
        // Act: Usuário da Empresa A tenta acessar
        mockMvc.perform(graphql(
            "query { clientById(id: " + clientFromCompanyB.getId() + ") { name } }"
        ))
        
        // Assert: Deve retornar erro de acesso negado
        .andExpect(jsonPath("$.errors[0].extensions.code").value("UNAUTHORIZED"));
    }
    
    // Teste: Validação de unicidade por empresa
    @Test
    public void shouldAllowSameCnpjInDifferentCompanies() {
        // Arrange: Mesmo CNPJ em duas empresas diferentes
        Client client1 = createClient("12.345.678/0001-99", 1);  // Empresa 1
        
        // Act: Criar segundo cliente com mesmo CNPJ em empresa 2
        Client client2 = createClient("12.345.678/0001-99", 2);  // Empresa 2
        
        // Assert: Ambos devem existir
        assertNotNull(repository.findById(client1.getId()));
        assertNotNull(repository.findById(client2.getId()));
    }
    
    // Teste: Rejeitar CNPJ duplicado na mesma empresa
    @Test
    public void shouldRejectDuplicateCnpjInSameCompany() {
        // Arrange: Primeiro cliente criado
        createClient("12.345.678/0001-99", 1);
        
        // Act & Assert: Segundo com mesmo CNPJ deve ser rejeitado
        assertThrows(DuplicateClientException.class, () -> {
            createClient("12.345.678/0001-99", 1);
        });
    }
}
```

### 9.2 Teste de Auditoria

```java
@Test
public void shouldLogAllOperations() {
    // Arrange
    String email = "admin@company.com";
    Client client = createClient("Test", 1);
    
    // Act
    clientService.deactivate(client.getId());
    
    // Assert
    AuditLog log = auditRepository.findLatestLog("DEACTIVATE_CLIENT");
    assertEquals(email, log.getUserEmail());
    assertEquals(client.getId(), log.getEntityId());
    assertNotNull(log.getTimestamp());
}
```

---

## 10. Checklist de Deploy

Antes de colocar em produção:

- [ ] Todas as queries aplicam filtro de `company_id`
- [ ] Mensagens de erro não revelam detalhes sensíveis
- [ ] JWT secret é configurado com valor forte
- [ ] CORS está restrito aos domínios autorizados
- [ ] Auditoria está habilitada
- [ ] Logs estão configurados (não expor senhas)
- [ ] Rate limiting está ativo
- [ ] Backup está configurado
- [ ] HTTPS/TLS está obrigatório
- [ ] Testes de segurança passam
- [ ] Code review foi realizado
- [ ] Plano de incident response está pronto

---

## 11. Referências e Recursos

- **OWASP Top 10**: https://owasp.org/Top10/
- **Spring Security**: https://spring.io/projects/spring-security
- **JWT Best Practices**: https://tools.ietf.org/html/rfc8725
- **GraphQL Security**: https://cheatsheetseries.owasp.org/cheatsheets/GraphQL_Cheat_Sheet.html
- **Multi-Tenancy Patterns**: https://www.postgresql.org/docs/current/ddl-schemas.html

---

**Documento Versão**: 1.0  
**Data de Atualização**: 25 de abril de 2026  
**Responsável**: Time de Segurança
