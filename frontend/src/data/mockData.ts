export interface SelectOption {
  value: string
  label: string
}

interface TenantData {
  sellers: SelectOption[]
  warehouses: SelectOption[]
  buyers: SelectOption[]
  paymentConditions: SelectOption[]
  products: SelectOption[]
}

const data: Record<string, TenantData> = {
  'FARMA-DEFAULT': {
    sellers: [
      { value: 'SELLER-001', label: 'SELLER-001 — Distribuidora Norte Ltda' },
      { value: 'SELLER-002', label: 'SELLER-002 — Distribuidora Sul S.A.' },
      { value: 'SELLER-005', label: 'SELLER-005 — Distribuidora Centro-Oeste' },
    ],
    warehouses: [
      { value: 'WH-001', label: 'WH-001 — CD São Paulo' },
      { value: 'WH-002', label: 'WH-002 — CD Rio de Janeiro' },
      { value: 'WH-005', label: 'WH-005 — CD Belo Horizonte' },
    ],
    buyers: [
      { value: 'BUYER-001', label: 'BUYER-001 — Farmácia Central Ltda' },
      { value: 'BUYER-002', label: 'BUYER-002 — Drogaria São João' },
      { value: 'BUYER-003', label: 'BUYER-003 — Farmácia Popular do Bairro' },
      { value: 'BUYER-004', label: 'BUYER-004 — Drogaria Bem Estar' },
    ],
    paymentConditions: [
      { value: 'A-VISTA',   label: 'A-VISTA — Pagamento à Vista (2% desc.)' },
      { value: '30-DIAS',   label: '30-DIAS — Pagamento em 30 dias' },
      { value: '30-60',     label: '30-60 — Parcelado 30/60 dias' },
      { value: '30-60-90',  label: '30-60-90 — Parcelado 30/60/90 dias' },
    ],
    products: [
      { value: 'PROD-001', label: 'PROD-001 — Dipirona 500mg cx/20cp' },
      { value: 'PROD-002', label: 'PROD-002 — Amoxicilina 500mg cx/21cp' },
      { value: 'PROD-003', label: 'PROD-003 — Omeprazol 20mg cx/28cp' },
      { value: 'PROD-004', label: 'PROD-004 — Losartana 50mg cx/30cp' },
      { value: 'PROD-005', label: 'PROD-005 — Metformina 850mg cx/30cp' },
      { value: 'PROD-006', label: 'PROD-006 — Atorvastatina 20mg cx/30cp' },
      { value: 'PROD-007', label: 'PROD-007 — Azitromicina 500mg cx/3cp' },
      { value: 'PROD-008', label: 'PROD-008 — Ibuprofeno 600mg cx/20cp' },
      { value: 'PROD-009', label: 'PROD-009 — Paracetamol 750mg cx/20cp' },
      { value: 'PROD-010', label: 'PROD-010 — Captopril 25mg cx/30cp' },
      { value: 'PROD-039', label: 'PROD-039 — Vitamina D3 2000UI cx/30cp' },
      { value: 'PROD-040', label: 'PROD-040 — Vitamina C 1g efervescente cx/10' },
      { value: 'PROD-041', label: 'PROD-041 — Ômega 3 1000mg cx/60cp' },
    ],
  },
  'FARMA-PREMIUM': {
    sellers: [
      { value: 'SELLER-003', label: 'SELLER-003 — Pharma Premium Distribuidora' },
    ],
    warehouses: [
      { value: 'WH-003', label: 'WH-003 — CD Premium Campinas' },
    ],
    buyers: [
      { value: 'BUYER-005', label: 'BUYER-005 — Rede Premium Saúde S.A.' },
      { value: 'BUYER-006', label: 'BUYER-006 — Clínica Vida Premium' },
      { value: 'BUYER-007', label: 'BUYER-007 — Hospital São Lucas Premium' },
    ],
    paymentConditions: [
      { value: 'PREM-VISTA', label: 'PREM-VISTA — Premium à Vista (5% desc.)' },
      { value: 'PREM-30',    label: 'PREM-30 — Premium 30 dias' },
    ],
    products: [
      { value: 'PROD-019', label: 'PROD-019 — Insulina Glargina 100UI/mL' },
      { value: 'PROD-020', label: 'PROD-020 — Adalimumabe 40mg inj' },
      { value: 'PROD-021', label: 'PROD-021 — Rivaroxabana 20mg cx/28cp' },
      { value: 'PROD-022', label: 'PROD-022 — Apixabana 5mg cx/60cp' },
      { value: 'PROD-023', label: 'PROD-023 — Empagliflozina 10mg cx/30cp' },
      { value: 'PROD-027', label: 'PROD-027 — Semaglutida 0.5mg inj' },
      { value: 'PROD-028', label: 'PROD-028 — Dupilumabe 300mg inj' },
    ],
  },
  'FARMA-ECONOMIA': {
    sellers: [
      { value: 'SELLER-004', label: 'SELLER-004 — Economia Atacado Ltda' },
    ],
    warehouses: [
      { value: 'WH-004', label: 'WH-004 — CD Economia Goiânia' },
    ],
    buyers: [
      { value: 'BUYER-008', label: 'BUYER-008 — Farmácia Economia Total' },
      { value: 'BUYER-009', label: 'BUYER-009 — Drogaria Barato Já' },
      { value: 'BUYER-010', label: 'BUYER-010 — Farmácia do Trabalhador' },
    ],
    paymentConditions: [
      { value: 'ECO-VISTA', label: 'ECO-VISTA — Economia à Vista (2% desc.)' },
      { value: 'ECO-30',    label: 'ECO-30 — Economia 30 dias' },
    ],
    products: [
      { value: 'PROD-029', label: 'PROD-029 — Dipirona Gotas 500mg/mL 10mL' },
      { value: 'PROD-030', label: 'PROD-030 — Paracetamol 500mg cx/20cp' },
      { value: 'PROD-031', label: 'PROD-031 — Amoxicilina 250mg/5mL susp' },
      { value: 'PROD-032', label: 'PROD-032 — Ibuprofeno 300mg cx/20cp' },
      { value: 'PROD-033', label: 'PROD-033 — Loratadina 10mg cx/12cp' },
      { value: 'PROD-034', label: 'PROD-034 — Dexametasona 4mg cx/6cp' },
      { value: 'PROD-035', label: 'PROD-035 — Metronidazol 400mg cx/14cp' },
    ],
  },
}

export function getMockData(tenant: string): TenantData {
  return data[tenant] ?? data['FARMA-DEFAULT']
}
