package com.wms.service;

import com.wms.dto.request.SupplierRequest;
import com.wms.dto.response.SupplierResponse;
import com.wms.entity.Supplier;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (supplierRepository.existsByCode(request.getCode())) {
            throw new BusinessRuleException("Supplier with code " + request.getCode() + " already exists");
        }

        Supplier supplier = Supplier.builder()
                .code(request.getCode())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .active(true)
                .build();

        supplier = supplierRepository.save(supplier);
        return mapToResponse(supplier);
    }

    @Transactional(readOnly = true)
    public SupplierResponse getSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        return mapToResponse(supplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers(boolean activeOnly) {
        List<Supplier> suppliers = activeOnly
                ? supplierRepository.findByActiveTrue()
                : supplierRepository.findByDeletedFalse();

        return suppliers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponse> searchSuppliers(String search, Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                search, search, pageable);
        return suppliers.map(this::mapToResponse);
    }

    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        if (!supplier.getCode().equals(request.getCode()) &&
                supplierRepository.existsByCode(request.getCode())) {
            throw new BusinessRuleException("Supplier with code " + request.getCode() + " already exists");
        }

        supplier.setCode(request.getCode());
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());

        supplier = supplierRepository.save(supplier);
        return mapToResponse(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        supplier.setDeleted(true);
        supplierRepository.save(supplier);
    }

    @Transactional
    public void activateSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        supplier.setActive(true);
        supplierRepository.save(supplier);
    }

    @Transactional
    public void deactivateSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .code(supplier.getCode())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .active(supplier.getActive())
                .build();
    }
}