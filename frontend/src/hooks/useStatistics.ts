import { useState } from 'react'
import { getStatistics } from '../api/orders'
import type { OrderStatistics } from '../types'

function defaultRange() {
  const now = new Date()
  const from = new Date(now.getFullYear(), now.getMonth(), 1)
  return {
    from: from.toISOString().slice(0, 16),
    to: now.toISOString().slice(0, 16),
  }
}

export function useStatistics() {
  const range = defaultRange()
  const [dateFrom, setDateFrom] = useState(range.from)
  const [dateTo, setDateTo] = useState(range.to)
  const [stats, setStats] = useState<OrderStatistics | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function load(from = dateFrom, to = dateTo) {
    setLoading(true)
    setError(null)
    try {
      setStats(await getStatistics(new Date(from).toISOString(), new Date(to).toISOString()))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Erro ao carregar estatísticas')
    } finally {
      setLoading(false)
    }
  }

  return { stats, loading, error, dateFrom, setDateFrom, dateTo, setDateTo, load }
}
