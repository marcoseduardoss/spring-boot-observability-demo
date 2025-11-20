## Demo de Observabilidade em Framework Web Java (Spring Boot) - v3

Esta versão usa:

* Spring Boot **3.3.2**
* **Java 17**
* **Log4j2** com logs em JSON
* **Micrometer + Actuator** com endpoint `/actuator/prometheus`
* **OpenTelemetry Spring Boot Starter** (auto-config) + Logging Exporter
* **Docker Compose** com Prometheus e Grafana

---

## 🏃 Executar localmente (sem Docker)

Requisitos:

* Java 17
* Maven 3.9+

### Rodar a aplicação

```bash
mvn spring-boot:run
```

### Endpoints úteis

| Função              | URL                                                                                    |
| ------------------- | -------------------------------------------------------------------------------------- |
| API REST            | [http://localhost:8080/api/clientes/1](http://localhost:8080/api/clientes/1)           |
| Health Check        | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)         |
| Métricas Prometheus | [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus) |

---

## 🐳 Executar com Docker Compose

Requisitos:

* Docker
* Docker Compose

### Subir tudo

```bash
docker compose up --build
```

### Serviços disponíveis

| Serviço               | URL                                                                          |
| --------------------- | ---------------------------------------------------------------------------- |
| Aplicação Spring Boot | [http://localhost:8080/api/clientes/1](http://localhost:8080/api/clientes/1) |
| Prometheus            | [http://localhost:9090](http://localhost:9090)                               |
| Grafana               | [http://localhost:3000](http://localhost:3000)                               |

### Login no Grafana

* **usuário:** `admin`
* **senha:** `admin`
  *(definido no docker compose)*

---

# 📊 PASSO A PASSO PARA TESTAR A OBSERVABILIDADE

Abaixo está um guia extremamente claro para você demonstrar tudo para os alunos.

---

# 1. 🔥 Testar a aplicação (gerar tráfego)

Acesse manualmente no navegador ou use curl:

### Sucesso (200)

```
http://localhost:8080/api/clientes/3
```

### Erro intencional (400 – ID inválido)

```
http://localhost:8080/api/clientes/xyz
```

Faça várias requisições para gerar dados de métricas.

---

# 2. 📈 Testar no Prometheus

Abra:

➡ [http://localhost:9090](http://localhost:9090)

### Consulta para ver contagem de requisições por URI

```
http_server_requests_seconds_count
```

Você verá algo como:

* `/api/clientes/{id}` com status **200**
* `/api/clientes/{id}` com status **400**

### Consulta filtrando por URI

```
http_server_requests_seconds_count{uri="/api/clientes/{id}"}
```

### Consulta com taxa por segundo (rate)

```
rate(http_server_requests_seconds_count[1m])
```

---

# 3. 📉 Criar gráfico no Grafana (PASSO A PASSO COMPLETO)

### 3.1 Abra o Grafana

➡ [http://localhost:3000](http://localhost:3000)
login: **admin** / **admin**

---

### 3.2 Abra o menu **Explore**

No menu esquerdo → **Explore**

---

### 3.3 Selecione o Data Source

Escolha:

```
prometheus
```

Se aparecer "default", está ok.

---

### 3.4 Clique em **Code** (não use Builder)

⚠ IMPORTANTE
A query deve ser inserida na aba **Code**, não no Builder.

---

### 3.5 Cole a query para exibir requisições por URI

```
sum(rate(http_server_requests_seconds_count{uri="/api/clientes/{id}"}[1m])) by (uri)
```

---

### 3.6 Gere tráfego novamente

Abra:

```
http://localhost:8080/api/clientes/5
http://localhost:8080/api/clientes/999
http://localhost:8080/api/clientes/abc
```

O gráfico atualiza automaticamente.

---

# 4. 📝 Testar os logs JSON (Log4j2 + OTel)

Veja os logs em JSON:

```
docker logs spring-boot-obs -f
```

Você verá:

* `traceId`
* `spanId`
* requisição
* status
* duração
* uri
* nome do método

Isso mostra a integração Log4j2 + OpenTelemetry.

---

# 5. ✔ Lista rápida de queries úteis para aula

## Quantidade de requisições por URI

```
http_server_requests_seconds_count
```

## Taxa de requisições por segundo (todas URIs)

```
sum(rate(http_server_requests_seconds_count[1m])) by (uri)
```

## Filtrar por status code

```
http_server_requests_seconds_count{status="400"}
```

## Latência (somente se habilitar histograma)

```
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_seconds_bucket[5m])) by (le))
```

---

# 6. 🧹 Reiniciar tudo (opcional)

```
docker compose down -v
docker compose up --build
```
