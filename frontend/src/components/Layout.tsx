import { useState, type ReactNode } from 'react'
import { NavLink } from 'react-router-dom'
import { useTenant } from '../hooks/useTenant'
import TenantModal from './TenantModal'

interface Props {
  children: ReactNode
}

export default function Layout({ children }: Props) {
  const { tenant } = useTenant()
  const [showModal, setShowModal] = useState(false)

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
      isActive ? 'bg-blue-50 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
    }`

  return (
    <div className="flex h-screen bg-gray-50">
      <aside className="w-56 bg-white border-r border-gray-200 flex flex-col shrink-0">
        <div className="px-5 py-4 border-b border-gray-200">
          <h1 className="text-lg font-bold text-blue-600">B2 Orders</h1>
          <p className="text-xs text-gray-400 mt-0.5">Gestão de Pedidos</p>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1">
          <NavLink to="/dashboard" className={linkClass}>
            📊 Dashboard
          </NavLink>
          <NavLink to="/orders" end className={linkClass}>
            📋 Pedidos
          </NavLink>
          <NavLink to="/orders/create" className={linkClass}>
            ➕ Novo Pedido
          </NavLink>
        </nav>

        <div className="px-3 py-4 border-t border-gray-200">
          <button
            onClick={() => setShowModal(true)}
            className="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-sm text-gray-600 hover:bg-gray-100 transition-colors"
          >
            ⚙️ <span className="truncate">{tenant}</span>
          </button>
        </div>
      </aside>

      <main className="flex-1 overflow-auto">
        {children}
      </main>

      {showModal && <TenantModal onClose={() => setShowModal(false)} />}
    </div>
  )
}