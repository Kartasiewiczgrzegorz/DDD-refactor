package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.api.NotificationFacade;
import com.grzegorzkartasiewicz.api.NotificationRequest;
import com.grzegorzkartasiewicz.app.ResetPasswordEvent;
import com.grzegorzkartasiewicz.app.UserEmailVerificationNeededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class UserEventListener {

  private final NotificationFacade notificationFacade;

  @EventListener
  void resetPassword(ResetPasswordEvent resetPasswordEvent) {
    notificationFacade.sendNotification(
        new NotificationRequest(resetPasswordEvent.userId().id(),
            "PASSWORD_RESET", "EMAIL", null));
  }

  @EventListener
  void sendVerificationEmail(UserEmailVerificationNeededEvent userEmailVerificationNeededEvent) {
    notificationFacade.sendNotification(
        new NotificationRequest(userEmailVerificationNeededEvent.id().id(),
            "EMAIL_VERIFICATION", "EMAIL", null));
  }
}
