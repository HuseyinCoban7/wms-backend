package com.wms.integration;

import com.wms.dto.request.ProductRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.entity.User;
import com.wms.enums.Role;
import com.wms.repository.UserRepository;
import com.wms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ProductIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;

    @BeforeEach
    void setUp() {
        String email = "test+" + UUID.randomUUID() + "@test.com";

        User user = User.builder()
                .fullName("Test User")
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
    }

    @Test
    void createProduct_Success() {
        ProductRequest request = ProductRequest.builder()
                .sku("INT-TEST-001")
                .barcode("9876543210")
                .name("Integration Test Product")
                .unit("PCS")
                .unitPrice(BigDecimal.valueOf(50.00))
                .minStockLevel(5)
                .category("Test")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.POST,
                entity,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void createProduct_DuplicateSku_ReturnsBadRequest() {
        ProductRequest request = ProductRequest.builder()
                .sku("INT-DUP-001")
                .name("Duplicate Product")
                .unit("PCS")
                .unitPrice(BigDecimal.valueOf(50.00))
                .minStockLevel(5)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductRequest> entity = new HttpEntity<>(request, headers);

        // First creation
        restTemplate.exchange("/api/products", HttpMethod.POST, entity, ApiResponse.class);

        // Second creation with same SKU
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.POST,
                entity,
                ApiResponse.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
