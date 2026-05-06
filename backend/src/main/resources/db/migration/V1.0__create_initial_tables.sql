CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tenants do sistema
CREATE TABLE IF NOT EXISTS tenant
(
    id         UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    code       VARCHAR(50) UNIQUE NOT NULL,
    name       VARCHAR(255)       NOT NULL,
    enabled    BOOLEAN                  DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Compradores
CREATE TABLE IF NOT EXISTS buyer
(
    id                 UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    external_reference VARCHAR(100) UNIQUE NOT NULL,
    name               VARCHAR(255)        NOT NULL,
    credit_limit       DECIMAL(15, 2)      NOT NULL DEFAULT 0,
    tenant_code        VARCHAR(50)         NOT NULL,
    enabled            BOOLEAN                      DEFAULT true,
    created_at         TIMESTAMP WITH TIME ZONE     DEFAULT now(),
    last_modified      TIMESTAMP WITH TIME ZONE     DEFAULT now(),
    version            BIGINT                       DEFAULT 0
);

-- Vendedores
CREATE TABLE IF NOT EXISTS seller
(
    id                 UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    external_reference VARCHAR(100) UNIQUE NOT NULL,
    name               VARCHAR(255)        NOT NULL,
    tenant_code        VARCHAR(50)         NOT NULL,
    enabled            BOOLEAN                  DEFAULT true,
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Armazéns
CREATE TABLE IF NOT EXISTS warehouse
(
    id                 UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    external_reference VARCHAR(100) UNIQUE NOT NULL,
    name               VARCHAR(255)        NOT NULL,
    seller_id          UUID                NOT NULL REFERENCES seller (id),
    tenant_code        VARCHAR(50)         NOT NULL,
    enabled            BOOLEAN                  DEFAULT true,
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Tabela de preços por produto/warehouse
CREATE TABLE IF NOT EXISTS product_price
(
    id            UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    product_code  VARCHAR(100)   NOT NULL,
    product_name  VARCHAR(255)   NOT NULL,
    warehouse_id  UUID           NOT NULL REFERENCES warehouse (id),
    unit_price    DECIMAL(15, 4) NOT NULL,
    list_price    DECIMAL(15, 4) NOT NULL,
    tenant_code   VARCHAR(50)    NOT NULL,
    enabled       BOOLEAN                  DEFAULT true,
    last_modified TIMESTAMP WITH TIME ZONE DEFAULT now(),
    UNIQUE (product_code, warehouse_id)
);

-- Condições de pagamento
CREATE TABLE IF NOT EXISTS payment_condition
(
    id                  UUID PRIMARY KEY            DEFAULT gen_random_uuid(),
    code                VARCHAR(50) UNIQUE NOT NULL,
    description         VARCHAR(255)       NOT NULL,
    max_installments    INT                NOT NULL DEFAULT 1,
    discount_percentage DECIMAL(5, 2)               DEFAULT 0,
    tenant_code         VARCHAR(50)        NOT NULL,
    enabled             BOOLEAN                     DEFAULT true
);

-- Pedidos
CREATE TABLE IF NOT EXISTS "order"
(
    id                   UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    external_reference   VARCHAR(100) UNIQUE NOT NULL,
    buyer_id             UUID                NOT NULL REFERENCES buyer (id),
    seller_id            UUID                NOT NULL REFERENCES seller (id),
    warehouse_id         UUID                NOT NULL REFERENCES warehouse (id),
    payment_condition_id UUID                NOT NULL REFERENCES payment_condition (id),
    status               VARCHAR(30)         NOT NULL DEFAULT 'PENDING',
    subtotal             DECIMAL(15, 2)      NOT NULL,
    discount_value       DECIMAL(15, 2)               DEFAULT 0,
    total                DECIMAL(15, 2)      NOT NULL,
    origin               VARCHAR(30)         NOT NULL DEFAULT 'API',
    tenant_code          VARCHAR(50)         NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE     DEFAULT now(),
    last_modified        TIMESTAMP WITH TIME ZONE     DEFAULT now(),
    version              BIGINT                       DEFAULT 0
);

-- Itens do pedido
CREATE TABLE IF NOT EXISTS order_item
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id     UUID           NOT NULL REFERENCES "order" (id),
    product_code VARCHAR(100)   NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    quantity     INT            NOT NULL,
    unit_price   DECIMAL(15, 4) NOT NULL,
    list_price   DECIMAL(15, 4) NOT NULL,
    subtotal     DECIMAL(15, 2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_order_tenant ON "order" (tenant_code);
CREATE INDEX IF NOT EXISTS idx_order_buyer ON "order" (buyer_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON "order" (status);
CREATE INDEX IF NOT EXISTS idx_order_created ON "order" (created_at);
CREATE INDEX IF NOT EXISTS idx_buyer_tenant ON buyer (tenant_code);
CREATE INDEX IF NOT EXISTS idx_product_price_warehouse ON product_price (warehouse_id);