package com.grzegorzkartasiewicz.app;

public class ConversationNotExistsException extends RuntimeException {

  public ConversationNotExistsException(String message) {
    super(message);
  }
}
