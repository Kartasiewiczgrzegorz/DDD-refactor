package com.grzegorzkartasiewicz.domain.vo;

import com.grzegorzkartasiewicz.domain.ValidationException;

public record Name(String name, String surname) {

  public Name {
    if (name == null || name.isBlank()) {
      throw new ValidationException("Name is required");
    }
    if (surname == null || surname.isBlank()) {
      throw new ValidationException("Surname is required");
    }
  }
}
