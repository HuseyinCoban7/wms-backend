package com.wms.integration;

import com.wms.dto.request.RegisterRequest;
import com.wms.dto.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class UserIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    void registerUser_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("User Test")
                .email("user+" + System.currentTimeMillis() + "@test.com")
                .password("password")
                .role("ROLE_WORKER")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<?>>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }
    @Test
    void registerUser_DuplicateEmail_ReturnsBadRequest() {
        String email = "userdup+" + System.currentTimeMillis() + "@test.com";
        RegisterRequest request = RegisterRequest.builder()
                .fullName("User Test")
                .email(email)
                .password("password")
                .role("ROLE_WORKER")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.exchange("/api/auth/register", HttpMethod.POST, entity, new ParameterizedTypeReference<ApiResponse<?>>() {});
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<?>>() {}
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
