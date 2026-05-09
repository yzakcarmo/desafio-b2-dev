import { api } from './client'
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

export async function listOrders(filters: OrderFilters = {}): Promise<PageResponse<OrderSummary>> {
  const { data } = await api.get('/orders', { params: filters })
  return data
}

export async function getOrder(externalReference: string): Promise<OrderDetail> {
  const { data } = await api.get(`/orders/${externalReference}`)
  return data
}

export async function createOrder(payload: CreateOrderRequest): Promise<CreateOrderResponse> {
  const { data } = await api.post('/orders', payload)
  return data
}

export async function cancelOrder(externalReference: string): Promise<void> {
  await api.post(`/orders/${externalReference}/cancel`)
}

export async function getStatistics(dateFrom: string, dateTo: string): Promise<OrderStatistics> {
  const { data } = await api.get('/orders/statistics', { params: { dateFrom, dateTo } })
  return data
}
