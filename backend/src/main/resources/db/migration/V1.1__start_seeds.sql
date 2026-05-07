-- ==========================================
-- TENANTS
-- ==========================================
INSERT INTO tenant (id, code, name)
VALUES ('00000000-0000-0000-0000-000000000001', 'FARMA-DEFAULT', 'Rede Farma Padrão'),
       ('00000000-0000-0000-0000-000000000002', 'FARMA-PREMIUM', 'Rede Farma Premium'),
       ('00000000-0000-0000-0000-000000000003', 'FARMA-ECONOMIA', 'Rede Farma Economia');

-- ==========================================
-- SELLERS
-- ==========================================
INSERT INTO seller (id, external_reference, name, tenant_code)
VALUES ('00000000-0000-0000-0001-000000000001', 'SELLER-001', 'Distribuidora Norte Ltda', 'FARMA-DEFAULT'),
       ('00000000-0000-0000-0001-000000000002', 'SELLER-002', 'Distribuidora Sul S.A.', 'FARMA-DEFAULT'),
       ('00000000-0000-0000-0001-000000000003', 'SELLER-003', 'Pharma Premium Distribuidora', 'FARMA-PREMIUM'),
       ('00000000-0000-0000-0001-000000000004', 'SELLER-004', 'Economia Atacado Ltda', 'FARMA-ECONOMIA'),
       ('00000000-0000-0000-0001-000000000005', 'SELLER-005', 'Distribuidora Centro-Oeste', 'FARMA-DEFAULT');

-- ==========================================
-- WAREHOUSES
-- ==========================================
INSERT INTO warehouse (id, external_reference, name, seller_id, tenant_code)
VALUES ('00000000-0000-0000-0002-000000000001', 'WH-001', 'CD São Paulo', '00000000-0000-0000-0001-000000000001',
        'FARMA-DEFAULT'),
       ('00000000-0000-0000-0002-000000000002', 'WH-002', 'CD Rio de Janeiro', '00000000-0000-0000-0001-000000000002',
        'FARMA-DEFAULT'),
       ('00000000-0000-0000-0002-000000000003', 'WH-003', 'CD Premium Campinas', '00000000-0000-0000-0001-000000000003',
        'FARMA-PREMIUM'),
       ('00000000-0000-0000-0002-000000000004', 'WH-004', 'CD Economia Goiânia', '00000000-0000-0000-0001-000000000004',
        'FARMA-ECONOMIA'),
       ('00000000-0000-0000-0002-000000000005', 'WH-005', 'CD Belo Horizonte', '00000000-0000-0000-0001-000000000005',
        'FARMA-DEFAULT');

-- ==========================================
-- BUYERS (10 por tenant, 30 no total — divididos)
-- ==========================================
INSERT INTO buyer (id, external_reference, name, credit_limit, tenant_code)
VALUES
    -- FARMA-DEFAULT
    ('00000000-0000-0000-0003-000000000001', 'BUYER-001', 'Farmácia Central Ltda', 15000.00, 'FARMA-DEFAULT'),
    ('00000000-0000-0000-0003-000000000002', 'BUYER-002', 'Drogaria São João', 8000.00, 'FARMA-DEFAULT'),
    ('00000000-0000-0000-0003-000000000003', 'BUYER-003', 'Farmácia Popular do Bairro', 5000.00, 'FARMA-DEFAULT'),
    ('00000000-0000-0000-0003-000000000004', 'BUYER-004', 'Drogaria Bem Estar', 12000.00, 'FARMA-DEFAULT'),
    -- FARMA-PREMIUM
    ('00000000-0000-0000-0003-000000000005', 'BUYER-005', 'Rede Premium Saúde S.A.', 50000.00, 'FARMA-PREMIUM'),
    ('00000000-0000-0000-0003-000000000006', 'BUYER-006', 'Clínica Vida Premium', 30000.00, 'FARMA-PREMIUM'),
    ('00000000-0000-0000-0003-000000000007', 'BUYER-007', 'Hospital São Lucas Premium', 80000.00, 'FARMA-PREMIUM'),
    -- FARMA-ECONOMIA
    ('00000000-0000-0000-0003-000000000008', 'BUYER-008', 'Farmácia Economia Total', 3000.00, 'FARMA-ECONOMIA'),
    ('00000000-0000-0000-0003-000000000009', 'BUYER-009', 'Drogaria Barato Já', 2000.00, 'FARMA-ECONOMIA'),
    ('00000000-0000-0000-0003-000000000010', 'BUYER-010', 'Farmácia do Trabalhador', 4500.00, 'FARMA-ECONOMIA');

-- ==========================================
-- PAYMENT CONDITIONS
-- ==========================================
INSERT INTO payment_condition (id, code, description, max_installments, discount_percentage, tenant_code)
VALUES ('00000000-0000-0000-0004-000000000001', 'A-VISTA', 'Pagamento à Vista', 1, 2.00, 'FARMA-DEFAULT'),
       ('00000000-0000-0000-0004-000000000002', '30-DIAS', 'Pagamento em 30 dias', 1, 0.00, 'FARMA-DEFAULT'),
       ('00000000-0000-0000-0004-000000000003', '30-60', 'Parcelado 30/60 dias', 2, 0.00, 'FARMA-DEFAULT'),
       ('00000000-0000-0000-0004-000000000004', '30-60-90', 'Parcelado 30/60/90 dias', 3, 0.00, 'FARMA-DEFAULT'),
       ('00000000-0000-0000-0004-000000000005', 'PREM-VISTA', 'Premium à Vista', 1, 5.00, 'FARMA-PREMIUM'),
       ('00000000-0000-0000-0004-000000000006', 'PREM-30', 'Premium 30 dias', 1, 0.00, 'FARMA-PREMIUM'),
       ('00000000-0000-0000-0004-000000000007', 'ECO-VISTA', 'Economia à Vista', 1, 2.00, 'FARMA-ECONOMIA'),
       ('00000000-0000-0000-0004-000000000008', 'ECO-30', 'Economia 30 dias', 1, 0.00, 'FARMA-ECONOMIA');

-- ==========================================
-- PRODUCT PRICES (50 produtos, distribuídos pelos warehouses)
-- ==========================================
INSERT INTO product_price (product_code, product_name, warehouse_id, unit_price, list_price, tenant_code)
VALUES
    -- WH-001 (FARMA-DEFAULT)
    ('PROD-001', 'Dipirona 500mg cx/20cp', '00000000-0000-0000-0002-000000000001', 8.50, 12.00, 'FARMA-DEFAULT'),
    ('PROD-002', 'Amoxicilina 500mg cx/21cp', '00000000-0000-0000-0002-000000000001', 22.90, 35.00, 'FARMA-DEFAULT'),
    ('PROD-003', 'Omeprazol 20mg cx/28cp', '00000000-0000-0000-0002-000000000001', 18.40, 28.00, 'FARMA-DEFAULT'),
    ('PROD-004', 'Losartana 50mg cx/30cp', '00000000-0000-0000-0002-000000000001', 14.20, 22.00, 'FARMA-DEFAULT'),
    ('PROD-005', 'Metformina 850mg cx/30cp', '00000000-0000-0000-0002-000000000001', 12.80, 19.00, 'FARMA-DEFAULT'),
    ('PROD-006', 'Atorvastatina 20mg cx/30cp', '00000000-0000-0000-0002-000000000001', 32.50, 48.00, 'FARMA-DEFAULT'),
    ('PROD-007', 'Azitromicina 500mg cx/3cp', '00000000-0000-0000-0002-000000000001', 19.90, 30.00, 'FARMA-DEFAULT'),
    ('PROD-008', 'Ibuprofeno 600mg cx/20cp', '00000000-0000-0000-0002-000000000001', 9.80, 15.00, 'FARMA-DEFAULT'),
    ('PROD-009', 'Paracetamol 750mg cx/20cp', '00000000-0000-0000-0002-000000000001', 7.60, 11.00, 'FARMA-DEFAULT'),
    ('PROD-010', 'Captopril 25mg cx/30cp', '00000000-0000-0000-0002-000000000001', 11.30, 17.00, 'FARMA-DEFAULT'),
    -- WH-002 (FARMA-DEFAULT)
    ('PROD-001', 'Dipirona 500mg cx/20cp', '00000000-0000-0000-0002-000000000002', 8.80, 12.00, 'FARMA-DEFAULT'),
    ('PROD-002', 'Amoxicilina 500mg cx/21cp', '00000000-0000-0000-0002-000000000002', 23.50, 35.00, 'FARMA-DEFAULT'),
    ('PROD-011', 'Sinvastatina 20mg cx/30cp', '00000000-0000-0000-0002-000000000002', 16.70, 25.00, 'FARMA-DEFAULT'),
    ('PROD-012', 'Enalapril 10mg cx/30cp', '00000000-0000-0000-0002-000000000002', 13.40, 20.00, 'FARMA-DEFAULT'),
    ('PROD-013', 'Clonazepam 2mg cx/30cp', '00000000-0000-0000-0002-000000000002', 28.90, 42.00, 'FARMA-DEFAULT'),
    ('PROD-014', 'Fluoxetina 20mg cx/30cp', '00000000-0000-0000-0002-000000000002', 24.60, 36.00, 'FARMA-DEFAULT'),
    ('PROD-015', 'Sertralina 50mg cx/30cp', '00000000-0000-0000-0002-000000000002', 31.20, 46.00, 'FARMA-DEFAULT'),
    ('PROD-016', 'Metoprolol 50mg cx/30cp', '00000000-0000-0000-0002-000000000002', 19.80, 30.00, 'FARMA-DEFAULT'),
    ('PROD-017', 'Amlodipino 5mg cx/30cp', '00000000-0000-0000-0002-000000000002', 15.60, 23.00, 'FARMA-DEFAULT'),
    ('PROD-018', 'Espironolactona 25mg cx/30cp', '00000000-0000-0000-0002-000000000002', 22.10, 33.00, 'FARMA-DEFAULT'),
    -- WH-003 (FARMA-PREMIUM)
    ('PROD-019', 'Insulina Glargina 100UI/mL', '00000000-0000-0000-0002-000000000003', 98.00, 145.00, 'FARMA-PREMIUM'),
    ('PROD-020', 'Adalimumabe 40mg inj', '00000000-0000-0000-0002-000000000003', 950.00, 1400.00, 'FARMA-PREMIUM'),
    ('PROD-021', 'Rivaroxabana 20mg cx/28cp', '00000000-0000-0000-0002-000000000003', 145.00, 210.00, 'FARMA-PREMIUM'),
    ('PROD-022', 'Apixabana 5mg cx/60cp', '00000000-0000-0000-0002-000000000003', 188.00, 275.00, 'FARMA-PREMIUM'),
    ('PROD-023', 'Empagliflozina 10mg cx/30cp', '00000000-0000-0000-0002-000000000003', 210.00, 310.00,
     'FARMA-PREMIUM'),
    ('PROD-024', 'Dapagliflozina 10mg cx/30cp', '00000000-0000-0000-0002-000000000003', 198.00, 290.00,
     'FARMA-PREMIUM'),
    ('PROD-025', 'Sitagliptina 100mg cx/28cp', '00000000-0000-0000-0002-000000000003', 165.00, 240.00, 'FARMA-PREMIUM'),
    ('PROD-026', 'Tadalafila 20mg cx/4cp', '00000000-0000-0000-0002-000000000003', 88.00, 130.00, 'FARMA-PREMIUM'),
    ('PROD-027', 'Semaglutida 0.5mg inj', '00000000-0000-0000-0002-000000000003', 420.00, 620.00, 'FARMA-PREMIUM'),
    ('PROD-028', 'Dupilumabe 300mg inj', '00000000-0000-0000-0002-000000000003', 880.00, 1300.00, 'FARMA-PREMIUM'),
    -- WH-004 (FARMA-ECONOMIA)
    ('PROD-029', 'Dipirona Gotas 500mg/mL 10mL', '00000000-0000-0000-0002-000000000004', 4.20, 7.00, 'FARMA-ECONOMIA'),
    ('PROD-030', 'Paracetamol 500mg cx/20cp', '00000000-0000-0000-0002-000000000004', 5.80, 9.00, 'FARMA-ECONOMIA'),
    ('PROD-031', 'Amoxicilina 250mg/5mL susp', '00000000-0000-0000-0002-000000000004', 12.40, 19.00, 'FARMA-ECONOMIA'),
    ('PROD-032', 'Ibuprofeno 300mg cx/20cp', '00000000-0000-0000-0002-000000000004', 7.90, 12.00, 'FARMA-ECONOMIA'),
    ('PROD-033', 'Loratadina 10mg cx/12cp', '00000000-0000-0000-0002-000000000004', 6.50, 10.00, 'FARMA-ECONOMIA'),
    ('PROD-034', 'Dexametasona 4mg cx/6cp', '00000000-0000-0000-0002-000000000004', 8.90, 14.00, 'FARMA-ECONOMIA'),
    ('PROD-035', 'Metronidazol 400mg cx/14cp', '00000000-0000-0000-0002-000000000004', 9.60, 15.00, 'FARMA-ECONOMIA'),
    ('PROD-036', 'Cetirizina 10mg cx/12cp', '00000000-0000-0000-0002-000000000004', 7.20, 11.00, 'FARMA-ECONOMIA'),
    ('PROD-037', 'Nimesulida 100mg cx/12cp', '00000000-0000-0000-0002-000000000004', 6.80, 10.00, 'FARMA-ECONOMIA'),
    ('PROD-038', 'Ranitidina 150mg cx/20cp', '00000000-0000-0000-0002-000000000004', 8.10, 12.00, 'FARMA-ECONOMIA'),
    -- WH-005 (FARMA-DEFAULT)
    ('PROD-039', 'Vitamina D3 2000UI cx/30cp', '00000000-0000-0000-0002-000000000005', 24.90, 37.00, 'FARMA-DEFAULT'),
    ('PROD-040', 'Vitamina C 1g efervescente cx/10', '00000000-0000-0000-0002-000000000005', 18.70, 28.00,
     'FARMA-DEFAULT'),
    ('PROD-041', 'Ômega 3 1000mg cx/60cp', '00000000-0000-0000-0002-000000000005', 42.50, 63.00, 'FARMA-DEFAULT'),
    ('PROD-042', 'Complexo B cx/60cp', '00000000-0000-0000-0002-000000000005', 16.80, 25.00, 'FARMA-DEFAULT'),
    ('PROD-043', 'Magnésio Quelato 300mg cx/60cp', '00000000-0000-0000-0002-000000000005', 38.90, 58.00,
     'FARMA-DEFAULT'),
    ('PROD-044', 'Zinco 30mg cx/60cp', '00000000-0000-0000-0002-000000000005', 22.40, 33.00, 'FARMA-DEFAULT'),
    ('PROD-045', 'Coenzima Q10 100mg cx/30cp', '00000000-0000-0000-0002-000000000005', 68.00, 100.00, 'FARMA-DEFAULT'),
    ('PROD-046', 'Colágeno Hidrolisado 10g saches', '00000000-0000-0000-0002-000000000005', 34.90, 52.00,
     'FARMA-DEFAULT'),
    ('PROD-047', 'Probiótico 10bi UFC cx/30cp', '00000000-0000-0000-0002-000000000005', 54.00, 80.00, 'FARMA-DEFAULT'),
    ('PROD-048', 'Melatonina 0.21mg cx/30cp', '00000000-0000-0000-0002-000000000005', 28.60, 42.00, 'FARMA-DEFAULT'),
    ('PROD-049', 'Biotina 10000mcg cx/60cp', '00000000-0000-0000-0002-000000000005', 31.20, 46.00, 'FARMA-DEFAULT'),
    ('PROD-050', 'Ferro Bisglicinato 25mg cx/60cp', '00000000-0000-0000-0002-000000000005', 36.80, 55.00,
     'FARMA-DEFAULT');

-- ==========================================
-- ORDERS (100+ pedidos pre-existentes)
-- ==========================================
DO
$$
    DECLARE
        v_index              INTEGER;
        v_order_id           UUID;
        v_buyer_id           UUID;
        v_seller_id          UUID;
        v_warehouse_id       UUID;
        v_payment_condition  UUID;
        v_tenant_code        VARCHAR;
        v_product_code       VARCHAR;
        v_product_name       VARCHAR;
        v_unit_price         NUMERIC(10,2);
        v_list_price         NUMERIC(10,2);
        v_quantity           INTEGER;
        v_order_subtotal     NUMERIC(10,2);
        v_items_count        INTEGER;
        v_item_subtotal      NUMERIC(10,2);
    BEGIN

        FOR v_index IN 1..103
            LOOP

            -- ==========================================
            -- DISTRIBUIÇÃO MAIS NATURAL DOS TENANTS
            -- ==========================================
                CASE MOD(v_index, 3)
                    WHEN 0 THEN
                        v_tenant_code := 'FARMA-DEFAULT';
                        v_buyer_id := '00000000-0000-0000-0003-000000000001';
                        v_seller_id := '00000000-0000-0000-0001-000000000001';
                        v_warehouse_id := '00000000-0000-0000-0002-000000000001';
                        v_payment_condition := '00000000-0000-0000-0004-000000000001';

                    WHEN 1 THEN
                        v_tenant_code := 'FARMA-PREMIUM';
                        v_buyer_id := '00000000-0000-0000-0003-000000000005';
                        v_seller_id := '00000000-0000-0000-0001-000000000003';
                        v_warehouse_id := '00000000-0000-0000-0002-000000000003';
                        v_payment_condition := '00000000-0000-0000-0004-000000000005';

                    ELSE
                        v_tenant_code := 'FARMA-ECONOMIA';
                        v_buyer_id := '00000000-0000-0000-0003-000000000008';
                        v_seller_id := '00000000-0000-0000-0001-000000000004';
                        v_warehouse_id := '00000000-0000-0000-0002-000000000004';
                        v_payment_condition := '00000000-0000-0000-0004-000000000007';
                    END CASE;

                v_order_id := (
                    '00000000-0000-0000-0005-' || LPAD(v_index::TEXT, 12, '0')
                    )::UUID;

                INSERT INTO "order"
                (
                    id,
                    external_reference,
                    buyer_id,
                    seller_id,
                    warehouse_id,
                    payment_condition_id,
                    status,
                    subtotal,
                    discount_value,
                    total,
                    origin,
                    tenant_code,
                    created_at
                )
                VALUES
                    (
                        v_order_id,
                        'ORD-' || LPAD(v_index::TEXT, 5, '0'),
                        v_buyer_id,
                        v_seller_id,
                        v_warehouse_id,
                        v_payment_condition,
                        CASE
                            WHEN MOD(v_index, 12) = 0 THEN 'CANCELLED'
                            WHEN MOD(v_index, 5) = 0 THEN 'PENDING'
                            ELSE 'CONFIRMED'
                            END,
                        0,
                        0,
                        0,
                        CASE
                            WHEN MOD(v_index, 3) = 0 THEN 'API'
                            WHEN MOD(v_index, 3) = 1 THEN 'MOBILE'
                            ELSE 'SYNC'
                            END,
                        v_tenant_code,
                        NOW() - ((RANDOM() * 120)::INT || ' days')::INTERVAL
                    );

                v_order_subtotal := 0;

                -- ==========================================
                -- ENTRE 1 E 4 ITENS POR PEDIDO
                -- ==========================================
                v_items_count := FLOOR(RANDOM() * 4 + 1);

                FOR i IN 1..v_items_count
                    LOOP

                        SELECT
                            product_code,
                            product_name,
                            unit_price,
                            list_price
                        INTO
                            v_product_code,
                            v_product_name,
                            v_unit_price,
                            v_list_price
                        FROM product_price
                        WHERE tenant_code = v_tenant_code
                        ORDER BY RANDOM()
                        LIMIT 1;

                        v_quantity := FLOOR(RANDOM() * 10 + 1);

                        v_item_subtotal := v_quantity * v_unit_price;
                        v_order_subtotal := v_order_subtotal + v_item_subtotal;

                        INSERT INTO order_item
                        (
                            order_id,
                            product_code,
                            product_name,
                            quantity,
                            unit_price,
                            list_price,
                            subtotal
                        )
                        VALUES
                            (
                                v_order_id,
                                v_product_code,
                                v_product_name,
                                v_quantity,
                                v_unit_price,
                                v_list_price,
                                v_item_subtotal
                            );

                    END LOOP;

                UPDATE "order"
                SET subtotal = ROUND(v_order_subtotal, 2),
                    discount_value = ROUND(v_order_subtotal * 0.05, 2),
                    total = ROUND(v_order_subtotal - (v_order_subtotal * 0.05), 2)
                WHERE id = v_order_id;

            END LOOP;

    END
$$;