package com.grzegorzkartasiewicz.app;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
