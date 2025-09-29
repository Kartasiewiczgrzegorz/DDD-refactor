package com.grzegorzkartasiewicz.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.grzegorzkartasiewicz.domain.Email;
import org.junit.jupiter.api.Test;

class EmailTest {

  @Test
  void validateShouldNotThrowExceptionWhenEmailIsValid() {
    Email email = new Email("email@test.com");

    email.validate();
  }

  @Test
  void validateShouldThrowExceptionWhenEmailIsNull() {
    Email email = new Email(null);

    assertThrows(IllegalArgumentException.class, email::validate);
  }

  @Test
  void validateShouldThrowExceptionWhenEmailIsInvalid() {
    Email email = new Email("invalidEmail");

    assertThrows(IllegalArgumentException.class, email::validate);
  }
}