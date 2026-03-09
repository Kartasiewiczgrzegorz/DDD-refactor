package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.NotificationSetting;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotificationSettings {

  private final UserId userId;
  private final Set<NotificationSetting> notificationSetting;

  public static NotificationSettings createDefault(UUID userId) {
    Set<NotificationSetting> notificationSetting = new HashSet<>();
    for (NotificationType type : Arrays.stream(NotificationType.values())
        .filter(notificationType -> !notificationType.critical).toList()) {
      notificationSetting.add(new NotificationSetting(type, Channel.EMAIL));
    }
    return new NotificationSettings(new UserId(userId), notificationSetting);
  }

  public boolean canSend(NotificationType notificationType, Channel channel) {
    List<NotificationType> criticalNotificationTypes = Arrays.stream(NotificationType.values())
        .filter(type -> type.critical).toList();
    return criticalNotificationTypes.contains(notificationType) || notificationSetting.stream()
        .anyMatch(
            notificationSetting1 -> notificationSetting1.type().equals(notificationType)
                && notificationSetting1.channel().equals(channel));
  }

  public void updatePreference(NotificationType notificationType, Channel channel, boolean active) {
    List<NotificationType> criticalNotificationTypes = Arrays.stream(NotificationType.values())
        .filter(type -> type.critical).toList();
    if (criticalNotificationTypes.contains(notificationType)) {
      throw new CriticalNotificationException("Cannot disable critical notification type");
    }
    if (active) {
      notificationSetting.add(new NotificationSetting(notificationType, channel));
    } else {
      notificationSetting.removeIf(
          notificationSetting1 -> notificationSetting1.type() == notificationType
              && notificationSetting1.channel() == channel);
    }
  }
}
