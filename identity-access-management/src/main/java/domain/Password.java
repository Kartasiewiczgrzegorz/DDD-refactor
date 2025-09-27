package domain;

public record Password(String password) {

  private static final String PASSWORD_VALIDATION_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()]).{8,}$";

  public void validate() {
    if (password == null) {
      throw new IllegalArgumentException("Password is required");
    }
    if (!password.matches(PASSWORD_VALIDATION_REGEX)) {
      throw new IllegalArgumentException("Invalid password");
    }
  }
}
