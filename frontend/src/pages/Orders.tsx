import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useOrders } from '../hooks/useOrders'
import { useList } from '../hooks/useList'
import type { OrderFilters } from '../api/orders'
import StatusBadge from '../components/StatusBadge'
import { formatCurrency, formatDate } from '../utils/format'

export default function Orders() {
  const { data, loading, error, page, search, goToPage, cancel } = useOrders()
  const { buyers, loadingBase, errorBase } = useList()
  const [filters, setFilters] = useState<OrderFilters>({})

  useEffect(() => { search({}) }, [search])

  function handleSearch(e: React.SyntheticEvent<HTMLFormElement>) {
    e.preventDefault()
    const f: OrderFilters = {
      status: filters.status || undefined,
      buyerRef: filters.buyerRef || undefined,
      dateFrom: filters.dateFrom ? new Date(filters.dateFrom as string).toISOString() : undefined,
      dateTo: filters.dateTo ? new Date(filters.dateTo as string).toISOString() : undefined,
    }
    search(f)
  }

  async function handleCancel(externalReference: string) {
    if (!confirm(`Cancelar o pedido ${externalReference}?`)) return
    await cancel(externalReference)
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Pedidos</h1>
        <Link to="/orders/create" className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700">
          + Novo Pedido
        </Link>
      </div>

      <form onSubmit={handleSearch} className="bg-white rounded-xl border border-gray-200 p-4">
        <div className="flex flex-wrap gap-4 items-end">
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">Status</label>
            <select
              value={filters.status ?? ''}
              onChange={e => setFilters(f => ({ ...f, status: e.target.value }))}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Todos</option>
              <option value="PENDING">Pendente</option>
              <option value="CONFIRMED">Confirmado</option>
              <option value="CANCELLED">Cancelado</option>
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">Comprador</label>
            <select
              value={filters.buyerRef ?? ''}
              onChange={e => setFilters(f => ({ ...f, buyerRef: e.target.value }))}
              disabled={loadingBase}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Todos</option>
              {buyers.map(b => <option key={b.value} value={b.value}>{b.label}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">De</label>
            <input
              type="datetime-local"
              value={filters.dateFrom ?? ''}
              onChange={e => setFilters(f => ({ ...f, dateFrom: e.target.value }))}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">Até</label>
            <input
              type="datetime-local"
              value={filters.dateTo ?? ''}
              onChange={e => setFilters(f => ({ ...f, dateTo: e.target.value }))}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <button type="submit" className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700">
            Filtrar
          </button>
        </div>
      </form>

      {(error || errorBase) && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">{error || errorBase}</div>
      )}

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        {loading ? (
          <div className="p-10 text-center text-gray-400 text-sm">Carregando...</div>
        ) : (
          <>
            <table className="w-full text-sm">
              <thead>
                <tr className="text-xs font-medium text-gray-500 uppercase bg-gray-50 border-b border-gray-200">
                  <th className="px-4 py-3 text-left">Referência</th>
                  <th className="px-4 py-3 text-left">Comprador</th>
                  <th className="px-4 py-3 text-left">Vendedor</th>
                  <th className="px-4 py-3 text-left">Status</th>
                  <th className="px-4 py-3 text-right">Total</th>
                  <th className="px-4 py-3 text-left">Criado em</th>
                  <th className="px-4 py-3"></th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {data?.content.map(order => (
                  <tr key={order.orderId} className="hover:bg-gray-50">
                    <td className="px-4 py-3">
                      <Link to={`/orders/${order.externalReference}`} className="text-blue-600 hover:text-blue-800 font-medium">
                        {order.externalReference}
                      </Link>
                    </td>
                    <td className="px-4 py-3 text-gray-700">{order.buyerName}</td>
                    <td className="px-4 py-3 text-gray-700">{order.sellerName}</td>
                    <td className="px-4 py-3"><StatusBadge status={order.status} /></td>
                    <td className="px-4 py-3 text-right font-medium text-gray-900">{formatCurrency(order.total)}</td>
                    <td className="px-4 py-3 text-gray-500">{formatDate(order.createdAt)}</td>
                    <td className="px-4 py-3 text-right">
                      {(order.status === 'PENDING' || order.status === 'CONFIRMED') && (
                        <button
                          onClick={() => handleCancel(order.externalReference)}
                          className="text-xs text-red-500 hover:text-red-700 font-medium px-2 py-1 rounded hover:bg-red-50"
                        >
                          Cancelar
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
                {!data?.content.length && (
                  <tr>
                    <td colSpan={7} className="px-6 py-10 text-center text-gray-400 text-sm">
                      Nenhum pedido encontrado
                    </td>
                  </tr>
                )}
              </tbody>
            </table>

            {data && data.totalPages > 1 && (
              <div className="px-6 py-4 border-t border-gray-200 flex items-center justify-between">
                <p className="text-sm text-gray-500">{data.totalElements} pedidos</p>
                <div className="flex items-center gap-2">
                  <button onClick={() => goToPage(page - 1)} disabled={page === 0} className="px-3 py-1 text-sm border border-gray-300 rounded-lg disabled:opacity-40 hover:bg-gray-50">
                    ← Anterior
                  </button>
                  <span className="text-sm text-gray-600">{page + 1} / {data.totalPages}</span>
                  <button onClick={() => goToPage(page + 1)} disabled={page >= data.totalPages - 1} className="px-3 py-1 text-sm border border-gray-300 rounded-lg disabled:opacity-40 hover:bg-gray-50">
                    Próxima →
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}
