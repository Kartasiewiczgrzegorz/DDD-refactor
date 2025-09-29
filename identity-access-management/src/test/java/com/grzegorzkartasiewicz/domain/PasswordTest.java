package com.grzegorzkartasiewicz.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.grzegorzkartasiewicz.domain.Password;
import org.junit.jupiter.api.Test;

class PasswordTest {

  @Test
  void validateShouldNotThrowExceptionWhenPasswordIsValid() {
    Password password = new Password("Passw0ord$#");

    password.validate();
  }

  @Test
  void validateShouldThrowExceptionWhenPasswordIsNull() {
    Password password = new Password(null);

    assertThrows(IllegalArgumentException.class, password::validate);
  }

  @Test
  void validateShouldThrowExceptionWhenPasswordIsInvalid() {
    Password password = new Password("bad");

    assertThrows(IllegalArgumentException.class, password::validate);
  }

}