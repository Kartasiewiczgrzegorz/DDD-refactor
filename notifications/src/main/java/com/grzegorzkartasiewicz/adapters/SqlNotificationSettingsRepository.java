package com.grzegorzkartasiewicz.adapters;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SqlNotificationSettingsRepository extends
    JpaRepository<NotificationSettingsEntity, UUID> {

}
