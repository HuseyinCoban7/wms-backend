package com.wms.service;

import com.wms.dto.request.PurchaseOrderItemRequest;
import com.wms.dto.request.PurchaseOrderRequest;
import com.wms.dto.response.PurchaseOrderResponse;
import com.wms.entity.*;
import com.wms.enums.PurchaseOrderStatus;
import com.wms.enums.StockMovementType;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .orderNumber("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .supplier(supplier)
                .warehouse(warehouse)
                .status(PurchaseOrderStatus.DRAFT)
                .orderDate(LocalDate.now())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .build();

        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return mapToResponse(purchaseOrder);
    }

    @Transactional
    public void addItem(Long purchaseOrderId, PurchaseOrderItemRequest request) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BusinessRuleException("Cannot add items to non-draft purchase order");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        PurchaseOrderItem item = PurchaseOrderItem.builder()
                .purchaseOrder(purchaseOrder)
                .product(product)
                .location(location)
                .orderedQuantity(request.getQuantity())
                .receivedQuantity(0)
                .unitPrice(request.getUnitPrice())
                .build();

        purchaseOrderItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approvePurchaseOrder(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BusinessRuleException("Only draft purchase orders can be approved");
        }

        if (purchaseOrder.getItems().isEmpty()) {
            throw new BusinessRuleException("Cannot approve purchase order without items");
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVED);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void receivePurchaseOrder(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new BusinessRuleException("Only approved purchase orders can be received");
        }

        // Process each item
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            int quantityToReceive = item.getOrderedQuantity() - item.getReceivedQuantity();

            if (quantityToReceive > 0) {
                // Update or create inventory
                Inventory inventory = inventoryRepository
                        .findByProductIdAndLocationId(item.getProduct().getId(), item.getLocation().getId())
                        .orElse(Inventory.builder()
                                .product(item.getProduct())
                                .location(item.getLocation())
                                .quantity(0)
                                .reservedQuantity(0)
                                .build());

                inventory.setQuantity(inventory.getQuantity() + quantityToReceive);
                inventoryRepository.save(inventory);

                // Update received quantity
                item.setReceivedQuantity(item.getOrderedQuantity());
                purchaseOrderItemRepository.save(item);

                // Record stock movement
                StockMovement movement = StockMovement.builder()
                        .type(StockMovementType.IN)
                        .product(item.getProduct())
                        .toLocation(item.getLocation())
                        .quantity(quantityToReceive)
                        .reason("Purchase Order Received")
                        .referenceNumber(purchaseOrder.getOrderNumber())
                        .movementDate(LocalDateTime.now())
                        .build();
                stockMovementRepository.save(movement);
            }
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        purchaseOrder.setReceivedDate(LocalDate.now());
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));
        return mapToResponse(purchaseOrder);
    }

    private PurchaseOrderResponse mapToResponse(PurchaseOrder po) {
        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .orderNumber(po.getOrderNumber())
                .supplierId(po.getSupplier().getId())
                .supplierName(po.getSupplier().getName())
                .warehouseId(po.getWarehouse().getId())
                .warehouseName(po.getWarehouse().getName())
                .status(po.getStatus().name())
                .orderDate(po.getOrderDate())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .receivedDate(po.getReceivedDate())
                .build();
    }
}