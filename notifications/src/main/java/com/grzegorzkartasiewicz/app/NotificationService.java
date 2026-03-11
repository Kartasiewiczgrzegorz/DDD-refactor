package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Notification;
import com.grzegorzkartasiewicz.domain.NotificationRepository;
import com.grzegorzkartasiewicz.domain.NotificationSettings;
import com.grzegorzkartasiewicz.domain.NotificationSettingsRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final NotificationSender notificationSender;
  private final DomainEventPublisher domainEventPublisher;

  public void triggerNotification(TriggerNotificationCommand command) {
    NotificationSettings notificationSettings = notificationSettingsRepository.findByUserId(
        command.userId()).orElseThrow();
    if (notificationSettings.canSend(command.type(), command.channel())) {
      Notification notification = Notification.create(command.userId(), command.type(),
          command.channel(), command.params());
      notificationRepository.save(notification);
      try {
        notificationSender.send(notification);
      } catch (Exception e) {
        notification.markAsFailed();
        notificationRepository.save(notification);
        return;
      }
      notification.markAsSent();
      notificationRepository.save(notification);
      domainEventPublisher.publish(new NotificationSent());
    }
  }

  public void updatePreference(UpdatePreferenceCommand command) {
    NotificationSettings notificationSettings = notificationSettingsRepository.findByUserId(
        command.userId()).orElse(NotificationSettings.createDefault(command.userId()));

    notificationSettings.updatePreference(command.type(), command.channel(), command.active());

    notificationSettingsRepository.save(notificationSettings);
  }

  public void markAsRead(UUID notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException("Notification not found."));

    notification.markAsRead();

    notificationRepository.save(notification);
  }
}
