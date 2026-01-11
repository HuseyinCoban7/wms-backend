package com.wms.service;

import com.wms.dto.request.SupplierRequest;
import com.wms.dto.response.SupplierResponse;
import com.wms.entity.Supplier;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder().code("S-1").name("Supplier").email("a@b.com").build();
        supplier.setId(1L);
    }

    @Test
    void createSupplier_Success() {
        SupplierRequest req = new SupplierRequest();
        req.setCode("S-1");
        req.setName("Supplier");
        req.setEmail("a@b.com");

        when(supplierRepository.existsByCode("S-1")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        SupplierResponse resp = supplierService.createSupplier(req);

        assertNotNull(resp);
        assertEquals("S-1", resp.getCode());
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    void createSupplier_DuplicateCode_Throws() {
        when(supplierRepository.existsByCode("S-1")).thenReturn(true);
        SupplierRequest req = new SupplierRequest();
        req.setCode("S-1");
        req.setName("Supplier");
        assertThrows(BusinessRuleException.class, () -> supplierService.createSupplier(req));
    }

    @Test
    void getSupplier_NotFound_Throws() {
        when(supplierRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplier(2L));
    }
}
