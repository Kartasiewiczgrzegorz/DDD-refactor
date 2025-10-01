package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.ResetPasswordEvent;
import com.grzegorzkartasiewicz.app.UserEmailVerificationEvent;
import com.grzegorzkartasiewicz.app.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserEventListener {

  private final UserService userService;

  @EventListener
  public void verifyUser(UserEmailVerificationEvent userEmailVerificationEvent) {
    userService.verifyUser(userEmailVerificationEvent.verifiedUserId(),
        userEmailVerificationEvent.verification());
  }

  @EventListener
  public void resetPassword(ResetPasswordEvent resetPasswordEvent) {
    userService.resetPassword(resetPasswordEvent.userId(), resetPasswordEvent.newPassword());
  }

}
