import { createContext, useContext, useState, type ReactNode } from 'react'

interface TenantConfig {
  tenant: string
  authToken: string
}

interface TenantContextValue extends TenantConfig {
  setConfig: (config: TenantConfig) => void
}

const TenantContext = createContext<TenantContextValue | null>(null)

export function TenantProvider({ children }: { children: ReactNode }) {
  const [config, setConfigState] = useState<TenantConfig>(() => {
    const tenant = localStorage.getItem('tenant') ?? 'FARMA-DEFAULT'
    const authToken = localStorage.getItem('authToken') ?? 'Bearer token'
    localStorage.setItem('tenant', tenant)
    localStorage.setItem('authToken', authToken)
    return { tenant, authToken }
  })

  function setConfig(newConfig: TenantConfig) {
    setConfigState(newConfig)
    localStorage.setItem('tenant', newConfig.tenant)
    localStorage.setItem('authToken', newConfig.authToken)
  }

  return (
    <TenantContext.Provider value={{ ...config, setConfig }}>
      {children}
    </TenantContext.Provider>
  )
}

export function useTenant() {
  const ctx = useContext(TenantContext)
  if (!ctx) throw new Error('useTenant must be used within TenantProvider')
  return ctx
}