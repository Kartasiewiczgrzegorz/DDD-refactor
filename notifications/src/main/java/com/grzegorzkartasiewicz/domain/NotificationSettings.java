package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.NotificationSetting;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotificationSettings {

  private final UserId userId;
  private final Set<NotificationSetting> disabledSettings;

  public static NotificationSettings createDefault(UUID userId) {
    Set<NotificationSetting> notificationSetting = new HashSet<>();
    return new NotificationSettings(new UserId(userId), notificationSetting);
  }

  public boolean canSend(NotificationType notificationType, Channel channel) {
    if (notificationType.isCritical()) {
      return true;
    }
    return !disabledSettings.contains(new NotificationSetting(notificationType, channel));
  }

  public void updatePreference(NotificationType notificationType, Channel channel, boolean active) {
    if (notificationType.isCritical()) {
      throw new CriticalNotificationException("Cannot disable critical notification type");
    }
    if (!active) {
      disabledSettings.add(new NotificationSetting(notificationType, channel));
    } else {
      disabledSettings.removeIf(
          notificationSetting1 -> notificationSetting1.type() == notificationType
              && notificationSetting1.channel() == channel);
    }
  }
}
