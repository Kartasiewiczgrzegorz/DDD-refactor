package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.DomainEvent;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SpringDomainEventPublisher implements DomainEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void publish(DomainEvent event) {
    applicationEventPublisher.publishEvent(event);
  }
}
