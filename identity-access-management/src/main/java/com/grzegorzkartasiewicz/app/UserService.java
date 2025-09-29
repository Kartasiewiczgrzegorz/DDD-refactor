package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserId;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.Verification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AuthorizationPort authorizationPort;

  public RegisteredUser signIn(UserRegistrationRequest userRegistrationRequest) {
    User signedUser = new User(userRegistrationRequest);
    signedUser = userRepository.save(signedUser);
    //TODO send verification email

    Token token = authorizationPort.generateToken(signedUser);
    return new RegisteredUser(signedUser.getId().id(), signedUser.getName().name(),
        signedUser.getName().surname(),
        signedUser.getEmail().email(), token.token());
  }

  public LoggedUser logIn(UserLogInRequest userLogInRequest) {
    User user = userRepository.findUserByEmail(userLogInRequest.email());
    if (user.isBlocked()) {
      throw new IllegalArgumentException("User is blocked");
    }
    try {
      user.verifyPassword(userLogInRequest.password());
    } catch (Exception e) {
      user.increaseInvalidLogInCounter();
      userRepository.save(user);
      throw e;
    }
    Token token = authorizationPort.generateToken(user);
    return new LoggedUser(user.getName().name(), user.getName().surname(), user.getEmail().email(),
        token.token());
  }

  public void requestResetPassword(ResetPasswordRequest resetPasswordRequest) {
    User user = userRepository.findUserByEmail(resetPasswordRequest.email());

    //TODO send reset password email
  }

  public void resetPassword(UserId userId, String password) {
    User user = userRepository.findUserById(userId);
    user.resetPassword(password);
    userRepository.save(user);
  }

  public void verifyUser(UserId verifiedUserId, Verification verification) {
    User user = userRepository.findUserById(verifiedUserId);
    if (verification.equals(Verification.VERIFIED)) {
      user.verify();
      userRepository.save(user);
    } else if (verification.equals(Verification.UNVERIFIED)) {
      userRepository.delete(user);
    }

  }
}
