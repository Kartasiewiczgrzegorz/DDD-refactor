package app;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserEventListener {

  private final UserService userService;

  public void verifyUser(UserEmailVerificationEvent userEmailVerificationEvent) {
    userService.verifyUser(userEmailVerificationEvent.verifiedUserId(), userEmailVerificationEvent.verification());
  }

}
