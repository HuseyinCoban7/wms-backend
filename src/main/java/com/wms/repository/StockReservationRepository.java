package com.wms.repository;

import com.wms.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    List<StockReservation> findByOrderIdAndReleasedFalse(Long orderId);
    List<StockReservation> findByInventoryIdAndReleasedFalse(Long inventoryId);
}