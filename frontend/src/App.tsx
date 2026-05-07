import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { TenantProvider } from './context/TenantContext'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import Orders from './pages/Orders'
import OrderDetail from './pages/OrderDetail'
import CreateOrder from './pages/CreateOrder'
import './App.css'

export default function App() {
  return (
    <TenantProvider>
      <BrowserRouter>
        <Layout>
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/orders" element={<Orders />} />
            <Route path="/orders/create" element={<CreateOrder />} />
            <Route path="/orders/:ref" element={<OrderDetail />} />
          </Routes>
        </Layout>
      </BrowserRouter>
    </TenantProvider>
  )
}