import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { listOrders } from '../api/orders'
import type { OrderSummary, PageResponse } from '../types'
import StatusBadge from '../components/StatusBadge'
import { formatCurrency, formatDate } from '../utils/format'

interface Filters {
  status: string
  buyerRef: string
  dateFrom: string
  dateTo: string
}

export default function Orders() {
  const [data, setData] = useState<PageResponse<OrderSummary> | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [page, setPage] = useState(0)
  const [filters, setFilters] = useState<Filters>({ status: '', buyerRef: '', dateFrom: '', dateTo: '' })

  async function load(currentPage = page, currentFilters = filters) {
    setLoading(true)
    setError(null)
    try {
      setData(await listOrders({
        page: currentPage,
        size: 20,
        status: currentFilters.status || undefined,
        buyerRef: currentFilters.buyerRef || undefined,
        dateFrom: currentFilters.dateFrom ? new Date(currentFilters.dateFrom).toISOString() : undefined,
        dateTo: currentFilters.dateTo ? new Date(currentFilters.dateTo).toISOString() : undefined,
      }))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Erro desconhecido')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load(0, filters) }, [])

  function handleSearch(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setPage(0)
    load(0, filters)
  }

  function goToPage(p: number) {
    setPage(p)
    load(p, filters)
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Pedidos</h1>
        <Link to="/orders/create" className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700">
          + Novo Pedido
        </Link>
      </div>

      {/* Filtros */}
      <form onSubmit={handleSearch} className="bg-white rounded-xl border border-gray-200 p-4">
        <div className="flex flex-wrap gap-4 items-end">
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">Status</label>
            <select
              value={filters.status}
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
            <label className="block text-xs font-medium text-gray-500 mb-1">Comprador (ref)</label>
            <input
              type="text"
              value={filters.buyerRef}
              onChange={e => setFilters(f => ({ ...f, buyerRef: e.target.value }))}
              placeholder="ex: BUYER-001"
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">De</label>
            <input
              type="datetime-local"
              value={filters.dateFrom}
              onChange={e => setFilters(f => ({ ...f, dateFrom: e.target.value }))}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">Até</label>
            <input
              type="datetime-local"
              value={filters.dateTo}
              onChange={e => setFilters(f => ({ ...f, dateTo: e.target.value }))}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <button type="submit" className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700">
            Filtrar
          </button>
        </div>
      </form>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>
      )}

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        {loading ? (
          <div className="p-10 text-center text-gray-400 text-sm">Carregando...</div>
        ) : (
          <>
            <table className="w-full text-sm">
              <thead>
                <tr className="text-xs font-medium text-gray-500 uppercase bg-gray-50 border-b border-gray-200">
                  <th className="px-6 py-3 text-left">Referência</th>
                  <th className="px-6 py-3 text-left">Comprador</th>
                  <th className="px-6 py-3 text-left">Vendedor</th>
                  <th className="px-6 py-3 text-left">Status</th>
                  <th className="px-6 py-3 text-right">Total</th>
                  <th className="px-6 py-3 text-left">Criado em</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {data?.content.map(order => (
                  <tr key={order.orderId} className="hover:bg-gray-50">
                    <td className="px-6 py-4">
                      <Link to={`/orders/${order.externalReference}`} className="text-blue-600 hover:text-blue-800 font-medium">
                        {order.externalReference}
                      </Link>
                    </td>
                    <td className="px-6 py-4 text-gray-700">{order.buyerName}</td>
                    <td className="px-6 py-4 text-gray-700">{order.sellerName}</td>
                    <td className="px-6 py-4"><StatusBadge status={order.status} /></td>
                    <td className="px-6 py-4 text-right font-medium text-gray-900">{formatCurrency(order.total)}</td>
                    <td className="px-6 py-4 text-gray-500">{formatDate(order.createdAt)}</td>
                  </tr>
                ))}
                {!data?.content.length && (
                  <tr>
                    <td colSpan={6} className="px-6 py-10 text-center text-gray-400 text-sm">
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
                  <button
                    onClick={() => goToPage(page - 1)}
                    disabled={page === 0}
                    className="px-3 py-1 text-sm border border-gray-300 rounded-lg disabled:opacity-40 hover:bg-gray-50"
                  >
                    ← Anterior
                  </button>
                  <span className="text-sm text-gray-600">{page + 1} / {data.totalPages}</span>
                  <button
                    onClick={() => goToPage(page + 1)}
                    disabled={page >= data.totalPages - 1}
                    className="px-3 py-1 text-sm border border-gray-300 rounded-lg disabled:opacity-40 hover:bg-gray-50"
                  >
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
