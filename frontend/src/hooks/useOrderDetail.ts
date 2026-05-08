import { useState, useEffect } from 'react'
import { getOrder, cancelOrder } from '../api/orders'
import type { OrderDetail } from '../types'

export function useOrderDetail(externalReference: string | undefined) {
  const [order, setOrder] = useState<OrderDetail | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [cancelling, setCancelling] = useState(false)

  useEffect(() => {
    if (!externalReference) return
    setLoading(true)
    setError(null)
    getOrder(externalReference)
      .then(setOrder)
      .catch(e => setError(e instanceof Error ? e.message : 'Erro ao carregar pedido'))
      .finally(() => setLoading(false))
  }, [externalReference])

  async function cancel(): Promise<boolean> {
    if (!order) return false
    setCancelling(true)
    try {
      await cancelOrder(order.externalReference)
      setOrder(prev => prev ? { ...prev, status: 'CANCELLED' } : null)
      return true
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Erro ao cancelar pedido')
      return false
    } finally {
      setCancelling(false)
    }
  }

  return { order, loading, error, cancelling, cancel }
}
