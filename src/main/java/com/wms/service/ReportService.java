package com.wms.service;

import com.wms.dto.response.LowStockReportResponse;
import com.wms.dto.response.StockMovementResponse;
import com.wms.entity.Inventory;
import com.wms.entity.StockMovement;
import com.wms.repository.InventoryRepository;
import com.wms.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional(readOnly = true)
    public List<LowStockReportResponse> getLowStockReport() {
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems();

        return lowStockItems.stream()
                .map(inv -> LowStockReportResponse.builder()
                        .productId(inv.getProduct().getId())
                        .productName(inv.getProduct().getName())
                        .productSku(inv.getProduct().getSku())
                        .locationId(inv.getLocation().getId())
                        .locationCode(inv.getLocation().getCode())
                        .warehouseId(inv.getLocation().getWarehouse().getId())
                        .warehouseName(inv.getLocation().getWarehouse().getName())
                        .currentQuantity(inv.getQuantity())
                        .minStockLevel(inv.getProduct().getMinStockLevel())
                        .deficit(inv.getProduct().getMinStockLevel() - inv.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponse> getMovementReport(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);

        List<StockMovement> movements = stockMovementRepository.findByMovementDateBetween(fromDateTime, toDateTime);

        return movements.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private StockMovementResponse mapToResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .type(movement.getType().name())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .fromLocationId(movement.getFromLocation() != null ? movement.getFromLocation().getId() : null)
                .fromLocationCode(movement.getFromLocation() != null ? movement.getFromLocation().getCode() : null)
                .toLocationId(movement.getToLocation() != null ? movement.getToLocation().getId() : null)
                .toLocationCode(movement.getToLocation() != null ? movement.getToLocation().getCode() : null)
                .quantity(movement.getQuantity())
                .reason(movement.getReason())
                .movementDate(movement.getMovementDate())
                .referenceNumber(movement.getReferenceNumber())
                .build();
    }
}