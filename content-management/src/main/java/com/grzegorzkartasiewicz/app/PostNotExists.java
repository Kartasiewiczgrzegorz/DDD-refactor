package com.grzegorzkartasiewicz.app;

public class PostNotExists extends RuntimeException {

  public PostNotExists(String message) {
    super(message);
  }
}
