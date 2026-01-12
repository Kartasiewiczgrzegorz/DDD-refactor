package com.grzegorzkartasiewicz.domain.vo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grzegorzkartasiewicz.domain.ValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthorIdTest {

  @Test
  void shouldCreateAuthorIdWhenIdIsValid() {
    assertDoesNotThrow(() -> new AuthorId(UUID.randomUUID()));
  }

  @Test
  void shouldThrowExceptionWhenAuthorIdIsNull() {
    assertThrows(ValidationException.class, () -> new AuthorId(null));
  }
}