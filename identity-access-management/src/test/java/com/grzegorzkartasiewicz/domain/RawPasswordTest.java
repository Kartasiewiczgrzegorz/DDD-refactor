package com.grzegorzkartasiewicz.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grzegorzkartasiewicz.domain.vo.RawPassword;
import org.junit.jupiter.api.Test;

class RawPasswordTest {

  @Test
  void shouldCreateRawPasswordWhenRawPasswordIsValid() {
    assertDoesNotThrow(() -> new RawPassword("Passw0ord$#"));
  }

  @Test
  void shouldThrowExceptionWhenRawPasswordIsNull() {
    assertThrows(ValidationException.class, () -> new RawPassword(null));
  }

  @Test
  void shouldThrowExceptionWhenRawPasswordIsInvalid() {
    assertThrows(ValidationException.class, () -> new RawPassword("bad"));
  }

}