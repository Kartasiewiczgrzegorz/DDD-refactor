package com.grzegorzkartasiewicz.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grzegorzkartasiewicz.domain.vo.Email;
import org.junit.jupiter.api.Test;

class EmailTest {

  @Test
  void shouldCreateEmailWhenEmailIsValid() {
    assertDoesNotThrow(() -> new Email("email@test.com"));
  }

  @Test
  void shouldThrowExceptionWhenEmailIsNull() {
    assertThrows(ValidationException.class, () -> new Email(null));
  }

  @Test
  void shouldThrowExceptionWhenEmailIsInvalid() {
    assertThrows(ValidationException.class, () -> new Email("invalidEmail"));
  }
}