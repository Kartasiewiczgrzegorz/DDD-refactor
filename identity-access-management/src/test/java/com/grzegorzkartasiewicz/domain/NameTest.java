package com.grzegorzkartasiewicz.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.grzegorzkartasiewicz.domain.vo.Name;
import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void validateShouldNotThrowExceptionWhenNameIsValid() {
    Name name = new Name("Name", "Surname");

    name.validate();
  }

  @Test
  void validateShouldThrowExceptionWhenNameIsNull() {
    Name name = new Name(null, "Surname");

    assertThrows(ValidationException.class, name::validate);
  }

  @Test
  void validateShouldThrowExceptionWhenSurnameIsNull() {
    Name name = new Name("Name", null);

    assertThrows(ValidationException.class, name::validate);
  }
}