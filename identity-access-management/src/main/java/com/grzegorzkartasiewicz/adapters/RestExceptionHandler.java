package com.grzegorzkartasiewicz.adapters; // Upewnij się, że pakiet jest poprawny

import com.grzegorzkartasiewicz.app.InvalidCredentialsException;
import com.grzegorzkartasiewicz.app.UserAlreadyExistsException;
import com.grzegorzkartasiewicz.app.UserBlockedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class RestExceptionHandler {

  @ExceptionHandler(UserAlreadyExistsException.class)
  ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ex.getMessage());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ex.getMessage());
  }

  @ExceptionHandler(UserBlockedException.class)
  ResponseEntity<String> handleUserBlocked(UserBlockedException ex) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ex.getMessage());
  }
}