export class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message)
  }
}

function getHeaders(): Record<string, string> {
  return {
    'Content-Type': 'application/json',
    'x-tenant': localStorage.getItem('tenant') ?? '',
    'Authorization': localStorage.getItem('authToken') ?? '',
  }
}

export async function apiFetch<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`/api/v1${path}`, {
    ...options,
    headers: { ...getHeaders(), ...options?.headers },
  })

  if (response.status === 204) return undefined as T

  const data = await response.json().catch(() => null)

  if (!response.ok) {
    throw new ApiError(response.status, data?.message ?? `Erro ${response.status}`)
  }

  return data as T
}