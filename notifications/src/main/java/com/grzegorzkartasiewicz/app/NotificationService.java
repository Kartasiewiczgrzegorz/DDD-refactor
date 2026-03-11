package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Notification;
import com.grzegorzkartasiewicz.domain.NotificationRepository;
import com.grzegorzkartasiewicz.domain.NotificationSent;
import com.grzegorzkartasiewicz.domain.NotificationSettings;
import com.grzegorzkartasiewicz.domain.NotificationSettingsRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Application service for managing notifications and user preferences.
 */
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationSettingsRepository settingsRepository;
  private final NotificationSender notificationSender;
  private final DomainEventPublisher eventPublisher;

  /**
   * Triggers a notification sending process if allowed by user preferences.
   */
  public void triggerNotification(TriggerNotificationCommand command) {
    NotificationSettings settings = getOrCreateSettings(command.userId());

    if (settings.canSend(command.type(), command.channel())) {
      processNotification(command);
    } else {
      log.info("Notification skipped due to user settings: user={}, type={}, channel={}",
          command.userId(), command.type(), command.channel());
    }
  }

  /**
   * Updates user notification preferences.
   */
  public void updatePreference(UpdatePreferenceCommand command) {
    NotificationSettings settings = getOrCreateSettings(command.userId());
    settings.updatePreference(command.type(), command.channel(), command.active());
    settingsRepository.save(settings);
  }

  /**
   * Marks a specific notification as read.
   */
  public void markAsRead(UUID notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(
            String.format("Notification %s not found", notificationId)));

    notification.markAsRead();
    notificationRepository.save(notification);
  }

  private NotificationSettings getOrCreateSettings(UUID userId) {
    return settingsRepository.findByUserId(userId)
        .orElseGet(() -> NotificationSettings.createDefault(userId));
  }

  private void processNotification(TriggerNotificationCommand command) {
    Notification notification = Notification.create(
        command.userId(), command.type(), command.channel(), command.params());

    notificationRepository.save(notification);

    try {
      notificationSender.send(notification);
      notification.markAsSent();
      eventPublisher.publish(new NotificationSent(notification.getId()));
    } catch (ExternalSenderException e) {
      log.error("Failed to send notification {}: {}", notification.getId().id(), e.getMessage());
      notification.markAsFailed();
    } finally {
      notificationRepository.save(notification);
    }
  }
}
