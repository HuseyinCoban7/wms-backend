package com.wms.service;

import com.wms.dto.request.ProductRequest;
import com.wms.dto.response.ProductResponse;
import com.wms.entity.Product;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new BusinessRuleException("Product with SKU " + request.getSku() + " already exists");
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .name(request.getName())
                .description(request.getDescription())
                .unit(request.getUnit())
                .unitPrice(request.getUnitPrice())
                .minStockLevel(request.getMinStockLevel())
                .category(request.getCategory())
                .build();

        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String search, Pageable pageable) {
        return productRepository.searchProducts(search, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnit(request.getUnit());
        product.setUnitPrice(request.getUnitPrice());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setCategory(request.getCategory());

        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        // ✅ Hard delete: DB'den tamamen sil
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .name(product.getName())
                .description(product.getDescription())
                .unit(product.getUnit())
                .unitPrice(product.getUnitPrice())
                .minStockLevel(product.getMinStockLevel())
                .category(product.getCategory())
                .build();
    }
}