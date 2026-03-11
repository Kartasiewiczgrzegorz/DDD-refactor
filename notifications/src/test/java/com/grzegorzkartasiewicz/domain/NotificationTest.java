package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationTest {

  private final UUID recipientId = UUID.randomUUID();
  private final NotificationType type = NotificationType.FRIEND_REQUEST;
  private final Channel channel = Channel.EMAIL;
  private final Map<String, String> params = Map.of("senderName", "John");

  @Test
  @DisplayName("should create notification with PENDING status")
  void shouldCreateNotificationWithPendingStatus() {
    // when
    Notification notification = Notification.create(recipientId, type, channel, params);

    // then
    Assertions.assertNotNull(notification);
    assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
    assertThat(notification.getRecipientId().id()).isEqualTo(recipientId);
    assertThat(notification.getType()).isEqualTo(type);
    assertThat(notification.getChannel()).isEqualTo(channel);
  }

  @Test
  @DisplayName("should transition from PENDING to SENT")
  void shouldTransitionToSent() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);

    // when
    notification.markAsSent();

    // then
    assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
  }

  @Test
  @DisplayName("should transition from PENDING to FAILED")
  void shouldTransitionToFailed() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);

    // when
    notification.markAsFailed();

    // then
    assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
  }

  @Test
  @DisplayName("should mark SENT notification as READ")
  void shouldMarkAsRead() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);
    notification.markAsSent();

    // when
    notification.markAsRead();

    // then
    assertThat(notification.getStatus()).isEqualTo(NotificationStatus.READ);
  }

  @Test
  @DisplayName("should throw exception when marking PENDING notification as READ")
  void shouldNotMarkPendingAsRead() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);

    // when & then
    assertThatThrownBy(notification::markAsRead)
        .isInstanceOf(IllegalStateTransitionException.class)
        .hasMessageContaining("Cannot mark PENDING notification as READ");
  }

  @Test
  @DisplayName("should throw exception when trying to send notification that is not PENDING")
  void shouldNotSendIfAlreadyProcessed() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);
    notification.markAsSent();

    // when & then
    assertThatThrownBy(notification::markAsSent)
        .isInstanceOf(IllegalStateTransitionException.class);
  }

  @Test
  @DisplayName("should throw exception when trying to mark as FAILED if already SENT")
  void shouldNotFailIfAlreadySent() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);
    notification.markAsSent();

    // when & then
    assertThatThrownBy(notification::markAsFailed)
        .isInstanceOf(IllegalStateTransitionException.class);
  }

  @Test
  @DisplayName("should throw exception when trying to mark as SENT if already FAILED")
  void shouldNotSendIfFailed() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);
    notification.markAsFailed();

    // when & then
    assertThatThrownBy(notification::markAsSent)
        .isInstanceOf(IllegalStateTransitionException.class);
  }

  @Test
  @DisplayName("should throw exception when trying to mark as SENT if already READ")
  void shouldNotSendIfRead() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);
    notification.markAsSent();
    notification.markAsRead();

    // when & then
    assertThatThrownBy(notification::markAsSent)
        .isInstanceOf(IllegalStateTransitionException.class);
  }

  @Test
  @DisplayName("should throw exception when trying to mark as READ if FAILED")
  void shouldNotReadIfFailed() {
    // given
    Notification notification = Notification.create(recipientId, type, channel, params);
    notification.markAsFailed();

    // when & then
    assertThatThrownBy(notification::markAsRead)
        .isInstanceOf(IllegalStateTransitionException.class);
  }
}
