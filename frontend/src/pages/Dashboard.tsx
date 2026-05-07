import { useState, useEffect } from 'react'
import { getStatistics } from '../api/orders'
import type { OrderStatistics } from '../types'
import { formatCurrency } from '../utils/format'

function toISO(localDt: string) {
  return new Date(localDt).toISOString()
}

function defaultRange() {
  const now = new Date()
  const from = new Date(now.getFullYear(), now.getMonth(), 1)
  return {
    from: from.toISOString().slice(0, 16),
    to: now.toISOString().slice(0, 16),
  }
}

export default function Dashboard() {
  const range = defaultRange()
  const [dateFrom, setDateFrom] = useState(range.from)
  const [dateTo, setDateTo] = useState(range.to)
  const [stats, setStats] = useState<OrderStatistics | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function load() {
    setLoading(true)
    setError(null)
    try {
      setStats(await getStatistics(toISO(dateFrom), toISO(dateTo)))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Erro desconhecido')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>

      {/* Filtro de período */}
      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <div className="flex flex-wrap gap-4 items-end">
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">De</label>
            <input
              type="datetime-local"
              value={dateFrom}
              onChange={e => setDateFrom(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">Até</label>
            <input
              type="datetime-local"
              value={dateTo}
              onChange={e => setDateTo(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <button
            onClick={load}
            disabled={loading}
            className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? 'Carregando...' : 'Atualizar'}
          </button>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>
      )}

      {stats && (
        <>
          {/* Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4">
            <StatCard label="Total de Pedidos" value={String(stats.totalOrders)} />
            <StatCard label="Confirmados" value={String(stats.confirmedOrders)} valueClass="text-green-600" />
            <StatCard label="Cancelados" value={String(stats.cancelledOrders)} valueClass="text-red-600" />
            <StatCard label="Receita Total" value={formatCurrency(stats.totalRevenue)} />
            <StatCard label="Ticket Médio" value={formatCurrency(stats.averageOrderValue)} />
          </div>

          {/* Tabelas */}
          <div className="grid lg:grid-cols-2 gap-6">
            <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-200">
                <h2 className="text-base font-semibold text-gray-900">Top Compradores</h2>
              </div>
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-xs font-medium text-gray-500 uppercase bg-gray-50">
                    <th className="px-6 py-3 text-left">Nome</th>
                    <th className="px-6 py-3 text-right">Pedidos</th>
                    <th className="px-6 py-3 text-right">Total</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {stats.topBuyers.map((b, i) => (
                    <tr key={i} className="hover:bg-gray-50">
                      <td className="px-6 py-3 text-gray-900">{b.name}</td>
                      <td className="px-6 py-3 text-right text-gray-600">{b.orderCount}</td>
                      <td className="px-6 py-3 text-right font-medium text-gray-900">{formatCurrency(b.totalSpent)}</td>
                    </tr>
                  ))}
                  {stats.topBuyers.length === 0 && (
                    <tr><td colSpan={3} className="px-6 py-4 text-center text-gray-400 text-xs">Sem dados</td></tr>
                  )}
                </tbody>
              </table>
            </div>

            <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-200">
                <h2 className="text-base font-semibold text-gray-900">Top Produtos</h2>
              </div>
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-xs font-medium text-gray-500 uppercase bg-gray-50">
                    <th className="px-6 py-3 text-left">Produto</th>
                    <th className="px-6 py-3 text-left">Código</th>
                    <th className="px-6 py-3 text-right">Qtd</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {stats.topProducts.map((p, i) => (
                    <tr key={i} className="hover:bg-gray-50">
                      <td className="px-6 py-3 text-gray-900">{p.productName}</td>
                      <td className="px-6 py-3 text-gray-500">{p.productCode}</td>
                      <td className="px-6 py-3 text-right font-medium text-gray-900">{p.totalQuantity}</td>
                    </tr>
                  ))}
                  {stats.topProducts.length === 0 && (
                    <tr><td colSpan={3} className="px-6 py-4 text-center text-gray-400 text-xs">Sem dados</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  )
}

function StatCard({ label, value, valueClass = 'text-gray-900' }: {
  label: string
  value: string
  valueClass?: string
}) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 px-5 py-4">
      <p className="text-xs font-medium text-gray-500 uppercase tracking-wide">{label}</p>
      <p className={`text-2xl font-bold mt-1 ${valueClass}`}>{value}</p>
    </div>
  )
}
