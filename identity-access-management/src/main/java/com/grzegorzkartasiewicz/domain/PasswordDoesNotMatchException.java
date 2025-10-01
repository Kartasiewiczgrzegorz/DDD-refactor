package com.grzegorzkartasiewicz.domain;

public class PasswordDoesNotMatchException extends RuntimeException {

  public PasswordDoesNotMatchException(String message) {
    super(message);
  }
}
