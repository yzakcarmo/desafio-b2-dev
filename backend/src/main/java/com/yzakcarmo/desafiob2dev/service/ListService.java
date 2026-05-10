package com.yzakcarmo.desafiob2dev.service;

import com.yzakcarmo.desafiob2dev.api.dto.response.ListResponse;
import com.yzakcarmo.desafiob2dev.domain.repository.BuyerRepository;
import com.yzakcarmo.desafiob2dev.domain.repository.SellerRepository;
import com.yzakcarmo.desafiob2dev.domain.repository.WarehouseRepository;
import com.yzakcarmo.desafiob2dev.domain.repository.ProductPriceRepository;
import com.yzakcarmo.desafiob2dev.domain.repository.PaymentConditionRepository;
import com.yzakcarmo.desafiob2dev.tenant.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListService {

    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductPriceRepository productPriceRepository;
    private final PaymentConditionRepository paymentConditionRepository;

    public ListService(
            BuyerRepository buyerRepository,
            SellerRepository sellerRepository,
            WarehouseRepository warehouseRepository,
            ProductPriceRepository productPriceRepository,
            PaymentConditionRepository paymentConditionRepository
    ) {
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
        this.warehouseRepository = warehouseRepository;
        this.productPriceRepository = productPriceRepository;
        this.paymentConditionRepository = paymentConditionRepository;
    }

    public List<ListResponse> listBuyers() {
        String tenantCode = TenantContext.getTenant();

        return buyerRepository.listAllToOrder(tenantCode)
                .stream()
                .map(p -> new ListResponse(p.getName(), p.getExternalReference(), null))
                .toList();
    }

    public List<ListResponse> listSellers() {
        String tenantCode = TenantContext.getTenant();

        return sellerRepository.listAllToOrder(tenantCode)
                .stream()
                .map(p -> new ListResponse(p.getName(), p.getExternalReference(), p.getId()))
                .toList();
    }

    public List<ListResponse> listPaymentConditions() {
        String tenantCode = TenantContext.getTenant();

        return paymentConditionRepository.listAllToOrder(tenantCode)
                .stream()
                .map(p -> new ListResponse(p.getName(), p.getExternalReference(), null))
                .toList();
    }

    public List<ListResponse> listWarehouses(UUID sellerId) {
        return warehouseRepository.listAllToOrder(sellerId)
                .stream()
                .map(p -> new ListResponse(p.getName(), p.getExternalReference(), p.getId()))
                .toList();
    }

    public List<ListResponse> listProducts(UUID warehouseId) {
        return productPriceRepository.listAllToOrder(warehouseId)
                .stream()
                .map(p -> new ListResponse(p.getName(), p.getExternalReference(), null))
                .toList();
    }
}
