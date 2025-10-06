package com.grzegorzkartasiewicz.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.grzegorzkartasiewicz.domain.vo.Password;
import org.junit.jupiter.api.Test;

class PasswordTest {

  @Test
  void validateShouldNotThrowExceptionWhenPasswordIsValid() {
    Password.validate("Passw0ord$#");
  }

  @Test
  void validateShouldThrowExceptionWhenPasswordIsNull() {
    assertThrows(ValidationException.class, () -> Password.validate(null));
  }

  @Test
  void validateShouldThrowExceptionWhenPasswordIsInvalid() {
    assertThrows(ValidationException.class, () -> Password.validate("bad"));
  }

}