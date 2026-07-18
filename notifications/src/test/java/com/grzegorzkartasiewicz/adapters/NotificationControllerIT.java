package com.grzegorzkartasiewicz.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grzegorzkartasiewicz.app.NotificationService;
import com.grzegorzkartasiewicz.app.TriggerNotificationCommand;
import com.grzegorzkartasiewicz.app.UpdatePreferenceCommand;
import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.NotificationType;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
class NotificationControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private NotificationService notificationService;

  @Test
  @DisplayName("Should trigger notification via REST")
  void shouldTriggerNotification() throws Exception {
    UUID userId = UUID.randomUUID();
    TriggerNotificationRequest request = new TriggerNotificationRequest(
        userId, userId, NotificationType.FRIEND_REQUEST, Channel.EMAIL, Map.of("key", "value")
    );

    mockMvc.perform(post("/api/notifications/trigger")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(notificationService).triggerNotification(any(TriggerNotificationCommand.class));
  }

  @Test
  @DisplayName("Should update preference via REST")
  void shouldUpdatePreference() throws Exception {
    UUID userId = UUID.randomUUID();
    UpdatePreferenceRequest request = new UpdatePreferenceRequest(
        userId, NotificationType.FRIEND_REQUEST, Channel.EMAIL, false
    );

    mockMvc.perform(put("/api/notifications/settings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(notificationService).updatePreference(any(UpdatePreferenceCommand.class));
  }

  @Test
  @DisplayName("Should mark notification as read via REST")
  void shouldMarkAsRead() throws Exception {
    UUID notificationId = UUID.randomUUID();

    mockMvc.perform(put("/api/notifications/{id}/read", notificationId))
        .andExpect(status().isOk());

    verify(notificationService).markAsRead(notificationId);
  }
}
