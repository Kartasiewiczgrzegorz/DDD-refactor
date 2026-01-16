package com.grzegorzkartasiewicz.domain;

public class RequestAlreadySentException extends RuntimeException {

  public RequestAlreadySentException(String message) {
    super(message);
  }
}
