# Desafio Técnico - Desenvolvedor Full Stack Sênior

Aplicação full stack de gestão de pedidos com suporte a multi-tenancy, processamento assíncrono via mensageria e estratégias de precificação e validação configuráveis por tenant.

---

## Tecnologias

### Backend
- **Java 21** + **Spring Boot 3.5**
- **PostgreSQL 15** — banco de dados principal com migrations via Flyway
- **RabbitMQ 3** — mensageria para processamento assíncrono de pedidos
- **Spring Data JPA** + **Hibernate** — persistência
- **MapStruct** — mapeamento entre entidades e DTOs
- **Lombok** — redução de boilerplate

### Frontend
- **React 19** + **TypeScript**
- **Vite** — bundler e dev server
- **React Router v7** — roteamento SPA
- **Tailwind CSS** — estilização
- **Axios** — cliente HTTP

### Infraestrutura
- **Docker Compose** — orquestração dos 4 serviços
- **Nginx** — servidor do frontend com proxy reverso para a API

---

## Arquitetura

```
┌─────────────────────────────────────────────────┐
│                  Docker Compose                  │
│                                                  │
│  ┌──────────┐    ┌──────────┐    ┌───────────┐  │
│  │ Frontend │───▶│ Backend  │───▶│PostgreSQL │  │
│  │  :3000   │    │  :8080   │    │   :5432   │  │
│  │  (Nginx) │    │  (Java)  │    └───────────┘  │
│  └──────────┘    └────┬─────┘                   │
│                       │        ┌───────────┐     │
│                       └───────▶│ RabbitMQ  │     │
│                                │   :5672   │     │
│                                │  UI:15672 │     │
│                                └───────────┘     │
└─────────────────────────────────────────────────┘
```

O frontend (React SPA) é servido pelo Nginx na porta `3000`. As chamadas para `/api/` são redirecionadas pelo proxy reverso do Nginx para o backend na porta `8080`. O backend publica eventos de pedido em uma exchange fanout do RabbitMQ, consumidos assincronamente pelos consumers de processamento e notificação.

---

## Estrutura do Projeto

```
desafio-b2-dev/
├── compose.yaml            # Orquestração Docker
├── backend/                # API Spring Boot
└── frontend/               # SPA React + TypeScript
```

### Backend

```
backend/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/yzakcarmo/desafiob2dev/
    │   ├── api/
    │   │   ├── controller/         # OrderController, MessagingController
    │   │   └── dto/                # Request/Response DTOs
    │   ├── domain/
    │   │   ├── entity/             # Order, OrderItem, Buyer, Seller, Warehouse,
    │   │   │                       #   ProductPrice, PaymentCondition, Tenant
    │   │   ├── enums/              # OrderStatus, OrderOrigin
    │   │   └── repository/         # Spring Data JPA + Specifications
    │   ├── service/                # OrderService, OrderStatisticsService
    │   ├── strategy/               # Validação, precificação e desconto por tenant
    │   │   └── farma/              # FARMA-DEFAULT, FARMA-ECONOMIA, FARMA-PREMIUM
    │   ├── infrastructure/
    │   │   └── messaging/          # Producer + Consumers RabbitMQ
    │   ├── config/                 # RabbitMQConfig
    │   ├── tenant/                 # TenantContext (ThreadLocal), TenantFilter
    │   └── exception/              # Exceptions de domínio + GlobalExceptionHandler
    └── resources/
        ├── application.yml
        └── db/migration/
            ├── V1.0__create_initial_tables.sql
            └── V1.1__start_seeds.sql
```

### Frontend

```
frontend/
├── Dockerfile
├── nginx.conf
├── package.json
├── vite.config.ts
└── src/
    ├── api/
    │   ├── client.ts       # Axios com interceptors (x-tenant, Authorization)
    │   └── orders.ts       # listOrders, getOrder, createOrder, cancelOrder, getStatistics
    ├── context/
    │   └── TenantContext.tsx   # Tenant e authToken (persiste no localStorage)
    ├── hooks/
    │   ├── useOrders.ts
    │   ├── useOrderDetail.ts
    │   └── useStatistics.ts
    ├── pages/
    │   ├── Dashboard.tsx       # Estatísticas com filtros de data
    │   ├── Orders.tsx          # Listagem paginada com filtros
    │   ├── CreateOrder.tsx     # Formulário de criação
    │   └── OrderDetail.tsx     # Detalhe do pedido
    ├── components/
    │   ├── Layout.tsx
    │   ├── StatusBadge.tsx
    │   └── TenantModal.tsx
    ├── types/index.ts
    └── utils/format.ts
```

---

## Multi-Tenancy

O tenant é identificado pelo header `x-tenant` em cada requisição. O frontend injeta esse header automaticamente via interceptor do Axios com base na configuração armazenada no `localStorage`.

Tenants disponíveis (seeds):

| Código | Perfil |
|--------|--------|
| `FARMA-DEFAULT` | Padrão — regras base de validação e desconto |
| `FARMA-ECONOMIA` | Economia — estratégia com foco em preço reduzido |
| `FARMA-PREMIUM` | Premium — estratégia com condições especiais |

Cada tenant possui implementações próprias de:
- **Validação** (`OrderValidationStrategy`)
- **Precificação** (`OrderPricingStrategy`)
- **Desconto** (`OrderDiscountStrategy`)

---

## Endpoints da API

Base URL: `http://localhost:8080/api/v1`

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/orders` | Criar pedido |
| `GET` | `/orders` | Listar pedidos (paginado, com filtros) |
| `GET` | `/orders/{externalReference}` | Detalhe do pedido |
| `POST` | `/orders/{externalReference}/cancel` | Cancelar pedido |
| `GET` | `/orders/statistics` | Estatísticas (receita, top compradores/produtos) |

Todos os endpoints exigem o header `x-tenant` com o código do tenant.

---

## Mensageria (RabbitMQ)

Ao criar um pedido, o backend publica um evento na exchange fanout `order.events.fanout`, consumido por duas filas independentes:

| Fila | Função |
|------|--------|
| `order.process` | Processamento do pedido (ack manual, DLQ após 3 tentativas) |
| `order.notification` | Envio de notificações |
| `order.process.dlq` | Dead-letter com TTL de 30s |
| `order.process.parking-lot` | Mensagens não processáveis (TTL de 30 dias) |

---

## Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados

### Subir todos os serviços

```bash
docker compose up --build
```

| Serviço | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| API Backend | http://localhost:8080 |
| RabbitMQ Management | http://localhost:15672 (user: `desafio` / pass: `desafio123`) |
| PostgreSQL | `localhost:5432` (db: `desafiob2dev` / user: `desafio` / pass: `desafio123`) |

O backend aguarda o PostgreSQL e o RabbitMQ estarem saudáveis antes de iniciar. As migrations do Flyway são executadas automaticamente na inicialização, incluindo dados de seed para testes.

---

## Padrões Aplicados

- **Strategy Pattern** — comportamento por tenant (validação, precificação, desconto)
- **Repository Pattern** — abstração de acesso a dados via Spring Data JPA
- **Event-Driven** — pedidos publicam eventos para processamento assíncrono
- **Optimistic Locking** — campo `@Version` em `Order` e `Buyer` para controle de concorrência
- **Global Exception Handler** — respostas de erro padronizadas com código, mensagem e traceId
