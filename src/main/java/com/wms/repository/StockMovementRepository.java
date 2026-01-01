package com.wms.repository;

import com.wms.entity.StockMovement;
import com.wms.enums.StockMovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductId(Long productId);
    List<StockMovement> findByType(StockMovementType type);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :from AND :to")
    List<StockMovement> findByMovementDateBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}