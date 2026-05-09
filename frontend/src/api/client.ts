import axios, { type AxiosError } from 'axios'

export interface ApiErrorData {
  status: number
  code: string
  message: string
  details?: string[]
  traceId?: string
}

export class ApiError extends Error {
  readonly status: number
  readonly code: string
  readonly details: string[]
  readonly traceId?: string

  constructor(data: ApiErrorData) {
    super(data.message)
    this.status = data.status
    this.code = data.code
    this.details = data.details ?? []
    this.traceId = data.traceId
  }
}

// Instância Axios com interceptor de headers — equivale a um HttpInterceptor no Angular
export const api = axios.create({ baseURL: '/api/v1' })

api.interceptors.request.use(config => {
  config.headers['x-tenant'] = localStorage.getItem('tenant') ?? ''
  config.headers['Authorization'] = localStorage.getItem('authToken') ?? ''
  return config
})

api.interceptors.response.use(
  response => response,
  (error: AxiosError<ApiErrorData>) => {
    const data = error.response?.data
    throw new ApiError({
      status: error.response?.status ?? 0,
      code: data?.code ?? 'UNKNOWN',
      message: data?.message ?? error.message ?? 'Erro desconhecido',
      details: data?.details,
      traceId: data?.traceId,
    })
  },
)
