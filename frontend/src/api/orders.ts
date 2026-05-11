import { api } from './client'
import type {
  OrderSummary,
  OrderDetail,
  ListResponse,
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

export async function getBuyers(): Promise<ListResponse[]> {
  const { data } = await api.get('/list/buyers')
  return data
}

export async function getSellers(): Promise<ListResponse[]> {
  const { data } = await api.get('/list/sellers')
  return data
}

export async function getPaymentConditions(): Promise<ListResponse[]> {
  const { data } = await api.get('/list/payment-conditions')
  return data
}

export async function getWarehouses(sellerId: string): Promise<ListResponse[]> {
  const { data } = await api.get('/list/warehouses', { params: { sellerId } })
  return data
}

export async function getProducts(warehouseId: string): Promise<ListResponse[]> {
  const { data } = await api.get('/list/products', { params: { warehouseId } })
  return data
}
