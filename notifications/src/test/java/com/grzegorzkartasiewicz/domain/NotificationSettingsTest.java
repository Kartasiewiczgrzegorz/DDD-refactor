package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationSettingsTest {

  private final UUID userId = UUID.randomUUID();
  private final Channel channel = Channel.EMAIL;

  @Test
  @DisplayName("should allow sending by default (Opt-Out approach)")
  void shouldAllowSendingByDefault() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);

    // when
    boolean canSend = settings.canSend(NotificationType.FRIEND_REQUEST, channel);

    // then
    assertThat(canSend).isTrue();
  }

  @Test
  @DisplayName("should allow sending critical notifications regardless of preferences")
  void shouldAlwaysAllowCriticalNotifications() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);

    // when
    boolean canSend = settings.canSend(NotificationType.PASSWORD_RESET, channel);

    // then
    assertThat(canSend).isTrue();
  }

  @Test
  @DisplayName("should disable notification when user explicitly opts out")
  void shouldDisableNotificationOnOptOut() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);

    // when
    settings.updatePreference(NotificationType.FRIEND_REQUEST, channel, false);

    // then
    assertThat(settings.canSend(NotificationType.FRIEND_REQUEST, channel)).isFalse();
  }

  @Test
  @DisplayName("should re-enable notification when user opts back in")
  void shouldReEnableNotification() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);
    settings.updatePreference(NotificationType.FRIEND_REQUEST, channel, false);

    // when
    settings.updatePreference(NotificationType.FRIEND_REQUEST, channel, true);

    // then
    assertThat(settings.canSend(NotificationType.FRIEND_REQUEST, channel)).isTrue();
  }

  @Test
  @DisplayName("should throw exception when trying to disable critical notification type")
  void shouldThrowExceptionOnDisablingCriticalType() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);

    // when & then
    assertThatThrownBy(() ->
        settings.updatePreference(NotificationType.PASSWORD_RESET, channel, false)
    )
        .isInstanceOf(CriticalNotificationException.class)
        .hasMessageContaining("Cannot disable critical notification type");
  }

  @Test
  @DisplayName("should allow updating preference for non-critical type even if it matches default")
  void shouldAllowSettingExplicitTrueForNonCritical() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);

    // when & then
    settings.updatePreference(NotificationType.FRIEND_REQUEST, channel, true);
    assertThat(settings.canSend(NotificationType.FRIEND_REQUEST, channel)).isTrue();
  }
}
