import { useState } from 'react'
import { useTenant } from '../context/TenantContext'

const TENANTS = ['FARMA-DEFAULT', 'FARMA-PREMIUM', 'FARMA-ECONOMIA']

interface Props {
  onClose: () => void
}

export default function TenantModal({ onClose }: Props) {
  const { tenant, authToken, setConfig } = useTenant()
  const [form, setForm] = useState({ tenant, authToken })

  function handleSave() {
    setConfig(form)
    onClose()
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-md p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Configuração</h2>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tenant</label>
            <select
              value={form.tenant}
              onChange={e => setForm(f => ({ ...f, tenant: e.target.value }))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {TENANTS.map(t => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Token de Autorização</label>
            <input
              type="text"
              value={form.authToken}
              onChange={e => setForm(f => ({ ...f, authToken: e.target.value }))}
              placeholder="Bearer ..."
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        <div className="flex justify-end gap-3 mt-6">
          <button onClick={onClose} className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-lg border border-gray-300">
            Cancelar
          </button>
          <button onClick={handleSave} className="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-lg">
            Salvar
          </button>
        </div>
      </div>
    </div>
  )
}