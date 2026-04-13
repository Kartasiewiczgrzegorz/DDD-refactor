package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Audience;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Notification;
import com.grzegorzkartasiewicz.domain.NotificationRepository;
import com.grzegorzkartasiewicz.domain.NotificationSent;
import com.grzegorzkartasiewicz.domain.NotificationSettings;
import com.grzegorzkartasiewicz.domain.NotificationSettingsRepository;
import java.util.Collections;
import java.util.Set;
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
  private final SocialGraphPort socialGraphPort;

  /**
   * Triggers a notification sending process if allowed by user preferences.
   */
  public void triggerNotification(TriggerNotificationCommand command) {
    Set<UUID> recipients = resolveRecipients(command);

    for (UUID recipientId : recipients) {
      if (recipientId.equals(command.actorId())) {
        continue;
      }

      NotificationSettings settings = getOrCreateSettings(recipientId);

      if (settings.canSend(command.type(), command.channel())) {
        processNotification(recipientId, command);
      } else {
        log.info("Notification skipped due to user settings: user={}, type={}, channel={}",
            recipientId, command.type(), command.channel());
      }
    }
  }

  private Set<UUID> resolveRecipients(TriggerNotificationCommand command) {
    if (command.type().getAudience() == Audience.DIRECT) {
      return command.targetId() != null ? Set.of(command.targetId()) : Collections.emptySet();
    } else if (command.type().getAudience() == Audience.NETWORK) {
      return socialGraphPort.getFriendsAndFollowers(command.actorId());
    }
    return Collections.emptySet();
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

  private void processNotification(UUID recipientId, TriggerNotificationCommand command) {
    Notification notification = Notification.create(
        recipientId, command.type(), command.channel(), command.params());

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
