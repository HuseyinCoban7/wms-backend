package com.wms.service;

import com.wms.dto.request.PurchaseOrderRequest;
import com.wms.dto.response.PurchaseOrderResponse;
import com.wms.entity.PurchaseOrder;
import com.wms.entity.Supplier;
import com.wms.entity.Warehouse;
import com.wms.enums.PurchaseOrderStatus;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.PurchaseOrderRepository;
import com.wms.repository.SupplierRepository;
import com.wms.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private Supplier supplier;
    private Warehouse warehouse;
    private PurchaseOrder po;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder().code("S-1").name("Supplier").build();
        supplier.setId(1L);
        warehouse = Warehouse.builder().code("W-1").name("WH").build();
        warehouse.setId(1L);

        po = PurchaseOrder.builder().orderNumber("PO-123").supplier(supplier).warehouse(warehouse).status(PurchaseOrderStatus.DRAFT).orderDate(LocalDate.now()).build();
        po.setId(1L);
    }

    @Test
    void createPurchaseOrder_Success() {
        PurchaseOrderRequest req = new PurchaseOrderRequest();
        req.setSupplierId(1L);
        req.setWarehouseId(1L);
        req.setExpectedDeliveryDate(LocalDate.now().plusDays(5));

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(po);

        PurchaseOrderResponse resp = purchaseOrderService.createPurchaseOrder(req);

        assertNotNull(resp);
        assertEquals("PO-123", resp.getOrderNumber());
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
    }

    @Test
    void createPurchaseOrder_SupplierNotFound_Throws() {
        when(supplierRepository.findById(2L)).thenReturn(Optional.empty());
        PurchaseOrderRequest req = new PurchaseOrderRequest();
        req.setSupplierId(2L);
        req.setWarehouseId(1L);
        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.createPurchaseOrder(req));
    }
}
