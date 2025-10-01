package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.DomainEvent;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher innerPublisher;

    public SpringDomainEventPublisher(final ApplicationEventPublisher innerPublisher) {
        this.innerPublisher = innerPublisher;
    }

  @Override
  public void publish(DomainEvent event) {
    innerPublisher.publishEvent(event);
  }
}
