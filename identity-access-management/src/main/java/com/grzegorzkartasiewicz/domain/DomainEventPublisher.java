package com.grzegorzkartasiewicz.domain;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
