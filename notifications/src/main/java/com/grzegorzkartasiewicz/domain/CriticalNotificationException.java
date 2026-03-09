package com.grzegorzkartasiewicz.domain;

public class CriticalNotificationException extends RuntimeException {

  public CriticalNotificationException(String message) {
    super(message);
  }
}
