package com.wms.repository;

import com.wms.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndLocationId(Long productId, Long locationId);
    List<Inventory> findByProductId(Long productId);
    List<Inventory> findByLocationId(Long locationId);

    @Query("SELECT i FROM Inventory i WHERE i.location.warehouse.id = :warehouseId")
    List<Inventory> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT i FROM Inventory i JOIN i.product p WHERE " +
            "i.quantity <= p.minStockLevel AND i.deleted = false")
    List<Inventory> findLowStockItems();

}