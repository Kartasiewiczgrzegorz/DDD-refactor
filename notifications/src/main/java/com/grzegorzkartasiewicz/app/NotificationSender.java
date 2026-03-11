package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Notification;

public interface NotificationSender {

  void send(Notification notification);
}
