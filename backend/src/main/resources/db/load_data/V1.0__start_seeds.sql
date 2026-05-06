-- =======================
-- TENANTS
-- =======================
INSERT INTO tenant (code, name, enabled) VALUES
('TENANT_001', 'Tech Solutions Brasil', true),
('TENANT_002', 'Varejo Premium', true),
('TENANT_003', 'Distribuição Nacional', true);

-- =======================
-- PAYMENT CONDITIONS
-- =======================
INSERT INTO payment_condition (code, description, max_installments, discount_percentage, tenant_code, enabled) VALUES
('T001_NET_30', 'Prazo de 30 dias', 1, 0, 'TENANT_001', true),
('T002_NET_30', 'Prazo de 30 dias', 1, 0, 'TENANT_002', true),
('T003_NET_30', 'Prazo de 30 dias', 1, 0, 'TENANT_003', true),
('T001_NET_60', 'Prazo de 60 dias', 1, 2.5, 'TENANT_001', true),
('T002_NET_60', 'Prazo de 60 dias', 1, 2.5, 'TENANT_002', true),
('T003_NET_60', 'Prazo de 60 dias', 1, 2.5, 'TENANT_003', true),
('T001_INST_3X', '3x Parcelado', 3, 1.5, 'TENANT_001', true),
('T002_INST_3X', '3x Parcelado', 3, 1.5, 'TENANT_002', true),
('T003_INST_3X', '3x Parcelado', 3, 1.5, 'TENANT_003', true),
('T001_INST_6X', '6x Parcelado', 6, 0.5, 'TENANT_001', true),
('T002_INST_6X', '6x Parcelado', 6, 0.5, 'TENANT_002', true),
('T003_INST_6X', '6x Parcelado', 6, 0.5, 'TENANT_003', true),
('T001_IMMEDIATE', 'À vista', 1, 5.0, 'TENANT_001', true),
('T002_IMMEDIATE', 'À vista', 1, 5.0, 'TENANT_002', true),
('T003_IMMEDIATE', 'À vista', 1, 5.0, 'TENANT_003', true);

-- =======================
-- SELLERS - TENANT_001
-- =======================
INSERT INTO seller (external_reference, name, tenant_code, enabled) VALUES
('SELL_001', 'Eletrônicos XYZ', 'TENANT_001', true),
('SELL_002', 'Distribuição ABC', 'TENANT_001', true);

-- =======================
-- SELLERS - TENANT_002
-- =======================
INSERT INTO seller (external_reference, name, tenant_code, enabled) VALUES
('SELL_003', 'Varejo Premium SP', 'TENANT_002', true),
('SELL_004', 'Comércio Geral MG', 'TENANT_002', true);

-- =======================
-- SELLERS - TENANT_003
-- =======================
INSERT INTO seller (external_reference, name, tenant_code, enabled) VALUES
('SELL_005', 'Distribuição Nacional RJ', 'TENANT_003', true);

-- =======================
-- WAREHOUSES
-- =======================
INSERT INTO warehouse (external_reference, name, seller_id, tenant_code, enabled) VALUES
('WH_001', 'Armazém São Paulo', (SELECT id FROM seller WHERE external_reference = 'SELL_001'), 'TENANT_001', true),
('WH_002', 'Armazém Rio de Janeiro', (SELECT id FROM seller WHERE external_reference = 'SELL_002'), 'TENANT_001', true),
('WH_003', 'Armazém Belo Horizonte', (SELECT id FROM seller WHERE external_reference = 'SELL_003'), 'TENANT_002', true),
('WH_004', 'Armazém Brasília', (SELECT id FROM seller WHERE external_reference = 'SELL_004'), 'TENANT_002', true),
('WH_005', 'Centro de Distribuição Curitiba', (SELECT id FROM seller WHERE external_reference = 'SELL_005'), 'TENANT_003', true);

-- =======================
-- PRODUCT PRICES - Warehouse 1
-- =======================
INSERT INTO product_price (product_code, product_name, warehouse_id, unit_price, list_price, tenant_code, enabled) VALUES
('PROD_001', 'Notebook Core i5', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 2800.00, 3500.00, 'TENANT_001', true),
('PROD_002', 'Mouse Sem Fio', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 45.00, 79.99, 'TENANT_001', true),
('PROD_003', 'Teclado Mecânico', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 250.00, 399.99, 'TENANT_001', true),
('PROD_004', 'Monitor 24 polegadas', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 650.00, 899.99, 'TENANT_001', true),
('PROD_005', 'Webcam Full HD', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 180.00, 299.99, 'TENANT_001', true),
('PROD_006', 'Headset Gamer', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 320.00, 499.99, 'TENANT_001', true),
('PROD_007', 'SSD 240GB', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 200.00, 319.99, 'TENANT_001', true),
('PROD_008', 'HD Externo 1TB', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 220.00, 349.99, 'TENANT_001', true),
('PROD_009', 'Cabo USB Tipo C', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 25.00, 49.99, 'TENANT_001', true),
('PROD_010', 'Carregador Rápido', (SELECT id FROM warehouse WHERE external_reference = 'WH_001'), 85.00, 149.99, 'TENANT_001', true);

-- =======================
-- PRODUCT PRICES - Warehouse 2
-- =======================
INSERT INTO product_price (product_code, product_name, warehouse_id, unit_price, list_price, tenant_code, enabled) VALUES
('PROD_011', 'Tablet 10 polegadas', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 900.00, 1299.99, 'TENANT_001', true),
('PROD_012', 'Pen Drive 64GB', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 35.00, 69.99, 'TENANT_001', true),
('PROD_013', 'Adaptador HDMI', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 40.00, 79.99, 'TENANT_001', true),
('PROD_014', 'Power Bank 20000mAh', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 120.00, 199.99, 'TENANT_001', true),
('PROD_015', 'Protetor Surto', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 150.00, 249.99, 'TENANT_001', true),
('PROD_016', 'Cabo de Rede Cat6', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 30.00, 59.99, 'TENANT_001', true),
('PROD_017', 'Roteador Wi-Fi 6', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 480.00, 699.99, 'TENANT_001', true),
('PROD_018', 'Switch 24 Portas', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 800.00, 1199.99, 'TENANT_001', true),
('PROD_019', 'Modem 5G', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 600.00, 899.99, 'TENANT_001', true),
('PROD_020', 'Webcam com Microfone', (SELECT id FROM warehouse WHERE external_reference = 'WH_002'), 250.00, 399.99, 'TENANT_001', true);

-- =======================
-- PRODUCT PRICES - Warehouse 3
-- =======================
INSERT INTO product_price (product_code, product_name, warehouse_id, unit_price, list_price, tenant_code, enabled) VALUES
('PROD_021', 'Impressora Jato de Tinta', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 450.00, 699.99, 'TENANT_002', true),
('PROD_022', 'Toner Preto', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 120.00, 189.99, 'TENANT_002', true),
('PROD_023', 'Papel A4 Resma', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 18.00, 29.99, 'TENANT_002', true),
('PROD_024', 'Cartuchos Colorido', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 85.00, 149.99, 'TENANT_002', true),
('PROD_025', 'Scanner de CNPJ', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 280.00, 449.99, 'TENANT_002', true),
('PROD_026', 'Leitor Biométrico', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 350.00, 549.99, 'TENANT_002', true),
('PROD_027', 'Caixa Registradora', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 1200.00, 1899.99, 'TENANT_002', true),
('PROD_028', 'Leitor de Código de Barras', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 280.00, 449.99, 'TENANT_002', true),
('PROD_029', 'Terminal PDV', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 950.00, 1499.99, 'TENANT_002', true),
('PROD_030', 'Impressora Térmica', (SELECT id FROM warehouse WHERE external_reference = 'WH_003'), 380.00, 599.99, 'TENANT_002', true);

-- =======================
-- PRODUCT PRICES - Warehouse 4
-- =======================
INSERT INTO product_price (product_code, product_name, warehouse_id, unit_price, list_price, tenant_code, enabled) VALUES
('PROD_031', 'Câmera de Segurança 2MP', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 280.00, 449.99, 'TENANT_002', true),
('PROD_032', 'DVR 4 Canais', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 450.00, 699.99, 'TENANT_002', true),
('PROD_033', 'Cabo Coaxial 100m', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 85.00, 149.99, 'TENANT_002', true),
('PROD_034', 'Fonte 12V 5A', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 65.00, 119.99, 'TENANT_002', true),
('PROD_035', 'Conectores RCA', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 15.00, 29.99, 'TENANT_002', true),
('PROD_036', 'NVR IP 8 Canais', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 1200.00, 1899.99, 'TENANT_002', true),
('PROD_037', 'Câmera IP 4MP', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 450.00, 699.99, 'TENANT_002', true),
('PROD_038', 'HD Vigilância 2TB', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 350.00, 549.99, 'TENANT_002', true),
('PROD_039', 'HD Vigilância 4TB', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 550.00, 849.99, 'TENANT_002', true),
('PROD_040', 'Cofre de Segurança', (SELECT id FROM warehouse WHERE external_reference = 'WH_004'), 800.00, 1299.99, 'TENANT_002', true);

-- =======================
-- PRODUCT PRICES - Warehouse 5
-- =======================
INSERT INTO product_price (product_code, product_name, warehouse_id, unit_price, list_price, tenant_code, enabled) VALUES
('PROD_041', 'Móvel para Servidor', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 3500.00, 5499.99, 'TENANT_003', true),
('PROD_042', 'Patch Panel 48 Portas', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 450.00, 699.99, 'TENANT_003', true),
('PROD_043', 'Nobreak 10kVA', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 4500.00, 6999.99, 'TENANT_003', true),
('PROD_044', 'Fusível de Proteção', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 25.00, 49.99, 'TENANT_003', true),
('PROD_045', 'Disjuntor DIN', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 85.00, 149.99, 'TENANT_003', true),
('PROD_046', 'Canaleta de Fiação', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 45.00, 79.99, 'TENANT_003', true),
('PROD_047', 'Tomada de Piso', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 120.00, 199.99, 'TENANT_003', true),
('PROD_048', 'Iluminação LED Industrial', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 280.00, 449.99, 'TENANT_003', true),
('PROD_049', 'Ar Condicionado 30k', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 3200.00, 4999.99, 'TENANT_003', true),
('PROD_050', 'Exaustor Industrial', (SELECT id FROM warehouse WHERE external_reference = 'WH_005'), 850.00, 1299.99, 'TENANT_003', true);

-- =======================
-- BUYERS - TENANT_001
-- =======================
INSERT INTO buyer (external_reference, name, credit_limit, tenant_code, enabled) VALUES
('BUYER_001', 'Loja TechCenter SP', 50000.00, 'TENANT_001', true),
('BUYER_002', 'Varejo Fast Computadores', 75000.00, 'TENANT_001', true),
('BUYER_003', 'Distribuição Técnica RJ', 100000.00, 'TENANT_001', true);

-- =======================
-- BUYERS - TENANT_002
-- =======================
INSERT INTO buyer (external_reference, name, credit_limit, tenant_code, enabled) VALUES
('BUYER_004', 'Supermercado Vale Premium', 150000.00, 'TENANT_002', true),
('BUYER_005', 'Loja Geral MG', 80000.00, 'TENANT_002', true),
('BUYER_006', 'Polos Comercial', 120000.00, 'TENANT_002', true),
('BUYER_007', 'Varejo Express', 60000.00, 'TENANT_002', true);

-- =======================
-- BUYERS - TENANT_003
-- =======================
INSERT INTO buyer (external_reference, name, credit_limit, tenant_code, enabled) VALUES
('BUYER_008', 'Grande Distribuição BR', 200000.00, 'TENANT_003', true),
('BUYER_009', 'Comércio Nacional', 180000.00, 'TENANT_003', true),
('BUYER_010', 'Logística do Brasil', 160000.00, 'TENANT_003', true);

-- =======================
-- ORDERS - TENANT_001 - 35 orders
-- =======================
INSERT INTO "order" (external_reference, buyer_id, seller_id, warehouse_id, payment_condition_id, status, subtotal, discount_value, total, origin, tenant_code)
SELECT
  'ORDER_' || LPAD(CAST(ROW_NUMBER() OVER (ORDER BY b.id) AS VARCHAR), 4, '0') || '_' || SUBSTRING(CAST(gen_random_uuid()::TEXT AS TEXT), 1, 8),
  b.id,
  s.id,
  w.id,
  pc.id,
  CASE WHEN random() < 0.5 THEN 'PENDING' ELSE 'CONFIRMED' END,
  ROUND((random() * 15000 + 500)::NUMERIC, 2),
  ROUND((random() * 2000)::NUMERIC, 2),
  ROUND((random() * 15000 + 500)::NUMERIC, 2),
  'API',
  'TENANT_001'
FROM
  buyer b,
  seller s,
  warehouse w,
  payment_condition pc
WHERE
  b.tenant_code = 'TENANT_001'
  AND s.tenant_code = 'TENANT_001'
  AND w.tenant_code = 'TENANT_001'
  AND pc.tenant_code = 'TENANT_001'
  AND s.id = w.seller_id
LIMIT 35;

-- =======================
-- ORDERS - TENANT_002 - 40 orders
-- =======================
INSERT INTO "order" (external_reference, buyer_id, seller_id, warehouse_id, payment_condition_id, status, subtotal, discount_value, total, origin, tenant_code)
SELECT
  'ORDER_' || LPAD(CAST(ROW_NUMBER() OVER (ORDER BY b.id) AS VARCHAR), 4, '0') || '_' || SUBSTRING(CAST(gen_random_uuid()::TEXT AS TEXT), 1, 8),
  b.id,
  s.id,
  w.id,
  pc.id,
  CASE WHEN random() < 0.7 THEN 'CONFIRMED' WHEN random() < 0.9 THEN 'PENDING' ELSE 'COMPLETED' END,
  ROUND((random() * 20000 + 1000)::NUMERIC, 2),
  ROUND((random() * 3000)::NUMERIC, 2),
  ROUND((random() * 20000 + 1000)::NUMERIC, 2),
  'API',
  'TENANT_002'
FROM
  buyer b,
  seller s,
  warehouse w,
  payment_condition pc
WHERE
  b.tenant_code = 'TENANT_002'
  AND s.tenant_code = 'TENANT_002'
  AND w.tenant_code = 'TENANT_002'
  AND pc.tenant_code = 'TENANT_002'
  AND s.id = w.seller_id
LIMIT 40;

-- =======================
-- ORDERS - TENANT_003 - 35 orders
-- =======================
INSERT INTO "order" (external_reference, buyer_id, seller_id, warehouse_id, payment_condition_id, status, subtotal, discount_value, total, origin, tenant_code)
SELECT
  'ORDER_' || LPAD(CAST(ROW_NUMBER() OVER (ORDER BY b.id) AS VARCHAR), 4, '0') || '_' || SUBSTRING(CAST(gen_random_uuid()::TEXT AS TEXT), 1, 8),
  b.id,
  s.id,
  w.id,
  pc.id,
  CASE WHEN random() < 0.4 THEN 'PENDING' WHEN random() < 0.8 THEN 'CONFIRMED' ELSE 'COMPLETED' END,
  ROUND((random() * 25000 + 2000)::NUMERIC, 2),
  ROUND((random() * 4000)::NUMERIC, 2),
  ROUND((random() * 25000 + 2000)::NUMERIC, 2),
  'API',
  'TENANT_003'
FROM
  buyer b,
  seller s,
  warehouse w,
  payment_condition pc
WHERE
  b.tenant_code = 'TENANT_003'
  AND s.tenant_code = 'TENANT_003'
  AND w.tenant_code = 'TENANT_003'
  AND pc.tenant_code = 'TENANT_003'
  AND s.id = w.seller_id
LIMIT 35;

-- =======================
-- ORDER ITEMS - TENANT_001
-- =======================
INSERT INTO order_item (order_id, product_code, product_name, quantity, unit_price, list_price, subtotal)
SELECT
  o.id,
  CASE
    WHEN random() < 0.2 THEN 'PROD_001'
    WHEN random() < 0.4 THEN 'PROD_002'
    WHEN random() < 0.6 THEN 'PROD_003'
    WHEN random() < 0.8 THEN 'PROD_004'
    ELSE 'PROD_005'
  END,
  CASE
    WHEN random() < 0.2 THEN 'Notebook Core i5'
    WHEN random() < 0.4 THEN 'Mouse Sem Fio'
    WHEN random() < 0.6 THEN 'Teclado Mecânico'
    WHEN random() < 0.8 THEN 'Monitor 24 polegadas'
    ELSE 'Webcam Full HD'
  END,
  (random() * 10 + 1)::INT,
  ROUND((random() * 3000 + 100)::NUMERIC, 4),
  ROUND((random() * 3500 + 150)::NUMERIC, 4),
  ROUND((random() * 10000 + 500)::NUMERIC, 2)
FROM "order" o
WHERE o.tenant_code = 'TENANT_001' AND random() < 0.9
LIMIT 70;

-- =======================
-- ORDER ITEMS - TENANT_002
-- =======================
INSERT INTO order_item (order_id, product_code, product_name, quantity, unit_price, list_price, subtotal)
SELECT
  o.id,
  CASE
    WHEN random() < 0.2 THEN 'PROD_021'
    WHEN random() < 0.4 THEN 'PROD_022'
    WHEN random() < 0.6 THEN 'PROD_023'
    WHEN random() < 0.8 THEN 'PROD_031'
    ELSE 'PROD_032'
  END,
  CASE
    WHEN random() < 0.2 THEN 'Impressora Jato de Tinta'
    WHEN random() < 0.4 THEN 'Toner Preto'
    WHEN random() < 0.6 THEN 'Papel A4 Resma'
    WHEN random() < 0.8 THEN 'Câmera de Segurança 2MP'
    ELSE 'DVR 4 Canais'
  END,
  (random() * 15 + 1)::INT,
  ROUND((random() * 1500 + 50)::NUMERIC, 4),
  ROUND((random() * 2000 + 100)::NUMERIC, 4),
  ROUND((random() * 15000 + 1000)::NUMERIC, 2)
FROM "order" o
WHERE o.tenant_code = 'TENANT_002' AND random() < 0.85
LIMIT 80;

-- =======================
-- ORDER ITEMS - TENANT_003
-- =======================
INSERT INTO order_item (order_id, product_code, product_name, quantity, unit_price, list_price, subtotal)
SELECT
  o.id,
  CASE
    WHEN random() < 0.2 THEN 'PROD_041'
    WHEN random() < 0.4 THEN 'PROD_043'
    WHEN random() < 0.6 THEN 'PROD_048'
    WHEN random() < 0.8 THEN 'PROD_049'
    ELSE 'PROD_050'
  END,
  CASE
    WHEN random() < 0.2 THEN 'Móvel para Servidor'
    WHEN random() < 0.4 THEN 'Nobreak 10kVA'
    WHEN random() < 0.6 THEN 'Iluminação LED Industrial'
    WHEN random() < 0.8 THEN 'Ar Condicionado 30k'
    ELSE 'Exaustor Industrial'
  END,
  (random() * 5 + 1)::INT,
  ROUND((random() * 5000 + 500)::NUMERIC, 4),
  ROUND((random() * 6000 + 800)::NUMERIC, 4),
  ROUND((random() * 20000 + 2000)::NUMERIC, 2)
FROM "order" o
WHERE o.tenant_code = 'TENANT_003' AND random() < 0.95
LIMIT 70;

