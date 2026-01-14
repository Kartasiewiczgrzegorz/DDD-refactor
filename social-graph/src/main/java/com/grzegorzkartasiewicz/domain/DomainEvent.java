package com.grzegorzkartasiewicz.domain;

public interface DomainEvent {

  enum State {
    UPDATED, DELETED, CREATED
  }
}
