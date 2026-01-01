package com.wms.service;

import com.wms.dto.request.OrderItemRequest;
import com.wms.dto.request.OrderRequest;
import com.wms.dto.response.OrderResponse;
import com.wms.entity.*;
import com.wms.enums.OrderStatus;
import com.wms.enums.StockMovementType;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.InsufficientStockException;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository stockReservationRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerName(request.getCustomerName())
                .shippingAddress(request.getShippingAddress())
                .warehouse(warehouse)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDate.now())
                .build();

        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    @Transactional
    public void addItem(Long orderId, OrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Cannot add items to non-pending order");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .location(location)
                .quantity(request.getQuantity())
                .build();

        orderItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void reserveStock(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only pending orders can reserve stock");
        }

        if (order.getItems().isEmpty()) {
            throw new BusinessRuleException("Cannot reserve stock for order without items");
        }

        // Check and reserve stock for each item
        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByProductIdAndLocationId(item.getProduct().getId(), item.getLocation().getId())
                    .orElseThrow(() -> new BusinessRuleException("No inventory found for product in location"));

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + item.getProduct().getName()
                        + ". Available: " + inventory.getAvailableQuantity() + ", Required: " + item.getQuantity());
            }

            // Reserve stock
            inventory.setReservedQuantity(inventory.getReservedQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);

            // Create reservation record
            StockReservation reservation = StockReservation.builder()
                    .order(order)
                    .inventory(inventory)
                    .quantity(item.getQuantity())
                    .reservedAt(LocalDateTime.now())
                    .released(false)
                    .build();
            stockReservationRepository.save(reservation);
        }

        order.setStatus(OrderStatus.RESERVED);
        orderRepository.save(order);
    }

    @Transactional
    public void shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.RESERVED) {
            throw new BusinessRuleException("Only reserved orders can be shipped");
        }

        // Process each item
        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByProductIdAndLocationId(item.getProduct().getId(), item.getLocation().getId())
                    .orElseThrow(() -> new BusinessRuleException("Inventory not found"));

            // Deduct from inventory
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - item.getQuantity());
            inventoryRepository.save(inventory);

            // Record stock movement
            StockMovement movement = StockMovement.builder()
                    .type(StockMovementType.OUT)
                    .product(item.getProduct())
                    .fromLocation(item.getLocation())
                    .quantity(item.getQuantity())
                    .reason("Order Shipped")
                    .referenceNumber(order.getOrderNumber())
                    .movementDate(LocalDateTime.now())
                    .build();
            stockMovementRepository.save(movement);
        }

        // Release reservations
        List<StockReservation> reservations = stockReservationRepository.findByOrderIdAndReleasedFalse(orderId);
        reservations.forEach(reservation -> {
            reservation.setReleased(true);
            stockReservationRepository.save(reservation);
        });

        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedDate(LocalDate.now());
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .shippingAddress(order.getShippingAddress())
                .warehouseId(order.getWarehouse().getId())
                .warehouseName(order.getWarehouse().getName())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .shippedDate(order.getShippedDate())
                .build();
    }
}