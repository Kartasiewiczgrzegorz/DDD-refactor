package com.grzegorzkartasiewicz.domain;

public class CannotReadOwnMessageException extends RuntimeException {

  public CannotReadOwnMessageException(String message) {
    super(message);
  }
}
