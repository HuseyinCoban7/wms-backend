package com.wms.exception;

import com.wms.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
    logger.error("Resource not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.<Void>builder()
                    .success(false)
                    .error(ex.getMessage())
                    .traceId(MDC.get("requestId"))
                    .build());
  }

  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException ex) {
    logger.error("Business rule violation: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.<Void>builder()
                    .success(false)
                    .error(ex.getMessage())
                    .traceId(MDC.get("requestId"))
                    .build());
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<ApiResponse<Void>> handleInsufficientStock(InsufficientStockException ex) {
    logger.error("Insufficient stock: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.<Void>builder()
                    .success(false)
                    .error(ex.getMessage())
                    .traceId(MDC.get("requestId"))
                    .build());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
    logger.error("Unauthorized: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.<Void>builder()
                    .success(false)
                    .error(ex.getMessage())
                    .traceId(MDC.get("requestId"))
                    .build());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
    logger.error("Bad credentials: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.<Void>builder()
                    .success(false)
                    .error("Invalid email or password")
                    .traceId(MDC.get("requestId"))
                    .build());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
    logger.error("Access denied: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.<Void>builder()
                    .success(false)
                    .error("Access denied")
                    .traceId(MDC.get("requestId"))
                    .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    logger.error("Validation errors: {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.<Map<String, String>>builder()
                    .success(false)
                    .data(errors)
                    .error("Validation failed")
                    .traceId(MDC.get("requestId"))
                    .build());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
    logger.error("Bad request: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.<Void>builder()
                    .success(false)
                    .error(ex.getMessage())
                    .traceId(MDC.get("requestId"))
                    .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
    logger.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.<Void>builder()
                    .success(false)
                    .error("An unexpected error occurred")
                    .traceId(MDC.get("requestId"))
                    .build());
  }
}
