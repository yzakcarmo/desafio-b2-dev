import { useState, useEffect, useCallback } from 'react'
import {
  getBuyers,
  getSellers,
  getPaymentConditions,
  getWarehouses,
  getProducts,
} from '../api/orders'
import type { ListResponse, ProductListResponse } from '../types'

interface UseListState {
  buyers: ListResponse[]
  sellers: ListResponse[]
  paymentConditions: ListResponse[]
  
  warehouses: ListResponse[]
  products: ProductListResponse[]
  
  selectedSeller: string
  selectedWarehouse: string
  
  loadingBase: boolean
  loadingWarehouses: boolean
  loadingProducts: boolean
  
  errorBase: string | null
  errorWarehouses: string | null
  errorProducts: string | null
  
  setSelectedSeller: (sellerId: string) => void
  setSelectedWarehouse: (warehouseId: string) => void
}

export function useList(): UseListState {
  const [buyers, setBuyers] = useState<ListResponse[]>([])
  const [sellers, setSellers] = useState<ListResponse[]>([])
  const [paymentConditions, setPaymentConditions] = useState<ListResponse[]>([])
  
  const [warehouses, setWarehouses] = useState<ListResponse[]>([])
  const [products, setProducts] = useState<ProductListResponse[]>([])
  
  const [selectedSeller, setSelectedSeller] = useState('')
  const [selectedWarehouse, setSelectedWarehouse] = useState('')
  
  const [loadingBase, setLoadingBase] = useState(true)
  const [loadingWarehouses, setLoadingWarehouses] = useState(false)
  const [loadingProducts, setLoadingProducts] = useState(false)
  
  const [errorBase, setErrorBase] = useState<string | null>(null)
  const [errorWarehouses, setErrorWarehouses] = useState<string | null>(null)
  const [errorProducts, setErrorProducts] = useState<string | null>(null)

  useEffect(() => {
    const loadBaseLists = async () => {
      setLoadingBase(true)
      setErrorBase(null)
      try {
        const [buyersData, sellersData, pcData] = await Promise.all([
          getBuyers(),
          getSellers(),
          getPaymentConditions(),
        ])
        setBuyers(buyersData)
        setSellers(sellersData)
        setPaymentConditions(pcData)
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : 'Erro ao carregar dados de base'
        setErrorBase(message)
      } finally {
        setLoadingBase(false)
      }
    }
    loadBaseLists()
  }, [])

  useEffect(() => {
    if (!selectedSeller) {
      return
    }
    
    const loadWarehouses = async () => {
      setLoadingWarehouses(true)
      setErrorWarehouses(null)
      try {
        const whData = await getWarehouses(selectedSeller)
        setWarehouses(whData)
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : 'Erro ao carregar armazéns'
        setErrorWarehouses(message)
      } finally {
        setLoadingWarehouses(false)
      }
    }
    loadWarehouses()
  }, [selectedSeller])

  useEffect(() => {
    if (!selectedWarehouse) {
      return
    }
    
    const loadProducts = async () => {
      setLoadingProducts(true)
      setErrorProducts(null)
      try {
        const prodData = await getProducts(selectedWarehouse)
        setProducts(prodData)
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : 'Erro ao carregar produtos'
        setErrorProducts(message)
      } finally {
        setLoadingProducts(false)
      }
    }
    loadProducts()
  }, [selectedWarehouse])

  const handleSetSelectedSeller = useCallback((sellerId: string) => {
    setWarehouses([])
    setProducts([])
    setSelectedWarehouse('')
    setSelectedSeller(sellerId)
  }, [])

  const handleSetSelectedWarehouse = useCallback((warehouseId: string) => {
    setProducts([])
    setSelectedWarehouse(warehouseId)
  }, [])

  return {
    buyers,
    sellers,
    paymentConditions,
    warehouses,
    products,
    selectedSeller,
    selectedWarehouse,
    loadingBase,
    loadingWarehouses,
    loadingProducts,
    errorBase,
    errorWarehouses,
    errorProducts,
    setSelectedSeller: handleSetSelectedSeller,
    setSelectedWarehouse: handleSetSelectedWarehouse,
  }
}