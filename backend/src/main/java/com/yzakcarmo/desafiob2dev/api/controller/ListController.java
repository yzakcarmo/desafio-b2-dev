package com.yzakcarmo.desafiob2dev.api.controller;

import com.yzakcarmo.desafiob2dev.api.dto.response.ListResponse;
import com.yzakcarmo.desafiob2dev.api.dto.response.ProductListResponse;
import com.yzakcarmo.desafiob2dev.service.ListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/list")
public class ListController {

    private final ListService service;

    public ListController(ListService service) { this.service = service; }

    @GetMapping("/buyers")
    public ResponseEntity<List<ListResponse>> listBuyers() {
        return ResponseEntity.ok(service.listBuyers());
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<ListResponse>> listSellers() {
        return ResponseEntity.ok(service.listSellers());
    }

    @GetMapping("/payment-conditions")
    public ResponseEntity<List<ListResponse>> listPaymentConditions() {
        return ResponseEntity.ok(service.listPaymentConditions());
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<ListResponse>> listWarehouses(
            @RequestParam(name = "sellerId", required = false) UUID sellerId
    ) {
        return ResponseEntity.ok(service.listWarehouses(sellerId));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductListResponse>> listProducts(
            @RequestParam(name = "warehouseId", required = false) UUID warehouseId
    ) {
        return ResponseEntity.ok(service.listProducts(warehouseId));
    }
}
