# Manual básico Redis com Docker (Linux e Windows)

## 1. Pré-requisitos

Instalar:

* Docker Desktop (Windows)
* Docker Engine + Docker Compose (Linux)

Verificar instalação:

```bash
docker --version
docker compose version
```

---

# 2. Criar o arquivo Docker Compose

Crie um arquivo chamado:

```text
docker-compose.yml
```

Conteúdo:

```yaml
services:
  redis:
    image: redis:7
    container_name: redis
    ports:
      - "6379:6379"
    restart: unless-stopped
```

---

# 3. Subir o Redis

## Linux

No terminal:

```bash
docker compose up -d
```

## Windows

No PowerShell ou CMD:

```powershell
docker compose up -d
```

---

# 4. Verificar se o container está rodando

```bash
docker ps
```

Resultado esperado:

```text
CONTAINER ID   IMAGE     NAMES
xxxxxx         redis:7   redis
```

---

# 5. Entrar no container

```bash
docker exec -it redis sh
```

---

# 6. Abrir o Redis CLI

Dentro do container:

```bash
redis-cli
```

Prompt esperado:

```text
127.0.0.1:6379>
```

---

# 7. Verificar cache / chaves

## Listar todas as keys

```redis
KEYS *
```

## Forma recomendada (produção)

```redis
SCAN 0
```

---

# 8. Consultar valores

## String

```redis
GET minha-chave
```

## Hash

```redis
HGETALL usuario:1
```

## Lista

```redis
LRANGE lista 0 -1
```

## Set

```redis
SMEMBERS set-exemplo
```

## Ver tipo da chave

```redis
TYPE minha-chave
```

---

# 9. Ver TTL (expiração)

```redis
TTL minha-chave
```

Retornos:

* `-1` → sem expiração
* `-2` → chave inexistente
* número positivo → segundos restantes

---

# 10. Ver quantidade de chaves

```redis
DBSIZE
```

---

# 11. Monitorar operações em tempo real

```redis
MONITOR
```

Mostra:

* GET
* SET
* DEL
* EXPIRE
* etc.

---

# 12. Ver uso de memória

```redis
INFO memory
```

---

# 13. Ver estatísticas do banco

```redis
INFO keyspace
```

---

# 14. Inserir dados manualmente

## String

```redis
SET teste "valor"
```

## Hash

```redis
HSET usuario:1 nome "Nyz"
```

---

# 15. Remover chaves

## Uma chave

```redis
DEL minha-chave
```

## Todas as chaves

```redis
FLUSHALL
```

Cuidado: remove tudo.

---

# 16. Sair

## Sair do redis-cli

```redis
exit
```

## Sair do container

```bash
exit
```

---

# 17. Derrubar o ambiente

```bash
docker compose down
```

---

# 18. Remover todos os containers Docker

```bash
docker rm -f $(docker ps -aq)
```

Windows PowerShell:

```powershell
docker rm -f $(docker ps -aq)
```

---

# 19. Limpeza completa Docker

Remove:

* containers
* imagens
* cache
* volumes não utilizados

```bash
docker system prune -a --volumes
```

---

# 20. Acesso direto sem entrar no shell

## Abrir redis-cli direto

```bash
docker exec -it redis redis-cli
```

## Executar comando direto

```bash
docker exec -it redis redis-cli KEYS '*'
```

---

# 21. Estrutura típica de uso

```bash
docker compose up -d
docker ps
docker exec -it redis redis-cli
```

Depois:

```redis
KEYS *
GET chave
SCAN 0
INFO keyspace
```
