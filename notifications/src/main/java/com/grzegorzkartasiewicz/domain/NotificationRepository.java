package com.grzegorzkartasiewicz.domain;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {

  Notification save(Notification notification);

  Optional<Notification> findById(UUID notificationId);
}
