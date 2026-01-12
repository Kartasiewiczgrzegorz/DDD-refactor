package com.grzegorzkartasiewicz.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grzegorzkartasiewicz.domain.vo.Name;
import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void shouldCreateNameWhenNameIsValid() {
    assertDoesNotThrow(() -> new Name("Name", "Surname"));
  }

  @Test
  void shouldThrowExceptionWhenNameIsNull() {
    assertThrows(ValidationException.class, () -> new Name(null, "Surname"));
  }

  @Test
  void shouldThrowExceptionWhenSurnameIsNull() {
    assertThrows(ValidationException.class, () -> new Name("Name", null));
  }
}