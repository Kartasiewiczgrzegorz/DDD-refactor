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
    validateIfUserExists(userRegistrationRequest.email());
    User signedUser;
    signedUser = createUserOrThrowIfValidationFails(userRegistrationRequest);
    //TODO send verification email

    Token token = authorizationPort.generateToken(signedUser);
    return mapUserToRegisterResponse(signedUser, token);
  }

  public LoggedUser logIn(UserLogInRequest userLogInRequest) {
    User user = findUserByEmailOrThrow(userLogInRequest.email());
    validateIfUserIsBlocked(user);
    validateCredentialsAndHandleFailedAttempt(userLogInRequest, user);
    Token token = authorizationPort.generateToken(user);
    return new LoggedUser(user.getName().name(), user.getName().surname(), user.getEmail().email(),
        token.token());
  }

  public void requestResetPassword(ResetPasswordRequest resetPasswordRequest) {
    User user = findUserByEmailOrThrow(resetPasswordRequest.email());

    //TODO send reset password email
    ResetPasswordEvent resetPasswordEvent = new ResetPasswordEvent(user.getId(),
        resetPasswordRequest.password());
    publisher.publish(resetPasswordEvent);
  }

  public void resetPassword(UserId userId, String password) {
    User user = findUserByIdOrThrow(userId);
    user.resetPassword(password);
    userRepository.save(user);
  }

  public void verifyUser(UserId verifiedUserId, Verification verification) {
    User user = findUserByIdOrThrow(verifiedUserId);
    if (verification.equals(Verification.VERIFIED)) {
      user.verify();
      userRepository.save(user);
    } else if (verification.equals(Verification.UNVERIFIED)) {
      userRepository.delete(user);
    }
  }

  private void validateCredentialsAndHandleFailedAttempt(UserLogInRequest userLogInRequest, User user) {
    try {
      user.verifyPassword(userLogInRequest.password());
    } catch (PasswordDoesNotMatchException e) {
      user.recordFailedLoginAttempt();
      userRepository.save(user);
      throw new InvalidCredentialsException("Invalid credentials");
    }
  }

  private static void validateIfUserIsBlocked(User user) {
    if (user.isBlocked()) {
      throw new UserBlockedException("User is blocked");
    }
  }

  private User createUserOrThrowIfValidationFails(UserRegistrationRequest userRegistrationRequest) {
    User signedUser;
    try {
      signedUser = mapRequestToUser(userRegistrationRequest);
      signedUser = userRepository.save(signedUser);
    } catch (ValidationException e) {
      throw new InvalidCredentialsException("Invalid credentials");
    }
    return signedUser;
  }

  private static RegisteredUser mapUserToRegisterResponse(User signedUser, Token token) {
    return new RegisteredUser(signedUser.getId().id(), signedUser.getName().name(),
        signedUser.getName().surname(),
        signedUser.getEmail().email(), token.token());
  }

  private static User mapRequestToUser(UserRegistrationRequest userRegistrationRequest) {
    return User.createNew(userRegistrationRequest.firstName(), userRegistrationRequest.lastName(),
        userRegistrationRequest.email(), userRegistrationRequest.password());
  }

  private User findUserByIdOrThrow(UserId userId) {
    return userRepository.findUserById(userId)
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
  }

  private User findUserByEmailOrThrow(String email) {
    return userRepository.findUserByEmail(new Email(email))
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
  }

  private void validateIfUserExists(String email) {
    if (userRepository.findUserByEmail(new Email(email)).isPresent()) {
      throw new UserAlreadyExistsException("User already exists");
    }
  }
}
