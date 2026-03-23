package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.NotificationSender;
import com.grzegorzkartasiewicz.app.NotificationService;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.NotificationRepository;
import com.grzegorzkartasiewicz.domain.NotificationSettingsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class NotificationsConfiguration {

  @Bean
  NotificationService notificationService(
      NotificationRepository notificationRepository,
      NotificationSettingsRepository notificationSettingsRepository,
      NotificationSender notificationSender,
      DomainEventPublisher domainEventPublisher) {
    return new NotificationService(
        notificationRepository,
        notificationSettingsRepository,
        notificationSender,
        domainEventPublisher
    );
  }
}
