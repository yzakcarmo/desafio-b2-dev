import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createOrder } from '../api/orders'
import { ApiError } from '../api/client'
import { useTenant } from '../context/TenantContext'
import { getMockData } from '../data/mockData'
import ApiErrorBanner from '../components/ApiErrorBanner'

interface Item {
  productCode: string
  quantity: number
}

interface FormErrors {
  externalReference?: string
  buyerReference?: string
  sellerReference?: string
  warehouseReference?: string
  paymentConditionCode?: string
  items?: string
}

function validate(form: Record<string, string>, items: Item[]): FormErrors {
  const errors: FormErrors = {}
  if (!form.externalReference.trim()) errors.externalReference = 'Campo obrigatório'
  if (!form.buyerReference) errors.buyerReference = 'Selecione um comprador'
  if (!form.sellerReference) errors.sellerReference = 'Selecione um vendedor'
  if (!form.warehouseReference) errors.warehouseReference = 'Selecione um armazém'
  if (!form.paymentConditionCode) errors.paymentConditionCode = 'Selecione uma condição de pagamento'
  if (items.some(i => !i.productCode)) errors.items = 'Selecione o produto em todos os itens'
  if (items.some(i => i.quantity < 1)) errors.items = 'Quantidade deve ser maior que zero'
  return errors
}

export default function CreateOrder() {
  const navigate = useNavigate()
  const { tenant } = useTenant()
  const mockData = getMockData(tenant)

  const [submitting, setSubmitting] = useState(false)
  const [apiError, setApiError] = useState<ApiError | null>(null)
  const [formErrors, setFormErrors] = useState<FormErrors>({})
  const [form, setForm] = useState({
    externalReference: '',
    buyerReference: '',
    sellerReference: '',
    warehouseReference: '',
    paymentConditionCode: '',
  })
  const [items, setItems] = useState<Item[]>([{ productCode: '', quantity: 1 }])

  function setField(field: keyof typeof form, value: string) {
    setForm(f => ({ ...f, [field]: value }))
    setFormErrors(e => ({ ...e, [field]: undefined }))
  }

  function addItem() {
    setItems(its => [...its, { productCode: '', quantity: 1 }])
  }

  function removeItem(index: number) {
    setItems(its => its.filter((_, i) => i !== index))
  }

  function updateItem(index: number, field: keyof Item, value: string | number) {
    setItems(its => its.map((item, i) => i === index ? { ...item, [field]: value } : item))
    setFormErrors(e => ({ ...e, items: undefined }))
  }

  async function handleSubmit(e: React.SyntheticEvent<HTMLFormElement>) {
    e.preventDefault()
    const errors = validate(form, items)
    if (Object.keys(errors).length > 0) {
      setFormErrors(errors)
      return
    }

    setSubmitting(true)
    setApiError(null)
    try {
      const response = await createOrder({ ...form, items })
      navigate(`/orders/${response.data.externalReference}`, { state: { creationResult: response } })
    } catch (e: unknown) {
      if (e instanceof ApiError) setApiError(e)
      else setApiError(new ApiError({ status: 0, code: 'UNKNOWN', message: 'Erro desconhecido' }))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="p-6 max-w-2xl">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Novo Pedido</h1>

      {apiError && <ApiErrorBanner error={apiError} />}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="bg-white rounded-xl border border-gray-200 p-6 space-y-4">
          <h2 className="text-sm font-semibold text-gray-700 uppercase tracking-wide border-b border-gray-100 pb-3">
            Dados do Pedido
          </h2>

          <Field label="Referência Externa" error={formErrors.externalReference}>
            <input
              type="text"
              value={form.externalReference}
              onChange={e => setField('externalReference', e.target.value)}
              placeholder="ex: ORD-2025-001"
              className={inputClass(!!formErrors.externalReference)}
            />
          </Field>

          <div className="grid grid-cols-2 gap-4">
            <Field label="Comprador" error={formErrors.buyerReference}>
              <select value={form.buyerReference} onChange={e => setField('buyerReference', e.target.value)} className={inputClass(!!formErrors.buyerReference)}>
                <option value="">Selecione...</option>
                {mockData.buyers.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
              </select>
            </Field>
            <Field label="Vendedor" error={formErrors.sellerReference}>
              <select value={form.sellerReference} onChange={e => setField('sellerReference', e.target.value)} className={inputClass(!!formErrors.sellerReference)}>
                <option value="">Selecione...</option>
                {mockData.sellers.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
              </select>
            </Field>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Field label="Armazém" error={formErrors.warehouseReference}>
              <select value={form.warehouseReference} onChange={e => setField('warehouseReference', e.target.value)} className={inputClass(!!formErrors.warehouseReference)}>
                <option value="">Selecione...</option>
                {mockData.warehouses.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
              </select>
            </Field>
            <Field label="Condição de Pagamento" error={formErrors.paymentConditionCode}>
              <select value={form.paymentConditionCode} onChange={e => setField('paymentConditionCode', e.target.value)} className={inputClass(!!formErrors.paymentConditionCode)}>
                <option value="">Selecione...</option>
                {mockData.paymentConditions.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
              </select>
            </Field>
          </div>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 p-6 space-y-4">
          <div className="flex items-center justify-between border-b border-gray-100 pb-3">
            <h2 className="text-sm font-semibold text-gray-700 uppercase tracking-wide">Itens</h2>
            <button type="button" onClick={addItem} className="text-sm text-blue-600 hover:text-blue-800 font-medium">
              + Adicionar item
            </button>
          </div>

          {formErrors.items && (
            <p className="text-xs text-red-600">{formErrors.items}</p>
          )}

          {items.map((item, index) => (
            <div key={index} className="flex gap-3 items-end">
              <div className="flex-1">
                {index === 0 && <label className="block text-xs font-medium text-gray-500 mb-1">Produto</label>}
                <select
                  value={item.productCode}
                  onChange={e => updateItem(index, 'productCode', e.target.value)}
                  className={inputClass(!!formErrors.items && !item.productCode)}
                >
                  <option value="">Selecione...</option>
                  {mockData.products.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              </div>
              <div className="w-28">
                {index === 0 && <label className="block text-xs font-medium text-gray-500 mb-1">Quantidade</label>}
                <input
                  type="number"
                  min={1}
                  value={item.quantity}
                  onChange={e => updateItem(index, 'quantity', Math.max(1, parseInt(e.target.value) || 1))}
                  className={inputClass(!!formErrors.items && item.quantity < 1)}
                />
              </div>
              <button
                type="button"
                onClick={() => removeItem(index)}
                disabled={items.length === 1}
                className="pb-0.5 text-gray-400 hover:text-red-500 disabled:opacity-20 text-lg leading-none"
              >
                ✕
              </button>
            </div>
          ))}
        </div>

        <div className="flex justify-end gap-3">
          <button type="button" onClick={() => navigate('/orders')} className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg border border-gray-300">
            Cancelar
          </button>
          <button type="submit" disabled={submitting} className="px-6 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 disabled:opacity-50">
            {submitting ? 'Criando...' : 'Criar Pedido'}
          </button>
        </div>
      </form>
    </div>
  )
}

function inputClass(hasError: boolean) {
  return `w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
    hasError ? 'border-red-400 bg-red-50' : 'border-gray-300'
  }`
}

function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
      {children}
      {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
    </div>
  )
}

