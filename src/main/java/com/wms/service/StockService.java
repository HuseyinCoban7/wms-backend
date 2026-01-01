package com.wms.service;

import com.wms.dto.request.StockAdjustmentRequest;
import com.wms.dto.request.StockTransferRequest;
import com.wms.dto.response.InventoryResponse;
import com.wms.entity.*;
import com.wms.enums.StockMovementType;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.InsufficientStockException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public void transferStock(StockTransferRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Location fromLocation = locationRepository.findById(request.getFromLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Source location not found"));

        Location toLocation = locationRepository.findById(request.getToLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination location not found"));

        // Get or create source inventory
        Inventory fromInventory = inventoryRepository
                .findByProductIdAndLocationId(product.getId(), fromLocation.getId())
                .orElseThrow(() -> new BusinessRuleException("No stock in source location"));

        // Check available quantity
        if (fromInventory.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock in source location. Available: "
                    + fromInventory.getAvailableQuantity());
        }

        // Deduct from source
        fromInventory.setQuantity(fromInventory.getQuantity() - request.getQuantity());
        inventoryRepository.save(fromInventory);

        // Add to destination
        Inventory toInventory = inventoryRepository
                .findByProductIdAndLocationId(product.getId(), toLocation.getId())
                .orElse(Inventory.builder()
                        .product(product)
                        .location(toLocation)
                        .quantity(0)
                        .reservedQuantity(0)
                        .build());

        toInventory.setQuantity(toInventory.getQuantity() + request.getQuantity());
        inventoryRepository.save(toInventory);

        // Record movement
        StockMovement movement = StockMovement.builder()
                .type(StockMovementType.TRANSFER)
                .product(product)
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .movementDate(LocalDateTime.now())
                .build();
        stockMovementRepository.save(movement);
    }

    @Transactional
    public void adjustStock(StockAdjustmentRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        Inventory inventory = inventoryRepository
                .findByProductIdAndLocationId(product.getId(), location.getId())
                .orElse(Inventory.builder()
                        .product(product)
                        .location(location)
                        .quantity(0)
                        .reservedQuantity(0)
                        .build());

        int oldQuantity = inventory.getQuantity();
        int newQuantity = request.getNewQuantity();

        if (newQuantity < inventory.getReservedQuantity()) {
            throw new BusinessRuleException("Cannot adjust below reserved quantity: " + inventory.getReservedQuantity());
        }

        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);

        // Record movement
        StockMovement movement = StockMovement.builder()
                .type(StockMovementType.ADJUSTMENT)
                .product(product)
                .toLocation(location)
                .quantity(Math.abs(newQuantity - oldQuantity))
                .reason(request.getReason())
                .movementDate(LocalDateTime.now())
                .build();
        stockMovementRepository.save(movement);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventory(Long warehouseId, Long productId) {
        List<Inventory> inventories;

        if (warehouseId != null && productId != null) {
            inventories = inventoryRepository.findByWarehouseId(warehouseId).stream()
                    .filter(inv -> inv.getProduct().getId().equals(productId))
                    .collect(Collectors.toList());
        } else if (warehouseId != null) {
            inventories = inventoryRepository.findByWarehouseId(warehouseId);
        } else if (productId != null) {
            inventories = inventoryRepository.findByProductId(productId);
        } else {
            inventories = inventoryRepository.findAll();
        }

        return inventories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .productSku(inventory.getProduct().getSku())
                .locationId(inventory.getLocation().getId())
                .locationCode(inventory.getLocation().getCode())
                .warehouseId(inventory.getLocation() != null && inventory.getLocation().getWarehouse() != null
                        ? inventory.getLocation().getWarehouse().getId() : null)
                .warehouseName(inventory.getLocation().getWarehouse().getName())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .build();
    }
}