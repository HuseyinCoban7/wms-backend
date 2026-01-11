package com.wms.integration;

import com.wms.dto.request.InventoryRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.entity.User;
import com.wms.entity.Location;
import com.wms.entity.Product;
import com.wms.entity.Warehouse;
import com.wms.enums.Role;
import com.wms.repository.UserRepository;
import com.wms.repository.LocationRepository;
import com.wms.repository.ProductRepository;
import com.wms.repository.WarehouseRepository;
import com.wms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class InventoryIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    private String token;
    private Long productId;
    private Long locationId;
    @BeforeEach
    void setUp() {
        String email = "inventory+" + UUID.randomUUID() + "@test.com";
        User user = User.builder()
                .fullName("Inventory User")
                .email(email)
                .password(passwordEncoder.encode("password"))
                .role(Role.ROLE_ADMIN)
                .active(true)
                .build();
        userRepository.save(user);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(user.getRole().name())))
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        token = jwtTokenProvider.generateToken(authentication);

        // Benzersiz Warehouse ekle
        String warehouseCode = "WH-TEST-" + UUID.randomUUID();
        Warehouse warehouse = Warehouse.builder()
                .code(warehouseCode)
                .name("Test Warehouse")
                .address("Test Address")
                .city("Test City")
                .country("Test Country")
                .build();
        warehouse = warehouseRepository.save(warehouse);
        // Benzersiz Product ekle
        String productSku = "SKU-TEST-" + UUID.randomUUID();
        String productBarcode = "BARCODE-TEST-" + UUID.randomUUID();
        Product product = Product.builder()
                .sku(productSku)
                .barcode(productBarcode)
                .name("Test Product")
                .description("Test Description")
                .unit("pcs")
                .unitPrice(new java.math.BigDecimal("10.00"))
                .minStockLevel(0)
                .build();
        product = productRepository.save(product);
        // Benzersiz Location ekle
        String locationCode = "LOC-TEST-" + UUID.randomUUID();
        Location location = Location.builder()
                .code(locationCode)
                .description("Test Location")
                .warehouse(warehouse)
                .build();
        location = locationRepository.save(location);
        productId = product.getId();
        locationId = location.getId();
    }
    @Test
    void addInventory_Success() {
        InventoryRequest request = InventoryRequest.builder()
                .productId(productId)
                .locationId(locationId)
                .quantity(100)
                .reservedQuantity(0)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<InventoryRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
                "/api/inventory/create",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }
    @Test
    void addInventory_DuplicateSkuLocation_ReturnsBadRequest() {
        InventoryRequest request = InventoryRequest.builder()
                .productId(productId)
                .locationId(locationId)
                .quantity(100)
                .reservedQuantity(0)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<InventoryRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.exchange("/api/inventory/create", HttpMethod.POST, entity, ApiResponse.class);
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
                "/api/inventory/create",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
