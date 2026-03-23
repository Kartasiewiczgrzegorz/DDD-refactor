package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.Notification;
import com.grzegorzkartasiewicz.domain.NotificationStatus;
import com.grzegorzkartasiewicz.domain.NotificationType;
import com.grzegorzkartasiewicz.domain.vo.NotificationId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class NotificationEntity {

  @Id
  private UUID id;

  @Column(name = "recipient_id", nullable = false)
  private UUID recipientId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Channel channel;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationStatus status;

  @ElementCollection
  @CollectionTable(name = "notification_params", joinColumns = @JoinColumn(name = "notification_id"))
  @MapKeyColumn(name = "param_key")
  @Column(name = "param_value")
  private Map<String, String> params = new HashMap<>();

  public static NotificationEntity fromDomain(Notification notification) {
    return new NotificationEntity(
        notification.getId().id(),
        notification.getRecipientId().id(),
        notification.getType(),
        notification.getChannel(),
        notification.getStatus(),
        notification.getParams() != null ? new HashMap<>(notification.getParams()) : new HashMap<>()
    );
  }

  public Notification toDomain() {
    return Notification.restore(
        new NotificationId(this.id),
        new UserId(this.recipientId),
        this.status,
        this.type,
        this.channel,
        this.params
    );
  }
}
