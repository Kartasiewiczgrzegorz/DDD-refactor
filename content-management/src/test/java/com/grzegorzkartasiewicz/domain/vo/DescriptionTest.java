package com.grzegorzkartasiewicz.domain.vo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grzegorzkartasiewicz.domain.ValidationException;
import org.junit.jupiter.api.Test;

class DescriptionTest {

  @Test
  void shouldCreateDescriptionWhenTextIsValid() {
    assertDoesNotThrow(() -> new Description("Description"));
  }

  @Test
  void shouldThrowExceptionWhenDescriptionIsNull() {
    assertThrows(ValidationException.class, () -> new Description(null));
  }

  @Test
  void shouldThrowExceptionWhenDescriptionIsInvalid() {
    assertThrows(ValidationException.class, () -> new Description(" "));
  }
}