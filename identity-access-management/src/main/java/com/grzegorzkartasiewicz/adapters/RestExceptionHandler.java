package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.InvalidCredentialsException;
import com.grzegorzkartasiewicz.app.InvalidUserDataException;
import com.grzegorzkartasiewicz.app.UserAlreadyExistsException;
import com.grzegorzkartasiewicz.app.UserBlockedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class RestExceptionHandler {

  @ExceptionHandler(InvalidUserDataException.class)
  ResponseEntity<String> handleInvalidUserDataException(InvalidUserDataException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ex.getMessage());
  }

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