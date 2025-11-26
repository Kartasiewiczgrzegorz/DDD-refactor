package com.grzegorzkartasiewicz.domain;

public class UnauthorizedToEditException extends RuntimeException {

  public UnauthorizedToEditException(String message) {
    super(message);
  }
}
