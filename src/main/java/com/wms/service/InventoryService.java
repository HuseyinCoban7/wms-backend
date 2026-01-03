package com.wms.service;

import com.wms.dto.request.InventoryRequest;
import com.wms.dto.response.InventoryResponse;
import com.wms.entity.Inventory;
import com.wms.entity.Location;
import com.wms.entity.Product;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.InventoryRepository;
import com.wms.repository.LocationRepository;
import com.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;

    // Tüm stok kayıtları
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Belirli ürünün tüm lokasyonlardaki stoğu
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByProduct(Long productId) {
        // Ürün var mı diye kontrol edelim (yoksa 404)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return inventoryRepository.findByProductId(product.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Belirli lokasyonun stokları
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));

        return inventoryRepository.findByLocationId(location.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Ürün + lokasyon için stok yarat / güncelle
    @Transactional
    public InventoryResponse createOrUpdateInventory(InventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        // Mevcut kayıt var mı bak (productId + locationId ile)
        Inventory inventory = inventoryRepository
                .findByProductIdAndLocationId(product.getId(), location.getId())
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setProduct(product);
                    inv.setLocation(location);
                    inv.setQuantity(0);
                    inv.setReservedQuantity(0);
                    return inv;
                });

        inventory.setQuantity(request.getQuantity());
        inventory.setReservedQuantity(
                request.getReservedQuantity() != null ? request.getReservedQuantity() : 0
        );

        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    // Sadece yeni envanter ekleyen ve duplicate varsa hata fırlatan createInventory metodu
    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        // Duplicate kontrolü
        if (inventoryRepository.findByProductIdAndLocationId(product.getId(), location.getId()).isPresent()) {
            throw new IllegalArgumentException("Inventory for this product and location already exists");
        }
        Inventory inventory = Inventory.builder()
                .product(product)
                .location(location)
                .quantity(request.getQuantity())
                .reservedQuantity(request.getReservedQuantity() != null ? request.getReservedQuantity() : 0)
                .build();
        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    // Adet ayarlama (+ / -)
    @Transactional
    public InventoryResponse adjustInventory(Long inventoryId, Integer delta) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        int newQty = inventory.getQuantity() + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        inventory.setQuantity(newQty);

        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    // Soft delete düşünmüyorsan burada gerçekten silebilirsin
    @Transactional
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found");
        }
        inventoryRepository.deleteById(id);
    }

    @Transactional
    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        // Ürün ve lokasyon değiştirilmek isteniyorsa
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            inventory.setProduct(product);
        }

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
            inventory.setLocation(location);
        }

        inventory.setQuantity(request.getQuantity());
        inventory.setReservedQuantity(
                request.getReservedQuantity() != null ? request.getReservedQuantity() : 0
        );

        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }



    private InventoryResponse toResponse(Inventory inventory) {
        Product product = inventory.getProduct();
        Location location = inventory.getLocation();

        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .productSku(product != null ? product.getSku() : null)
                .locationId(location != null ? location.getId() : null)
                .locationCode(location != null ? location.getCode() : null)
                .locationName(location != null ? location.getDescription() : null)
                .warehouseId(location != null && location.getWarehouse() != null
                        ? location.getWarehouse().getId() : null)
                .warehouseName(location != null && location.getWarehouse() != null
                        ? location.getWarehouse().getName() : null)
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
