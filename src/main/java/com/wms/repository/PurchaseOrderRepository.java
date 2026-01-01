package com.wms.repository;

import com.wms.entity.PurchaseOrder;
import com.wms.enums.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
    List<PurchaseOrder> findByWarehouseId(Long warehouseId);
}