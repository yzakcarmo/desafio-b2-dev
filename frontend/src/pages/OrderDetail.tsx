import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getOrder, cancelOrder } from '../api/orders'
import type { OrderDetail as OrderDetailType } from '../types'
import StatusBadge from '../components/StatusBadge'
import { formatCurrency, formatDate } from '../utils/format'

export default function OrderDetail() {
  const { ref } = useParams<{ ref: string }>()

  const [order, setOrder] = useState<OrderDetailType | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [cancelling, setCancelling] = useState(false)

  useEffect(() => {
    if (!ref) return
    getOrder(ref)
      .then(setOrder)
      .catch(e => setError(e instanceof Error ? e.message : 'Erro desconhecido'))
      .finally(() => setLoading(false))
  }, [ref])

  async function handleCancel() {
    if (!order || !confirm('Confirmar cancelamento do pedido?')) return
    setCancelling(true)
    try {
      await cancelOrder(order.externalReference)
      setOrder(prev => prev ? { ...prev, status: 'CANCELLED' } : null)
    } catch (e: unknown) {
      alert(e instanceof Error ? e.message : 'Erro ao cancelar')
    } finally {
      setCancelling(false)
    }
  }

  if (loading) return <div className="p-6 text-gray-400 text-sm">Carregando...</div>
  if (error) return (
    <div className="p-6">
      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>
    </div>
  )
  if (!order) return null

  const canCancel = order.status === 'PENDING' || order.status === 'CONFIRMED'

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-4">
          <Link to="/orders" className="text-gray-400 hover:text-gray-600 text-sm">← Voltar</Link>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{order.externalReference}</h1>
            <div className="flex items-center gap-2 mt-1">
              <StatusBadge status={order.status} />
              <span className="text-xs text-gray-400">Origem: {order.origin}</span>
            </div>
          </div>
        </div>
        {canCancel && (
          <button
            onClick={handleCancel}
            disabled={cancelling}
            className="px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-lg hover:bg-red-700 disabled:opacity-50"
          >
            {cancelling ? 'Cancelando...' : 'Cancelar Pedido'}
          </button>
        )}
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl border border-gray-200 p-6 space-y-3">
          <h2 className="text-sm font-semibold text-gray-700 uppercase tracking-wide border-b border-gray-100 pb-3">
            Valores
          </h2>
          <InfoRow label="Subtotal" value={formatCurrency(order.subtotal)} />
          <InfoRow label="Desconto" value={formatCurrency(order.discountValue)} valueClass="text-red-600" />
          <InfoRow label="Total" value={formatCurrency(order.total)} valueClass="text-green-700 font-bold text-base" />
          <InfoRow label="Criado em" value={formatDate(order.createdAt)} />
          <InfoRow label="Atualizado em" value={formatDate(order.lastModified)} />
        </div>

        <div className="bg-white rounded-xl border border-gray-200 p-6 space-y-3">
          <h2 className="text-sm font-semibold text-gray-700 uppercase tracking-wide border-b border-gray-100 pb-3">
            Partes
          </h2>
          <InfoRow label="Comprador" value={`${order.buyer.name} (${order.buyer.externalReference})`} />
          <InfoRow label="Vendedor" value={`${order.seller.name} (${order.seller.externalReference})`} />
          <InfoRow label="Armazém" value={`${order.warehouse.name} (${order.warehouse.externalReference})`} />
          <div className="border-t border-gray-100 pt-3">
            <p className="text-xs text-gray-400 mb-1">Condição de Pagamento</p>
            <p className="text-sm font-medium text-gray-900">{order.paymentCondition.description}</p>
            <p className="text-xs text-gray-400 mt-0.5">
              {order.paymentCondition.code} · máx. {order.paymentCondition.maxInstallments}x
            </p>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-base font-semibold text-gray-900">Itens ({order.items.length})</h2>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="text-xs font-medium text-gray-500 uppercase bg-gray-50">
              <th className="px-6 py-3 text-left">Produto</th>
              <th className="px-6 py-3 text-left">Código</th>
              <th className="px-6 py-3 text-right">Qtd</th>
              <th className="px-6 py-3 text-right">Preço Lista</th>
              <th className="px-6 py-3 text-right">Preço Unit.</th>
              <th className="px-6 py-3 text-right">Subtotal</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {order.items.map((item, i) => (
              <tr key={i} className="hover:bg-gray-50">
                <td className="px-6 py-4 font-medium text-gray-900">{item.productName}</td>
                <td className="px-6 py-4 text-gray-500">{item.productCode}</td>
                <td className="px-6 py-4 text-right text-gray-700">{item.quantity}</td>
                <td className="px-6 py-4 text-right text-gray-400 line-through">{formatCurrency(item.listPrice)}</td>
                <td className="px-6 py-4 text-right text-gray-900">{formatCurrency(item.unitPrice)}</td>
                <td className="px-6 py-4 text-right font-medium text-gray-900">{formatCurrency(item.subtotal)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

function InfoRow({ label, value, valueClass = 'text-gray-900' }: {
  label: string
  value: string
  valueClass?: string
}) {
  return (
    <div className="flex justify-between items-center gap-4">
      <span className="text-sm text-gray-500 shrink-0">{label}</span>
      <span className={`text-sm text-right ${valueClass}`}>{value}</span>
    </div>
  )
}
