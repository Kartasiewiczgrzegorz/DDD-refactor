package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.AlreadyFollowingException;
import com.grzegorzkartasiewicz.domain.RelationAlreadyExistsException;
import com.grzegorzkartasiewicz.domain.RequestAlreadySentException;
import com.grzegorzkartasiewicz.domain.RequestNotExistsException;
import com.grzegorzkartasiewicz.domain.SelfInteractionException;
import com.grzegorzkartasiewicz.domain.ValidationException;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.grzegorzkartasiewicz.adapters")
class SocialExceptionHandler {

  @ExceptionHandler(SelfInteractionException.class)
  ResponseEntity<String> handleSelfInteraction(SelfInteractionException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(ValidationException.class)
  ResponseEntity<String> handleValidation(ValidationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler({
      RelationAlreadyExistsException.class,
      RequestAlreadySentException.class,
      AlreadyFollowingException.class
  })
  ResponseEntity<String> handleConflict(RuntimeException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
  }

  @ExceptionHandler(RequestNotExistsException.class)
  ResponseEntity<String> handleRequestNotExists(RequestNotExistsException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  ResponseEntity<String> handleNotFound(NoSuchElementException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
  }
}
