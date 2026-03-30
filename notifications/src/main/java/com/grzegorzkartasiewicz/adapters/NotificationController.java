package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.NotificationService;
import com.grzegorzkartasiewicz.app.TriggerNotificationCommand;
import com.grzegorzkartasiewicz.app.UpdatePreferenceCommand;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
class NotificationController {

  private final NotificationService notificationService;

  @PostMapping("/trigger")
  public ResponseEntity<Void> triggerNotification(
      @RequestBody @Valid TriggerNotificationRequest request) {
    TriggerNotificationCommand command = new TriggerNotificationCommand(
        request.userId(),
        request.type(),
        request.channel(),
        request.params()
    );
    notificationService.triggerNotification(command);
    return ResponseEntity.accepted().build();
  }

  @PutMapping("/{id}/read")
  public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
    notificationService.markAsRead(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/settings")
  public ResponseEntity<Void> updatePreference(
      @RequestBody @Valid UpdatePreferenceRequest request) {
    UpdatePreferenceCommand command = new UpdatePreferenceCommand(
        request.userId(),
        request.type(),
        request.channel(),
        request.active()
    );
    notificationService.updatePreference(command);
    return ResponseEntity.accepted().build();
  }
}
