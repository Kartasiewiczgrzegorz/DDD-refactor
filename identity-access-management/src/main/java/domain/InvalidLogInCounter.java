package domain;

public record InvalidLogInCounter(int counter) {

  public InvalidLogInCounter increase() {
    return new InvalidLogInCounter(counter + 1);
  }
}
