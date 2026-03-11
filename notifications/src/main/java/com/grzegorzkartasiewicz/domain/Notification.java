package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.NotificationId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Aggregate Root representing a single Notification. Manages the state machine of the notification
 * lifecycle.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Notification {

  private final NotificationId id;
  private final UserId recipientId;
  private final NotificationType type;
  private final Channel channel;
  private NotificationStatus status;

  /**
   * Factory method to create a new PENDING notification.
   */
  public static Notification create(UUID recipientId, NotificationType type,
      Channel channel, Map<String, String> params) {
    return new Notification(
        new NotificationId(UUID.randomUUID()),
        new UserId(recipientId),
        type,
        channel,
        NotificationStatus.PENDING
    );
  }

  /**
   * Factory method for rehydrating an existing notification from persistence.
   */
  public static Notification restore(NotificationId id, UserId recipientId,
      NotificationStatus status,
      NotificationType type, Channel channel) {
    return new Notification(id, recipientId, type, channel, status);
  }

  public void markAsSent() {
    ensureStatusIs(NotificationStatus.PENDING, "Cannot mark as SENT");
    this.status = NotificationStatus.SENT;
  }

  public void markAsFailed() {
    ensureStatusIs(NotificationStatus.PENDING, "Cannot mark as FAILED");
    this.status = NotificationStatus.FAILED;
  }

  public void markAsRead() {
    ensureStatusIs(NotificationStatus.SENT, "Cannot mark as READ");
    this.status = NotificationStatus.READ;
  }

  private void ensureStatusIs(NotificationStatus expectedStatus, String actionPrefix) {
    if (this.status != expectedStatus) {
      throw new IllegalStateTransitionException(
          String.format("%s: notification is in %s state, but expected %s",
              actionPrefix, this.status, expectedStatus));
    }
  }
}
