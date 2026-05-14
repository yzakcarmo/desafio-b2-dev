import { useState, type ReactNode } from 'react'
import { TenantContext, type TenantConfig } from './tenantCtx'

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
