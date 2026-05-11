import { useState, useCallback } from 'react'
import { listOrders, cancelOrder, type OrderFilters } from '../api/orders'
import type { OrderSummary, PageResponse } from '../types'

export function useOrders() {
  const [data, setData] = useState<PageResponse<OrderSummary> | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [page, setPage] = useState(0)
  const [filters, setFilters] = useState<OrderFilters>({})

  const fetch = useCallback(async (p: number, f: OrderFilters) => {
    setLoading(true)
    setError(null)
    try {
      setData(await listOrders({ ...f, page: p, size: 20 }))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Erro ao carregar pedidos')
    } finally {
      setLoading(false)
    }
  }, [])

  const search = useCallback((newFilters: OrderFilters) => {
    setFilters(newFilters)
    setPage(0)
    fetch(0, newFilters)
  }, [fetch])

  const goToPage = useCallback((p: number) => {
    setPage(p)
    fetch(p, filters)
  }, [fetch, filters])

  const cancel = useCallback(async (externalReference: string): Promise<boolean> => {
    try {
      await cancelOrder(externalReference)
      setData(prev => prev
        ? { ...prev, content: prev.content.map(o =>
            o.externalReference === externalReference ? { ...o, status: 'CANCELLED' as const } : o
          )}
        : null
      )
      return true
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Erro ao cancelar pedido')
      return false
    }
  }, [])

  return { data, loading, error, page, filters, search, goToPage, cancel }
}
