package com.grzegorzkartasiewicz.domain;

public class RequestNotExistsException extends RuntimeException {

  public RequestNotExistsException(String message) {
    super(message);
  }
}
