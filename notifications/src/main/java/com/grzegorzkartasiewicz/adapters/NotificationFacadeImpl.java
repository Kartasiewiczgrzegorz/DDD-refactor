package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.api.NotificationFacade;
import com.grzegorzkartasiewicz.api.NotificationRequest;
import com.grzegorzkartasiewicz.app.NotificationService;
import com.grzegorzkartasiewicz.app.TriggerNotificationCommand;
import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class NotificationFacadeImpl implements NotificationFacade {

  private final NotificationService notificationService;

  @Override
  public void sendNotification(NotificationRequest request) {
    notificationService.triggerNotification(
        new TriggerNotificationCommand(request.userId(),
            NotificationType.valueOf(request.type()),
            Channel.valueOf(request.channel()), request.params()));
  }
}
