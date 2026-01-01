package com.wms.service;

import com.wms.dto.request.ProductRequest;
import com.wms.dto.response.ProductResponse;
import com.wms.entity.Product;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .sku("TEST-001")
                .barcode("1234567890")
                .name("Test Product")
                .description("Test Description")
                .unit("PCS")
                .unitPrice(BigDecimal.valueOf(100.00))
                .minStockLevel(10)
                .category("Test")
                .build();

        product = Product.builder()
                .sku("TEST-001")
                .barcode("1234567890")
                .name("Test Product")
                .description("Test Description")
                .unit("PCS")
                .unitPrice(BigDecimal.valueOf(100.00))
                .minStockLevel(10)
                .category("Test")
                .build();
        product.setId(1L);
    }

    @Test
    void createProduct_Success() {
        when(productRepository.existsBySku(productRequest.getSku())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertEquals("TEST-001", response.getSku());
        assertEquals("Test Product", response.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_DuplicateSku_ThrowsException() {
        when(productRepository.existsBySku(productRequest.getSku())).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> productService.createProduct(productRequest));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProduct(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TEST-001", response.getSku());
    }

    @Test
    void getProduct_NotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProduct(1L));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }
}