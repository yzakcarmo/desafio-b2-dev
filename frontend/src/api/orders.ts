import { apiFetch } from './client'
import type {
  OrderSummary,
  OrderDetail,
  PageResponse,
  OrderStatistics,
  CreateOrderRequest,
  CreateOrderResponse,
} from '../types'

export interface OrderFilters {
  page?: number
  size?: number
  status?: string
  buyerRef?: string
  dateFrom?: string
  dateTo?: string
}

export function listOrders(filters: OrderFilters = {}): Promise<PageResponse<OrderSummary>> {
  const params = new URLSearchParams()
  if (filters.page != null) params.set('page', String(filters.page))
  if (filters.size != null) params.set('size', String(filters.size))
  if (filters.status) params.set('status', filters.status)
  if (filters.buyerRef) params.set('buyerRef', filters.buyerRef)
  if (filters.dateFrom) params.set('dateFrom', filters.dateFrom)
  if (filters.dateTo) params.set('dateTo', filters.dateTo)
  const qs = params.toString()
  return apiFetch(`/orders${qs ? `?${qs}` : ''}`)
}

export function getOrder(externalReference: string): Promise<OrderDetail> {
  return apiFetch(`/orders/${externalReference}`)
}

export function createOrder(data: CreateOrderRequest): Promise<CreateOrderResponse> {
  return apiFetch('/orders', { method: 'POST', body: JSON.stringify(data) })
}

export function cancelOrder(externalReference: string): Promise<void> {
  return apiFetch(`/orders/${externalReference}/cancel`, { method: 'POST' })
}

export function getStatistics(dateFrom: string, dateTo: string): Promise<OrderStatistics> {
  return apiFetch(`/orders/statistics?dateFrom=${encodeURIComponent(dateFrom)}&dateTo=${encodeURIComponent(dateTo)}`)
}