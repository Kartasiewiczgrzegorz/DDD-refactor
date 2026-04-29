package com.grzegorzkartasiewicz.app;

public class MessageNotExistsException extends RuntimeException {

  public MessageNotExistsException(String message) {
    super(message);
  }
}
