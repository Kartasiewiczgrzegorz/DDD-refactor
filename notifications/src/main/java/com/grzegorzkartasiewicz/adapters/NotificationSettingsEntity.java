package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.NotificationSettings;
import com.grzegorzkartasiewicz.domain.NotificationType;
import com.grzegorzkartasiewicz.domain.vo.NotificationSetting;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class NotificationSettingsEntity {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @ElementCollection
  @CollectionTable(name = "disabled_notification_settings", joinColumns = @JoinColumn(name = "user_id"))
  private Set<DisabledSettingEmbeddable> disabledSettings = new HashSet<>();

  public static NotificationSettingsEntity fromDomain(NotificationSettings settings) {
    Set<DisabledSettingEmbeddable> disabled = settings.getDisabledSettings().stream()
        .map(s -> new DisabledSettingEmbeddable(s.type(), s.channel()))
        .collect(Collectors.toSet());

    return new NotificationSettingsEntity(settings.getUserId().id(), disabled);
  }

  public NotificationSettings toDomain() {
    Set<NotificationSetting> settings = this.disabledSettings.stream()
        .map(ds -> new NotificationSetting(ds.getType(), ds.getChannel()))
        .collect(Collectors.toSet());

    return NotificationSettings.restore(new UserId(this.userId), settings);
  }

  @Embeddable
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor
  public static class DisabledSettingEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;
  }
}
