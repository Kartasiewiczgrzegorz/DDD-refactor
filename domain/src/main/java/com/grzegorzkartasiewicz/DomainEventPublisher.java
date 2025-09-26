package com.grzegorzkartasiewicz;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
