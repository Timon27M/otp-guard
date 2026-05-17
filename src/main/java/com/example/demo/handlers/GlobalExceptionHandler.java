package com.example.demo.handlers;

import com.example.demo.dto.global.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({IllegalArgumentException.class, ServletException.class})
  public ResponseEntity<ErrorResponse> handleAuthExceptions(
          Exception ex) {

    ErrorResponse error =
            new ErrorResponse(401, ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(error);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
    ErrorResponse error = new ErrorResponse(403, ex.getMessage()) {
    };
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    ErrorResponse error = new ErrorResponse(403, "Переданы не валидные данные");
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(404, ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ErrorResponse handleAccessDenied(AccessDeniedException e) {
    return new ErrorResponse(
            403,
            "You don't have permission to access this resource. Admin role required."
    );
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {
    ErrorResponse error = new ErrorResponse(403, "Access Denied: Admin role required");
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    ErrorResponse error = new ErrorResponse(500, "Внутренняя ошибка сервера");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
    ErrorResponse error = new ErrorResponse(ex.getStatusCode().value(), ex.getReason());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}
