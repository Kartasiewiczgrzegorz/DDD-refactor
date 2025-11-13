package com.grzegorzkartasiewicz.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.grzegorzkartasiewicz.domain.vo.RawPassword;
import org.junit.jupiter.api.Test;

class RawPasswordTest {

  @Test
  void validateShouldNotThrowExceptionWhenRawPasswordIsValid() {
    RawPassword.validate("Passw0ord$#");
  }

  @Test
  void validateShouldThrowExceptionWhenRawPasswordIsNull() {
    assertThrows(ValidationException.class, () -> RawPassword.validate(null));
  }

  @Test
  void validateShouldThrowExceptionWhenRawPasswordIsInvalid() {
    assertThrows(ValidationException.class, () -> RawPassword.validate("bad"));
  }

}