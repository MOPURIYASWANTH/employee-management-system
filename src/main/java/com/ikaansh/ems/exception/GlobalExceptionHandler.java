package com.ikaansh.ems.exception;

import com.ikaansh.ems.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String,String> errors = new LinkedHashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Validation failed.", req, errors);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiError> badRequest(Exception ex, HttpServletRequest req) { return error(HttpStatus.BAD_REQUEST, "Request body or query parameters are invalid.", req, null); }
    @ExceptionHandler(ResourceNotFoundException.class) public ResponseEntity<ApiError> notFound(ResourceNotFoundException ex, HttpServletRequest req) { return error(HttpStatus.NOT_FOUND, ex.getMessage(), req, null); }
    @ExceptionHandler(ConflictException.class) public ResponseEntity<ApiError> conflict(ConflictException ex, HttpServletRequest req) { return error(HttpStatus.CONFLICT, ex.getMessage(), req, null); }
    @ExceptionHandler(UnauthorizedException.class) public ResponseEntity<ApiError> unauthorized(UnauthorizedException ex, HttpServletRequest req) { return error(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null); }
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<ApiError> integrity(DataIntegrityViolationException ex, HttpServletRequest req) { return error(HttpStatus.CONFLICT, "The request conflicts with an existing database record.", req, null); }
    @ExceptionHandler(Exception.class) public ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest req) { return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", req, null); }
    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest req, Map<String,String> fields) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, req.getRequestURI(), fields));
    }
}
