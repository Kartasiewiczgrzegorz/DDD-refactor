package com.grzegorzkartasiewicz.domain.vo;

import com.grzegorzkartasiewicz.domain.ValidationException;

public record Name(String name, String surname) {

  public void validate() {
    if (this.name == null) {
      throw new ValidationException("Name is required");
    }
    if (this.surname == null) {
      throw new ValidationException("Surname is required");
    }
  }
}
