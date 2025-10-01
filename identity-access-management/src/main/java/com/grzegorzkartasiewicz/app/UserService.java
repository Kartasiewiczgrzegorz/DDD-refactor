package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Email;
import com.grzegorzkartasiewicz.domain.PasswordDoesNotMatchException;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserId;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.ValidationException;
import com.grzegorzkartasiewicz.domain.Verification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AuthorizationPort authorizationPort;
  private final DomainEventPublisher publisher;

  public RegisteredUser signIn(UserRegistrationRequest userRegistrationRequest) {
    if (userRepository.findUserByEmail(new Email(userRegistrationRequest.email())).isPresent()) {
      throw new UserAlreadyExistsException("User already exists");
    }
    User signedUser;
    try {
      signedUser = new User(userRegistrationRequest.firstName(), userRegistrationRequest.lastName(),
          userRegistrationRequest.email(), userRegistrationRequest.password());
      signedUser = userRepository.save(signedUser);
    } catch (ValidationException e) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    //TODO send verification email

    Token token = authorizationPort.generateToken(signedUser);
    return new RegisteredUser(signedUser.getId().id(), signedUser.getName().name(),
        signedUser.getName().surname(),
        signedUser.getEmail().email(), token.token());
  }

  public LoggedUser logIn(UserLogInRequest userLogInRequest) {
    User user = userRepository.findUserByEmail(userLogInRequest.email())
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    if (user.isBlocked()) {
      throw new UserBlockedException("User is blocked");
    }
    try {
      user.verifyPassword(userLogInRequest.password());
    } catch (PasswordDoesNotMatchException e) {
      user.increaseInvalidLogInCounter();
      userRepository.save(user);
      throw new InvalidCredentialsException("Invalid credentials");
    }
    Token token = authorizationPort.generateToken(user);
    return new LoggedUser(user.getName().name(), user.getName().surname(), user.getEmail().email(),
        token.token());
  }

  public void requestResetPassword(ResetPasswordRequest resetPasswordRequest) {
    User user = userRepository.findUserByEmail(resetPasswordRequest.email())
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

    //TODO send reset password email
    ResetPasswordEvent resetPasswordEvent = new ResetPasswordEvent(user.getId(),
        resetPasswordRequest.password());
    publisher.publish(resetPasswordEvent);
  }

  public void resetPassword(UserId userId, String password) {
    User user = userRepository.findUserById(userId)
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    user.resetPassword(password);
    userRepository.save(user);
  }

  public void verifyUser(UserId verifiedUserId, Verification verification) {
    User user = userRepository.findUserById(verifiedUserId)
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    if (verification.equals(Verification.VERIFIED)) {
      user.verify();
      userRepository.save(user);
    } else if (verification.equals(Verification.UNVERIFIED)) {
      userRepository.delete(user);
    }

  }
}
