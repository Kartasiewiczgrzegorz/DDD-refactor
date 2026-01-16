package com.grzegorzkartasiewicz.domain;

public class RelationAlreadyExistsException extends RuntimeException {

  public RelationAlreadyExistsException(String message) {
    super(message);
  }
}
