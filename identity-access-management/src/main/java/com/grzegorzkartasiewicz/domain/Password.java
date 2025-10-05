package com.grzegorzkartasiewicz.domain;

public record Password(String password) {

  private static final String PASSWORD_VALIDATION_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()]).{8,}$";

  public static void validate(String rawPassword) {
    if (rawPassword == null) {
      throw new ValidationException("Password is required");
    }
    if (!rawPassword.matches(PASSWORD_VALIDATION_REGEX)) {
      throw new ValidationException("Invalid password");
    }
  }
}
