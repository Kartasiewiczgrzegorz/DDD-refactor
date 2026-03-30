package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.ResetPasswordEvent;
import com.grzegorzkartasiewicz.app.UserEmailVerificationEvent;
import com.grzegorzkartasiewicz.app.UserEmailVerificationNeededEvent;
import com.grzegorzkartasiewicz.app.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class UserEventListener {

  private final UserService userService;
  private final NotficationFacade notficationFacade;

  @EventListener
  void verifyUser(UserEmailVerificationEvent userEmailVerificationEvent) {
    userService.verifyUser(userEmailVerificationEvent.verifiedUserId(),
        userEmailVerificationEvent.verification());
  }

  @EventListener
  void resetPassword(ResetPasswordEvent resetPasswordEvent) {
    userService.resetPassword(resetPasswordEvent.userId(), resetPasswordEvent.newPassword());
  }

  @EventListener
  void sendVerificationEmail(UserEmailVerificationNeededEvent userEmailVerificationNeededEvent) {

  }

}
