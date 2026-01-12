package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.PasswordDoesNotMatchException;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.ValidationException;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.RawPassword;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import com.grzegorzkartasiewicz.domain.vo.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application service responsible for handling user-related use cases. It acts as an orchestrator,
 * coordinating logic between the domain and adapters.
 */
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AuthorizationPort authorizationPort;
  private final DomainEventPublisher publisher;
  private final PasswordEncoder passwordEncoder;

  /**
   * Registers a new user in the system.
   *
   * @param userRegistrationRequest The registration data from the user.
   * @return A {@link RegisteredUser} object containing the new user's data and a JWT.
   * @throws UserAlreadyExistsException if a user with the given email already exists.
   * @throws InvalidUserDataException   if the provided data (e.g., password) is invalid.
   */
  public RegisteredUser signUp(UserRegistrationRequest userRegistrationRequest) {
    try {
      validateIfUserExists(userRegistrationRequest.email());
      User signedUser = createUser(userRegistrationRequest);
      //TODO send verification email

      Token token = authorizationPort.generateToken(signedUser);
      return mapUserToRegisterResponse(signedUser, token);
    } catch (ValidationException e) {
      throw new InvalidUserDataException(e.getMessage());
    }
  }

  /**
   * Authenticates a user and generates an access token.
   *
   * @param userLogInRequest The user's login data.
   * @return A {@link LoggedUser} object containing the logged-in user's data and a JWT.
   * @throws InvalidCredentialsException if the email or password is incorrect.
   * @throws UserBlockedException        if the user account is blocked.
   * @throws InvalidUserDataException    if input data format is invalid.
   */
  public LoggedUser logIn(UserLogInRequest userLogInRequest) {
    try {
      User user = findUserByEmailOrThrow(userLogInRequest.email());
      validateIfUserIsBlocked(user);
      validateCredentialsAndHandleFailedAttempt(userLogInRequest, user);
      Token token = authorizationPort.generateToken(user);
      return new LoggedUser(user.getName().name(), user.getName().surname(),
          user.getEmail().email(),
          token.token());
    } catch (ValidationException e) {
      throw new InvalidUserDataException(e.getMessage());
    }
  }

  /**
   * Initiates the password reset process for a user. This method finds the user by email and
   * publishes a {@link ResetPasswordEvent}. The actual sending of the email is handled by an event
   * listener.
   *
   * @param resetPasswordRequest The request object containing the user's email.
   * @throws InvalidCredentialsException if a user with the given email is not found.
   * @throws InvalidUserDataException    if input data format is invalid.
   */
  public void requestResetPassword(ResetPasswordRequest resetPasswordRequest) {
    try {
      User user = findUserByEmailOrThrow(resetPasswordRequest.email());

      //TODO send reset password email
      ResetPasswordEvent resetPasswordEvent = new ResetPasswordEvent(user.getId(),
          resetPasswordRequest.password());
      publisher.publish(resetPasswordEvent);
    } catch (ValidationException e) {
      throw new InvalidUserDataException(e.getMessage());
    }
  }

  /**
   * Resets the password for a user identified by their UserId. This method should be called after a
   * user has confirmed the reset request (e.g., by clicking a link in an email).
   *
   * @param userId   The unique identifier of the user.
   * @param password The new, raw password to be set and hashed.
   * @throws InvalidCredentialsException if a user with the given ID is not found.
   * @throws InvalidUserDataException    if the new password does not meet the required validation
   *                                     criteria.
   */
  public void resetPassword(UserId userId, String password) {
    try {
      User user = findUserByIdOrThrow(userId);
      user.resetPassword(new RawPassword(password), passwordEncoder);
      userRepository.save(user);
    } catch (ValidationException e) {
      throw new InvalidUserDataException(e.getMessage());
    }
  }

  /**
   * Verifies a user's account or handles an unverified account.
   *
   * @param verifiedUserId The ID of the user to verify.
   * @param verification   The desired verification status.
   * @throws InvalidCredentialsException if the user with the given ID is not found.
   */
  public void verifyUser(UserId verifiedUserId, Verification verification) {
    User user = findUserByIdOrThrow(verifiedUserId);
    if (verification.equals(Verification.VERIFIED)) {
      user.verify();
      userRepository.save(user);
    } else if (verification.equals(Verification.UNVERIFIED)) {
      userRepository.delete(user);
    }
  }

  private void validateCredentialsAndHandleFailedAttempt(UserLogInRequest userLogInRequest,
      User user) {
    try {
      user.verifyPassword(userLogInRequest.password(), passwordEncoder);
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

  private User createUser(UserRegistrationRequest userRegistrationRequest) {
    User signedUser = mapRequestToUser(userRegistrationRequest);
    return userRepository.save(signedUser);
  }

  private static RegisteredUser mapUserToRegisterResponse(User signedUser, Token token) {
    return new RegisteredUser(signedUser.getId().id(), signedUser.getName().name(),
        signedUser.getName().surname(),
        signedUser.getEmail().email(), token.token());
  }

  private User mapRequestToUser(UserRegistrationRequest userRegistrationRequest) {
    Name name = new Name(userRegistrationRequest.firstName(), userRegistrationRequest.lastName());
    Email email = new Email(userRegistrationRequest.email());
    RawPassword password = new RawPassword(userRegistrationRequest.password());
    return User.createNew(name, email, password, passwordEncoder);
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
