package com.grzegorzkartasiewicz.app;

public class InvalidUserDataException extends RuntimeException {

  public InvalidUserDataException(String message) {
    super(message);
  }
}
