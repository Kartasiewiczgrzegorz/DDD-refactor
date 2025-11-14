package com.grzegorzkartasiewicz.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.grzegorzkartasiewicz.domain.ValidationException;
import org.junit.jupiter.api.Test;

class DescriptionTest {

  @Test
  void validateShouldNotThrowExceptionWhenDescriptionIsValid() {
    Description description = new Description("Description");

    description.validate();
  }

  @Test
  void validateShouldThrowExceptionWhenDescriptionIsNull() {
    Description description = new Description(null);

    assertThrows(ValidationException.class, description::validate);
  }

  @Test
  void validateShouldThrowExceptionWhenDescriptionIsInvalid() {
    Description description = new Description(" ");

    assertThrows(ValidationException.class, description::validate);
  }
}