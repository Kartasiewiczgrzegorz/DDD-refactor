package com.grzegorzkartasiewicz.domain.vo;

import com.grzegorzkartasiewicz.domain.ValidationException;

public record Description(String text) {

  public Description {
    if (text == null || text.isBlank()) {
      throw new ValidationException("Description is required");
    }
  }
}
