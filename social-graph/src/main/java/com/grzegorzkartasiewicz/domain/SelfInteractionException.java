package com.grzegorzkartasiewicz.domain;

public class SelfInteractionException extends RuntimeException {

  public SelfInteractionException(String message) {
    super(message);
  }
}
