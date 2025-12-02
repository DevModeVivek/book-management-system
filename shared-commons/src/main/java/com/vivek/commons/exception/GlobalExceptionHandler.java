package com.vivek.commons.exception;

import com.vivek.commons.constants.ErrorCodes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Enhanced Global Exception Handler with centralized error codes and reduced repetition
 * Uses refactored exception hierarchy and centralized constants
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ============= DOMAIN EXCEPTIONS =============
    
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, HttpServletRequest request) {
        
        log.warn("DomainException [{}]: {} - Path: {}", 
                ex.getErrorCode(), ex.getLocalizedMessage(), request.getRequestURI());
        
        HttpStatus status = getHttpStatusForDomainException(ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(ex.getTimestamp())
                .status(status.value())
                .error(ex.getErrorCode())
                .message(ex.getLocalizedMessage())
                .technicalMessage(ex.getTechnicalDetails())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(ex.getContextData())
                .build();
        
        HttpHeaders headers = createStandardHeaders(ex.getErrorCode(), errorResponse.getTraceId());
        return ResponseEntity.status(status)
                .headers(headers)
                .body(errorResponse);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        
        log.warn("ValidationException [{}]: {} - Path: {}", 
                ex.getErrorCode(), ex.getLocalizedMessage(), request.getRequestURI());
        
        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .timestamp(ex.getTimestamp())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ex.getErrorCode())
                .message(ex.getLocalizedMessage())
                .technicalMessage(ex.getTechnicalDetails())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .validationErrors(ex.getFieldErrors())
                .contextData(ex.getContextData())
                .build();
        
        HttpHeaders headers = createStandardHeaders(ex.getErrorCode(), errorResponse.getTraceId());
        headers.set("X-Validation-Error-Count", String.valueOf(ex.getFieldErrors().size()));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(errorResponse);
    }

    // ============= DATABASE EXCEPTIONS =============
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        log.error("DataIntegrityViolationException: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        String errorCode = determineDataIntegrityErrorCode(ex);
        String userMessage = getUserMessageForDataIntegrityError(ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(errorCode)
                .message(userMessage)
                .technicalMessage(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of("constraintType", "data_integrity"))
                .build();
        
        HttpHeaders headers = createStandardHeaders(errorCode, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(
            SQLException ex, HttpServletRequest request) {
        
        log.error("SQLException: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ErrorCodes.Database.SQL_ERROR)
                .message("Database operation failed")
                .technicalMessage("SQL Error: " + ex.getMessage())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of("sqlState", ex.getSQLState(), "errorCode", ex.getErrorCode()))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Database.SQL_ERROR, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {
        
        log.warn("EntityNotFoundException: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(ErrorCodes.Database.ENTITY_NOT_FOUND)
                .message("Requested resource not found")
                .technicalMessage(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of("entityType", "unknown"))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Database.ENTITY_NOT_FOUND, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(errorResponse);
    }

    // ============= AUTHENTICATION & AUTHORIZATION EXCEPTIONS =============
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        log.warn("AuthenticationException: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = createSecurityErrorResponse(
                ErrorCodes.Security.AUTHENTICATION_REQUIRED,
                "Authentication required",
                "Authentication failed: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        HttpHeaders headers = createSecurityHeaders(ErrorCodes.Security.AUTHENTICATION_REQUIRED, 
                errorResponse.getTraceId());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        
        log.warn("BadCredentialsException: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = createSecurityErrorResponse(
                ErrorCodes.Security.INVALID_CREDENTIALS,
                "Invalid credentials provided",
                "Bad credentials: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        HttpHeaders headers = createSecurityHeaders(ErrorCodes.Security.INVALID_CREDENTIALS, 
                errorResponse.getTraceId());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientAuthentication(
            InsufficientAuthenticationException ex, HttpServletRequest request) {
        
        log.warn("InsufficientAuthenticationException: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = createSecurityErrorResponse(
                ErrorCodes.Security.INSUFFICIENT_AUTHENTICATION,
                "Insufficient authentication",
                "Insufficient authentication: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        HttpHeaders headers = createSecurityHeaders(ErrorCodes.Security.INSUFFICIENT_AUTHENTICATION, 
                errorResponse.getTraceId());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).body(errorResponse);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        
        log.warn("Access denied for request: {} - {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = createSecurityErrorResponse(
                ErrorCodes.Security.ACCESS_DENIED,
                "Access denied",
                "Insufficient privileges: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Security.ACCESS_DENIED, 
                errorResponse.getTraceId());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(headers).body(errorResponse);
    }

    // ============= SPRING FRAMEWORK EXCEPTIONS =============
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("Validation failed for request: {}", request.getRequestURI());
        
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    error -> error.getDefaultMessage() != null 
                        ? error.getDefaultMessage() 
                        : "Invalid value",
                    (existing, replacement) -> existing + "; " + replacement
                ));
        
        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorCodes.Validation.GENERAL_ERROR)
                .message("Request validation failed")
                .technicalMessage(String.format("Validation failed for %d fields", fieldErrors.size()))
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .validationErrors(fieldErrors)
                .contextData(Map.of(
                    "fieldCount", ex.getBindingResult().getFieldErrorCount(),
                    "globalErrorCount", ex.getBindingResult().getGlobalErrorCount()
                ))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Validation.GENERAL_ERROR, 
                errorResponse.getTraceId());
        headers.set("X-Validation-Error-Count", String.valueOf(fieldErrors.size()));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.warn("Constraint violation for request: {} - {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, String> violations = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage,
                    (existing, replacement) -> existing + "; " + replacement
                ));
        
        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorCodes.Validation.CONSTRAINT_VIOLATION)
                .message("Constraint validation failed")
                .technicalMessage(String.format("Constraint violation for %d properties", violations.size()))
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .validationErrors(violations)
                .contextData(Map.of("violationCount", violations.size()))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Validation.CONSTRAINT_VIOLATION, 
                errorResponse.getTraceId());
        headers.set("X-Validation-Error-Count", String.valueOf(violations.size()));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        log.warn("HttpMessageNotReadableException for request: {} - {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorCodes.Validation.INVALID_FORMAT)
                .message("Invalid request format")
                .technicalMessage("Request body is not readable: " + ex.getMessage())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of("requestMethod", request.getMethod()))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Validation.INVALID_FORMAT, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.warn("MethodArgumentTypeMismatchException for request: {} - Parameter: {}", 
                request.getRequestURI(), ex.getName());
        
        Class<?> requiredType = ex.getRequiredType();
        String expectedTypeName = requiredType != null ? requiredType.getSimpleName() : "unknown";
        Object providedValue = ex.getValue();
        String providedValueStr = providedValue != null ? providedValue.toString() : "null";
        
        String message = String.format("Parameter '%s' has invalid type. Expected: %s", 
                ex.getName(), expectedTypeName);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorCodes.Validation.INVALID_FORMAT)
                .message("Invalid parameter type")
                .technicalMessage(message)
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of(
                    "parameter", ex.getName(),
                    "providedValue", providedValueStr,
                    "expectedType", expectedTypeName
                ))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Validation.INVALID_FORMAT, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        log.warn("MissingServletRequestParameterException for request: {} - Parameter: {}", 
                request.getRequestURI(), ex.getParameterName());
        
        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorCodes.Validation.REQUIRED_FIELD)
                .message("Required parameter missing")
                .technicalMessage(message)
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of(
                    "missingParameter", ex.getParameterName(),
                    "parameterType", ex.getParameterType()
                ))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Validation.REQUIRED_FIELD, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        log.warn("HttpRequestMethodNotSupportedException for request: {} - Method: {}", 
                request.getRequestURI(), ex.getMethod());
        
        String supportedMethods = ex.getSupportedMethods() != null ? 
                String.join(", ", ex.getSupportedMethods()) : "none";
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(ErrorCodes.System.METHOD_NOT_ALLOWED)
                .message("HTTP method not supported")
                .technicalMessage(String.format("Method '%s' not supported. Supported methods: %s", 
                        ex.getMethod(), supportedMethods))
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of(
                    "method", ex.getMethod(),
                    "supportedMethods", supportedMethods
                ))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.System.METHOD_NOT_ALLOWED, errorResponse.getTraceId());
        headers.set("Allow", supportedMethods);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        log.warn("NoHandlerFoundException for request: {} - {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(ErrorCodes.System.ENDPOINT_NOT_FOUND)
                .message("Endpoint not found")
                .technicalMessage(String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()))
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of(
                    "httpMethod", ex.getHttpMethod(),
                    "requestURL", ex.getRequestURL()
                ))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.System.ENDPOINT_NOT_FOUND, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(errorResponse);
    }

    // ============= GENERAL EXCEPTIONS =============

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.warn("IllegalArgumentException for request: {} - {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorCodes.Validation.INVALID_ARGUMENT)
                .message("Invalid argument provided")
                .technicalMessage(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(new HashMap<>())
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.Validation.INVALID_ARGUMENT, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        log.error("RuntimeException for request: {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ErrorCodes.System.RUNTIME_ERROR)
                .message("An unexpected error occurred")
                .technicalMessage("Runtime error: " + ex.getMessage())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of("exceptionType", ex.getClass().getSimpleName()))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.System.RUNTIME_ERROR, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unhandled Exception for request: {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ErrorCodes.System.INTERNAL_ERROR)
                .message("Internal server error")
                .technicalMessage("Unexpected error: " + ex.getMessage())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .contextData(Map.of(
                    "exceptionType", ex.getClass().getSimpleName(),
                    "exceptionPackage", ex.getClass().getPackage().getName()
                ))
                .build();
        
        HttpHeaders headers = createStandardHeaders(ErrorCodes.System.INTERNAL_ERROR, errorResponse.getTraceId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body(errorResponse);
    }

    // ============= UTILITY METHODS =============
    
    /**
     * Determine appropriate HTTP status for domain exceptions
     */
    private HttpStatus getHttpStatusForDomainException(DomainException ex) {
        String errorCode = ex.getErrorCode();
        
        if (errorCode.contains("NOT_FOUND")) return HttpStatus.NOT_FOUND;
        if (errorCode.contains("ALREADY_EXISTS")) return HttpStatus.CONFLICT;
        if (errorCode.contains("VALIDATION")) return HttpStatus.BAD_REQUEST;
        if (errorCode.contains("EXTERNAL_API")) return HttpStatus.SERVICE_UNAVAILABLE;
        if (errorCode.contains("SECURITY") || errorCode.contains("ACCESS")) return HttpStatus.FORBIDDEN;
        
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    /**
     * Create standardized headers for error responses
     */
    private HttpHeaders createStandardHeaders(String errorCode, String traceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Error-Code", errorCode);
        headers.set("X-Trace-Id", traceId);
        headers.set("X-Error-Category", getErrorCategory(errorCode));
        return headers;
    }
    
    /**
     * Create security-specific headers
     */
    private HttpHeaders createSecurityHeaders(String errorCode, String traceId) {
        HttpHeaders headers = createStandardHeaders(errorCode, traceId);
        headers.set("WWW-Authenticate", "Basic realm=\"Book Management System\"");
        return headers;
    }
    
    /**
     * Create standardized security error response
     */
    private ErrorResponse createSecurityErrorResponse(String errorCode, String userMessage, 
                                                    String technicalMessage, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(getHttpStatusForErrorCode(errorCode).value())
                .error(errorCode)
                .message(userMessage)
                .technicalMessage(technicalMessage)
                .path(path)
                .traceId(generateTraceId())
                .contextData(Map.of("securityViolation", true, "category", ErrorCodes.Categories.SECURITY))
                .build();
    }
    
    /**
     * Determine data integrity error code based on exception message
     */
    private String determineDataIntegrityErrorCode(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("isbn")) return ErrorCodes.Book.DUPLICATE_ISBN;
            if (message.contains("unique")) return ErrorCodes.Database.DUPLICATE_KEY;
        }
        return ErrorCodes.Database.CONSTRAINT_VIOLATION;
    }
    
    /**
     * Get user-friendly message for data integrity errors
     */
    private String getUserMessageForDataIntegrityError(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("isbn")) return "A book with this ISBN already exists";
            if (message.contains("unique")) return "This record already exists";
        }
        return "Data constraint violation";
    }
    
    /**
     * Get HTTP status for error code
     */
    private HttpStatus getHttpStatusForErrorCode(String errorCode) {
        if (errorCode.contains("NOT_FOUND")) return HttpStatus.NOT_FOUND;
        if (errorCode.contains("VALIDATION") || errorCode.contains("INVALID")) return HttpStatus.BAD_REQUEST;
        if (errorCode.contains("AUTH") || errorCode.contains("CREDENTIALS")) return HttpStatus.UNAUTHORIZED;
        if (errorCode.contains("ACCESS") || errorCode.contains("FORBIDDEN")) return HttpStatus.FORBIDDEN;
        if (errorCode.contains("DUPLICATE") || errorCode.contains("CONFLICT")) return HttpStatus.CONFLICT;
        if (errorCode.contains("EXTERNAL") || errorCode.contains("UNAVAILABLE")) return HttpStatus.SERVICE_UNAVAILABLE;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    /**
     * Extract error category from error code
     */
    private String getErrorCategory(String errorCode) {
        if (errorCode.startsWith("BOOK_")) return ErrorCodes.Categories.DOMAIN;
        if (errorCode.startsWith("VALIDATION_")) return ErrorCodes.Categories.VALIDATION;
        if (errorCode.startsWith("EXTERNAL_")) return ErrorCodes.Categories.EXTERNAL_API;
        if (errorCode.startsWith("SECURITY_")) return ErrorCodes.Categories.SECURITY;
        if (errorCode.startsWith("DATABASE_")) return ErrorCodes.Categories.DATABASE;
        return ErrorCodes.Categories.SYSTEM;
    }
    
    /**
     * Generate enhanced trace ID
     */
    private String generateTraceId() {
        String timestamp = String.valueOf(System.currentTimeMillis() % 100000);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "-" + uuid;
    }

    // ============= RESPONSE DTOs =============
    
    @Data
    @AllArgsConstructor
    @lombok.Builder
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String technicalMessage;
        private String path;
        private String traceId;
        private Map<String, Object> contextData;
        
        @lombok.Builder.Default
        private boolean success = false;
    }
    
    @Data
    @AllArgsConstructor
    @lombok.Builder
    public static class ValidationErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String technicalMessage;
        private String path;
        private String traceId;
        private Map<String, String> validationErrors;
        private Map<String, Object> contextData;
        
        @lombok.Builder.Default
        private boolean success = false;
    }
}