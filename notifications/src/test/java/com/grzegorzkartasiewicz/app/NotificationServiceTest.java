package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Notification;
import com.grzegorzkartasiewicz.domain.NotificationRepository;
import com.grzegorzkartasiewicz.domain.NotificationSettings;
import com.grzegorzkartasiewicz.domain.NotificationSettingsRepository;
import com.grzegorzkartasiewicz.domain.NotificationStatus;
import com.grzegorzkartasiewicz.domain.NotificationType;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  private final UUID userId = UUID.randomUUID();
  private final UUID notificationId = UUID.randomUUID();
  private final NotificationType type = NotificationType.FRIEND_REQUEST;
  private final Channel channel = Channel.EMAIL;
  private final Map<String, String> params = Map.of("key", "value");
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private NotificationSettingsRepository settingsRepository;
  @Mock
  private NotificationSender notificationSender;
  @Mock
  private DomainEventPublisher domainEventPublisher;
  @InjectMocks
  private NotificationService notificationService;
  @Captor
  private ArgumentCaptor<Notification> notificationCaptor;
  @Captor
  private ArgumentCaptor<NotificationSettings> settingsCaptor;

  @Test
  @DisplayName("triggerNotification should send notification successfully when allowed by settings")
  void triggerNotification_shouldSendNotificationSuccessfullyWhenAllowedBySettings() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);
    when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

    java.util.List<NotificationStatus> capturedStatuses = new java.util.ArrayList<>();
    when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
      Notification n = invocation.getArgument(0);
      capturedStatuses.add(n.getStatus());
      return n;
    });

    TriggerNotificationCommand command = new TriggerNotificationCommand(userId, type, channel,
        params);

    // when
    notificationService.triggerNotification(command);

    // then
    assertThat(capturedStatuses).containsExactly(
        NotificationStatus.PENDING,
        NotificationStatus.SENT
    );

    verify(notificationSender).send(any(Notification.class));
    verify(domainEventPublisher).publish(any(NotificationSent.class));
  }

  @Test
  @DisplayName("triggerNotification should abort sending if user opted out")
  void triggerNotification_shouldAbortWhenUserOptedOut() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);
    settings.updatePreference(type, channel, false); // Opt-out
    when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

    TriggerNotificationCommand command = new TriggerNotificationCommand(userId, type, channel,
        params);

    // when
    notificationService.triggerNotification(command);

    // then
    verify(notificationSender, never()).send(any());
    verify(notificationRepository, never()).save(any());
    verify(domainEventPublisher, never()).publish(any());
  }

  @Test
  @DisplayName("triggerNotification should mark as FAILED when infrastructure adapter throws exception")
  void triggerNotification_shouldMarkAsFailedOnInfrastructureError() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);
    when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

    java.util.List<NotificationStatus> capturedStatuses = new java.util.ArrayList<>();
    when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
      Notification n = invocation.getArgument(0);
      capturedStatuses.add(n.getStatus());
      return n;
    });

    doThrow(new ExternalSenderException("Connection timeout"))
        .when(notificationSender).send(any(Notification.class));

    TriggerNotificationCommand command = new TriggerNotificationCommand(userId, type, channel,
        params);

    // when
    notificationService.triggerNotification(command);

    // then
    assertThat(capturedStatuses).containsExactly(
        NotificationStatus.PENDING,
        NotificationStatus.FAILED
    );
    verify(domainEventPublisher, never()).publish(any(NotificationSent.class));
  }

  @Test
  @DisplayName("updatePreference should update notification preference and save settings")
  void updatePreference_shouldUpdateNotificationPreferenceAndSaveSettings() {
    // given
    NotificationSettings settings = NotificationSettings.createDefault(userId);
    when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

    UpdatePreferenceCommand command = new UpdatePreferenceCommand(userId, type, channel, false);

    // when
    notificationService.updatePreference(command);

    // then
    verify(settingsRepository).save(settingsCaptor.capture());
    NotificationSettings savedSettings = settingsCaptor.getValue();
    assertThat(savedSettings.canSend(type, channel)).isFalse();
  }

  @Test
  @DisplayName("updatePreference should create default settings if none exists when updating preference")
  void updatePreference_shouldCreateDefaultSettingsIfNoneExists() {
    // given
    when(settingsRepository.findByUserId(userId)).thenReturn(Optional.empty());

    UpdatePreferenceCommand command = new UpdatePreferenceCommand(userId, type, channel, false);

    // when
    notificationService.updatePreference(command);

    // then
    verify(settingsRepository).save(settingsCaptor.capture());
    NotificationSettings savedSettings = settingsCaptor.getValue();
    assertThat(savedSettings.getUserId().id()).isEqualTo(userId);
    assertThat(savedSettings.canSend(type, channel)).isFalse();
  }

  @Test
  @DisplayName("markAsRead should mark existing notification as READ")
  void markAsRead_shouldMarkNotificationAsRead() {
    // given
    Notification notification = Notification.create(userId, type, channel, params);
    notification.markAsSent();
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

    // when
    notificationService.markAsRead(notificationId);

    // then
    verify(notificationRepository).save(notificationCaptor.capture());
    assertThat(notificationCaptor.getValue().getStatus()).isEqualTo(NotificationStatus.READ);
  }

  @Test
  @DisplayName("markAsRead should throw not found exception when notification does not exist")
  void markAsRead_shouldThrowNotFoundExceptionWhenNotificationDoesNotExist() {
    // given
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(NotificationNotFoundException.class,
        () -> notificationService.markAsRead(notificationId));
  }
}
