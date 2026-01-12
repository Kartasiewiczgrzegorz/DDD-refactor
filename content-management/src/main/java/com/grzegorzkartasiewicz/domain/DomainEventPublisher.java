package com.grzegorzkartasiewicz.domain;

/**
 * Interface for publishing domain events. Decouples the domain from the event publishing
 * mechanism.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a domain event.
     *
     * @param event The event to publish.
     */
    void publish(DomainEvent event);
}
