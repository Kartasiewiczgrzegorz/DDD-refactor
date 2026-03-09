package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.NotificationId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Notification {

  NotificationId id;
  UserId recipientId;
  NotificationStatus status;
  NotificationType type;
  Channel channel;

  public static Notification create(UUID recipientId, NotificationType type,
      Channel channel, Map<String, String> params) {
    return new Notification(null, new UserId(recipientId), NotificationStatus.PENDING, type,
        channel);
  }

  public void markAsSent() {
    if (this.status != NotificationStatus.PENDING) {
      throw new IllegalStateTransitionException("Cannot sent not PENDING notification");
    }
    this.status = NotificationStatus.SENT;
  }

  public void markAsFailed() {
    if (this.status != NotificationStatus.PENDING) {
      throw new IllegalStateTransitionException("Cannot FAILED sent notification");
    }
    this.status = NotificationStatus.FAILED;
  }

  public void markAsRead() {
    if (this.status != NotificationStatus.SENT) {
      throw new IllegalStateTransitionException("Cannot mark PENDING notification as READ");
    }
    this.status = NotificationStatus.READ;
  }
}
