package domain;

public record Name(String name, String surname) {

  public void validate() {
    if (this.name == null) {
      throw new IllegalArgumentException("Name is required");
    }
    if (this.surname == null) {
      throw new IllegalArgumentException("Surname is required");
    }
  }
}
