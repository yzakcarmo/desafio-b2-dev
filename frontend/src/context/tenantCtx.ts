import { createContext } from 'react'

export interface TenantConfig {
  tenant: string
  authToken: string
}

export interface TenantContextValue extends TenantConfig {
  setConfig: (config: TenantConfig) => void
}

export const TenantContext = createContext<TenantContextValue | null>(null)
