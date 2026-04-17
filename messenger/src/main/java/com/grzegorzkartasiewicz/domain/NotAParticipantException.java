package com.grzegorzkartasiewicz.domain;

public class NotAParticipantException extends RuntimeException {

  public NotAParticipantException(String message) {
    super(message);
  }
}
