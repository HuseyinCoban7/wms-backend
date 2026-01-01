package com.wms.controller;

import com.wms.dto.request.LoginRequest;
import com.wms.dto.request.RegisterRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.JwtResponse;
import com.wms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse<JwtResponse>> register(@Valid @RequestBody RegisterRequest request) {
        JwtResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.<JwtResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.<JwtResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }
}