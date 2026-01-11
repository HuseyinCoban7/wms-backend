package com.wms.integration;

import com.wms.dto.request.OrderRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.entity.User;
import com.wms.entity.Warehouse;
import com.wms.enums.Role;
import com.wms.repository.UserRepository;
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
class OrderIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private WarehouseRepository warehouseRepository;
    private String token;
    private Long warehouseId;
    @BeforeEach
    void setUp() {
        String email = "order+" + UUID.randomUUID() + "@test.com";
        User user = User.builder()
                .fullName("Order User")
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
        // Depo ekle
        String warehouseCode = "TEST-WH-" + UUID.randomUUID();
        Warehouse warehouse = Warehouse.builder()
                .code(warehouseCode)
                .name("Test Warehouse")
                .address("Test Address")
                .city("Test City")
                .country("Test Country")
                .build();
        warehouseRepository.save(warehouse);
        warehouseId = warehouse.getId();
    }
    @Test
    void createOrder_Success() {
        OrderRequest request = OrderRequest.builder()
                .customerName("Test Customer")
                .shippingAddress("Adres 1")
                .warehouseId(warehouseId)
                .orderDate(java.time.LocalDate.now())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
                "/api/orders",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<?>>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }
    @Test
    void createOrder_DuplicateOrderNumber_AllowsMultipleOrders() {
        OrderRequest request = OrderRequest.builder()
                .customerName("Test Customer")
                .shippingAddress("Adres 2")
                .warehouseId(warehouseId)
                .orderDate(java.time.LocalDate.now())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ApiResponse<?>> firstResponse = restTemplate.exchange(
                "/api/orders",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<?>>() {}
        );
        ResponseEntity<ApiResponse<?>> secondResponse = restTemplate.exchange(
                "/api/orders",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<?>>() {}
        );
        assertEquals(HttpStatus.OK, firstResponse.getStatusCode());
        assertEquals(HttpStatus.OK, secondResponse.getStatusCode());
    }
}
