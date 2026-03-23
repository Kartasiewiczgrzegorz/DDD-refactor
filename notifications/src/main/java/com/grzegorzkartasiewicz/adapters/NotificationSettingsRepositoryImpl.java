package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.NotificationSettings;
import com.grzegorzkartasiewicz.domain.NotificationSettingsRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class NotificationSettingsRepositoryImpl implements NotificationSettingsRepository {

  private final SqlNotificationSettingsRepository sqlRepository;

  @Override
  public Optional<NotificationSettings> findByUserId(UUID userId) {
    return sqlRepository.findById(userId).map(NotificationSettingsEntity::toDomain);
  }

  @Override
  public void save(NotificationSettings notificationSettings) {
    sqlRepository.save(NotificationSettingsEntity.fromDomain(notificationSettings));
  }
}
