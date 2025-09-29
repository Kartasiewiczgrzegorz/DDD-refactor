package domain;

public record Email(String email) {

  private static final String EMAIL_VALIDATION_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

  public void validate() {
    if (this.email == null) {
      throw new IllegalArgumentException("Email is required");
    }
    if (!this.email.matches(EMAIL_VALIDATION_REGEX)) {
      throw new IllegalArgumentException("Invalid email");
    }
  }
}
