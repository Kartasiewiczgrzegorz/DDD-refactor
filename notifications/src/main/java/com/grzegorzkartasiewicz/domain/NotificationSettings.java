package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.NotificationSetting;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Aggregate Root representing user notification preferences. Implements the Overrides pattern: by
 * default everything is enabled, only explicit opt-outs are stored.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationSettings {

  @Getter
  private final UserId userId;

  /**
   * Set of [NotificationType, Channel] pairs that the user has explicitly disabled.
   */
  private final Set<NotificationSetting> disabledSettings;

  public static NotificationSettings createDefault(UUID userId) {
    return new NotificationSettings(new UserId(userId), new HashSet<>());
  }

  /**
   * Factory method for rehydrating settings from persistence.
   */
  public static NotificationSettings restore(UserId userId,
      Set<NotificationSetting> disabledSettings) {
    return new NotificationSettings(userId, new HashSet<>(disabledSettings));
  }

  public Set<NotificationSetting> getDisabledSettings() {
    return Collections.unmodifiableSet(disabledSettings);
  }

  /**
   * Determines if a notification can be sent based on system policy and user preferences.
   */
  public boolean canSend(NotificationType type, Channel channel) {
    if (type.isCritical()) {
      return true;
    }
    return !isExplicitlyDisabled(type, channel);
  }

  /**
   * Updates a preference for a specific type and channel.
   *
   * @param active true to enable (remove from overrides), false to disable (add to overrides).
   * @throws CriticalNotificationException if the type is critical and cannot be disabled.
   */
  public void updatePreference(NotificationType type, Channel channel, boolean active) {
    if (type.isCritical()) {
      throw new CriticalNotificationException(
          String.format("Cannot disable critical notification type: %s", type));
    }

    if (active) {
      enable(type, channel);
    } else {
      disable(type, channel);
    }
  }

  private boolean isExplicitlyDisabled(NotificationType type, Channel channel) {
    return disabledSettings.contains(new NotificationSetting(type, channel));
  }

  private void enable(NotificationType type, Channel channel) {
    disabledSettings.remove(new NotificationSetting(type, channel));
  }

  private void disable(NotificationType type, Channel channel) {
    disabledSettings.add(new NotificationSetting(type, channel));
  }
}
