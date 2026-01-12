package com.grzegorzkartasiewicz.domain.vo;

import com.grzegorzkartasiewicz.domain.ValidationException;

public record RawPassword(String rawPassword) {

  private static final String PASSWORD_VALIDATION_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()]).{8,}$";

  public RawPassword {
    if (rawPassword == null || rawPassword.isBlank()) {
      throw new ValidationException("Password is required");
    }
    if (!rawPassword.matches(PASSWORD_VALIDATION_REGEX)) {
      throw new ValidationException(
          "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one special character.");
    }
  }
}
