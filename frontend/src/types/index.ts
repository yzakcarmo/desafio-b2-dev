export interface OrderSummary {
  orderId: string
  externalReference: string
  buyerName: string
  sellerName: string
  warehouseName: string
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED'
  subtotal: number
  discountValue: number
  total: number
  itemCount: number
  origin: string
  createdAt: string
}

export interface OrderDetail {
  orderId: string
  externalReference: string
  status: string
  origin: string
  subtotal: number
  discountValue: number
  total: number
  createdAt: string
  lastModified: string
  buyer: { externalReference: string; name: string }
  seller: { externalReference: string; name: string }
  warehouse: { externalReference: string; name: string }
  paymentCondition: { code: string; description: string; maxInstallments: number }
  items: Array<{
    productCode: string
    productName: string
    quantity: number
    unitPrice: number
    listPrice: number
    subtotal: number
  }>
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface OrderStatistics {
  tenant: string
  period: { from: string; to: string }
  totalOrders: number
  confirmedOrders: number
  cancelledOrders: number
  totalRevenue: number
  averageOrderValue: number
  topBuyers: Array<{ name: string; orderCount: number; totalSpent: number }>
  topProducts: Array<{ productCode: string; productName: string; totalQuantity: number }>
}

export interface CreateOrderRequest {
  externalReference: string
  buyerReference: string
  sellerReference: string
  warehouseReference: string
  paymentConditionCode: string
  items: Array<{ productCode: string; quantity: number }>
}

export interface CreateOrderResponse {
  code: string
  message: string
  data: {
    orderId: string
    externalReference: string
    status: string
    subtotal: number
    discountValue: number
    total: number
    itemCount: number
    validation: { warnings: string[] }
    pricing: { subtotal: number; description: string }
    discount: { value: number; percentage: number; description: string; freeShipping: boolean }
  }
}

export interface ListResponse {
  label: string
  value: string
  id: string
}