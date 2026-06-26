package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.MessengerService;
import com.grzegorzkartasiewicz.app.SocialGraphPort;
import com.grzegorzkartasiewicz.domain.ConversationRepository;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MessengerConfiguration {

  @Bean
  MessengerService messengerService(ConversationRepository conversationRepository,
      SocialGraphPort socialGraphPort, DomainEventPublisher domainEventPublisher) {
    return new MessengerService(conversationRepository, socialGraphPort, domainEventPublisher);
  }
}
