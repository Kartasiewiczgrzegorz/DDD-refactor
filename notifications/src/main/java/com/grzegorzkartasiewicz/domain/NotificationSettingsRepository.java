package com.grzegorzkartasiewicz.domain;

import java.util.Optional;
import java.util.UUID;

public interface NotificationSettingsRepository {

  Optional<NotificationSettings> findByUserId(UUID userId);

  void save(NotificationSettings notificationSettings);
}
