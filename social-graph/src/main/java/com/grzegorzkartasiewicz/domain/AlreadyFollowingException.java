package com.grzegorzkartasiewicz.domain;

public class AlreadyFollowingException extends RuntimeException {

  public AlreadyFollowingException(String message) {
    super(message);
  }
}
