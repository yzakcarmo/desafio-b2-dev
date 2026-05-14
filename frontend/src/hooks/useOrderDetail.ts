import { useReducer, useEffect } from 'react'
import { getOrder, cancelOrder } from '../api/orders'
import type { OrderDetail } from '../types'

type State = {
  order: OrderDetail | null
  loading: boolean
  error: string | null
  cancelling: boolean
}

type Action =
  | { type: 'fetch_start' }
  | { type: 'fetch_success'; order: OrderDetail }
  | { type: 'fetch_error'; error: string }
  | { type: 'cancel_start' }
  | { type: 'cancel_success' }
  | { type: 'cancel_error'; error: string }
  | { type: 'cancel_end' }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'fetch_start': return { ...state, loading: true, error: null }
    case 'fetch_success': return { ...state, loading: false, order: action.order }
    case 'fetch_error': return { ...state, loading: false, error: action.error }
    case 'cancel_start': return { ...state, cancelling: true }
    case 'cancel_success': return { ...state, order: state.order ? { ...state.order, status: 'CANCELLED' } : null }
    case 'cancel_error': return { ...state, error: action.error }
    case 'cancel_end': return { ...state, cancelling: false }
  }
}

const initialState: State = { order: null, loading: true, error: null, cancelling: false }

export function useOrderDetail(externalReference: string | undefined) {
  const [state, dispatch] = useReducer(reducer, initialState)

  useEffect(() => {
    if (!externalReference) return
    dispatch({ type: 'fetch_start' })
    getOrder(externalReference)
      .then(order => dispatch({ type: 'fetch_success', order }))
      .catch(e => dispatch({ type: 'fetch_error', error: e instanceof Error ? e.message : 'Erro ao carregar pedido' }))
  }, [externalReference])

  async function cancel(): Promise<boolean> {
    if (!state.order) return false
    dispatch({ type: 'cancel_start' })
    try {
      await cancelOrder(state.order.externalReference)
      dispatch({ type: 'cancel_success' })
      return true
    } catch (e: unknown) {
      dispatch({ type: 'cancel_error', error: e instanceof Error ? e.message : 'Erro ao cancelar pedido' })
      return false
    } finally {
      dispatch({ type: 'cancel_end' })
    }
  }

  return { ...state, cancel }
}
