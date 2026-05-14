import { useState, useCallback, useRef, useEffect } from 'react'
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

  const dateFromRef = useRef(dateFrom)
  const dateToRef = useRef(dateTo)

  useEffect(() => { dateFromRef.current = dateFrom }, [dateFrom])
  useEffect(() => { dateToRef.current = dateTo }, [dateTo])

  const load = useCallback(async (from?: string, to?: string) => {
    const f = from ?? dateFromRef.current
    const t = to ?? dateToRef.current
    setLoading(true)
    setError(null)
    try {
      setStats(await getStatistics(new Date(f).toISOString(), new Date(t).toISOString()))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Erro ao carregar estatísticas')
    } finally {
      setLoading(false)
    }
  }, [])

  return { stats, loading, error, dateFrom, setDateFrom, dateTo, setDateTo, load }
}
