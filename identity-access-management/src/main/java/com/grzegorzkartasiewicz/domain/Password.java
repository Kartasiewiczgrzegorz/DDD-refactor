package com.grzegorzkartasiewicz.domain;

public record Password(String password) {

  private static final String PASSWORD_VALIDATION_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()]).{8,}$";

  public void validate() {
    if (password == null) {
      throw new ValidationException("Password is required");
    }
    if (!password.matches(PASSWORD_VALIDATION_REGEX)) {
      throw new ValidationException("Invalid password");
    }
  }

  public void isEqual(String password) {
    if (!this.password.equals(password)) {
      throw new PasswordDoesNotMatchException("Passwords do not match");
    }
  }
}
