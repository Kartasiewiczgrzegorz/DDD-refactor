package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Notification;
import com.grzegorzkartasiewicz.domain.NotificationRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class NotificationRepositoryImpl implements NotificationRepository {

  private final SqlNotificationRepository sqlRepository;

  @Override
  public Notification save(Notification notification) {
    NotificationEntity entity = sqlRepository.save(NotificationEntity.fromDomain(notification));
    return entity.toDomain();
  }

  @Override
  public Optional<Notification> findById(UUID id) {
    return sqlRepository.findById(id).map(NotificationEntity::toDomain);
  }
}
