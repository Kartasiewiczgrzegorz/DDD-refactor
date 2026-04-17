package com.grzegorzkartasiewicz.domain.vo;

import com.grzegorzkartasiewicz.domain.ValidationException;

public record MessageContent(String value) {

  public MessageContent {
    if (value == null) {
      throw new ValidationException("value is null");
    }
    if (value.isEmpty()) {
      throw new ValidationException("Empty content");
    }
    if (value.isBlank()) {
      throw new ValidationException("Blank content");
    }
    if (value.length() > 1000) {
      throw new ValidationException("Too long");
    }
  }
}
