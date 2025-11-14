package com.grzegorzkartasiewicz.domain.vo;

import com.grzegorzkartasiewicz.domain.ValidationException;

public record Description(String text) {

  public void validate() {
    if (this.text == null || text.isBlank()) {
      throw new ValidationException("Name is required");
    }
  }
}
