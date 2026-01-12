package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.Blocked;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.InvalidLogInCounter;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.Password;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import com.grzegorzkartasiewicz.domain.vo.Verification;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private AuthorizationPort authorizationPort;
  @Mock
  private DomainEventPublisher domainEventPublisher;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  void setUp() {
    Name name = new Name("John", "Doe");
    Email testEmail = new Email("john.doe@example.com");
    Password password = new Password("Password123!");
    testUser = new User(new UserId(UUID.randomUUID()), name, testEmail, password,
        Verification.UNVERIFIED,
        new InvalidLogInCounter(0), Blocked.NOT_BLOCKED);
  }

  @Test
  @DisplayName("signIn should save user and return registered user with token")
  void signUp_shouldSaveUserAndReturnRegisteredUserWithToken() {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest("John", "Doe",
        "john.doe@example.com", "Password123!");
    Token token = new Token("dummy-token");
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(authorizationPort.generateToken(any(User.class))).thenReturn(token);
    when(passwordEncoder.encode("Password123!")).thenReturn("Password123!");

    // when
    RegisteredUser registeredUser = userService.signUp(request);

    // then
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getName().name()).isEqualTo(request.firstName());
    assertThat(savedUser.getName().surname()).isEqualTo(request.lastName());
    assertThat(savedUser.getEmail().email()).isEqualTo(request.email());

    verify(authorizationPort).generateToken(any(User.class));

    assertThat(registeredUser).isNotNull();
    assertThat(registeredUser.firstName()).isEqualTo(request.firstName());
    assertThat(registeredUser.email()).isEqualTo(request.email());
    assertThat(registeredUser.token()).isEqualTo(token.token());
  }

  @Test
  @DisplayName("signIn should throw UserAlreadyExistsException when user already exists")
  void signUp_shouldThrowExceptionWhenUserExists() {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest("John", "Doe",
        "john.doe@example.com", "Password123!");
    when(userRepository.findUserByEmail(new Email(request.email()))).thenReturn(
        Optional.of(testUser));

    // when & then
    assertThrows(UserAlreadyExistsException.class, () -> userService.signUp(request));
  }

  @Test
  @DisplayName("signIn should throw InvalidUserDataException for invalid data")
  void signUp_shouldThrowExceptionForInvalidData() {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest(null, "Doe",
        "john.doe@example.com", "Password123!");

    // when & then
    assertThrows(InvalidUserDataException.class, () -> userService.signUp(request));
  }


  @Test
  @DisplayName("logIn should return logged user when credentials are correct")
  void logIn_shouldReturnLoggedUserWhenCredentialsAreCorrect() {
    // given
    UserLogInRequest request = new UserLogInRequest(testUser.getEmail().email(),
        testUser.getPassword().password());
    Token token = new Token("dummy-token");

    when(userRepository.findUserByEmail(new Email(request.email()))).thenReturn(
        Optional.of(testUser));
    when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
    when(authorizationPort.generateToken(testUser)).thenReturn(token);

    // when
    LoggedUser loggedUser = userService.logIn(request);

    // then
    assertThat(loggedUser).isNotNull();
    assertThat(loggedUser.firstName()).isEqualTo(testUser.getName().name());
    assertThat(loggedUser.email()).isEqualTo(testUser.getEmail().email());
    assertThat(loggedUser.token()).isEqualTo(token.token());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("logIn should throw InvalidCredentialsException when user not found")
  void logIn_shouldThrowExceptionWhenUserNotFound() {
    // given
    UserLogInRequest request = new UserLogInRequest("nonexistent@example.com",
        "password");
    when(userRepository.findUserByEmail(new Email(request.email()))).thenReturn(Optional.empty());

    // when & then
    assertThrows(InvalidCredentialsException.class, () -> userService.logIn(request));
  }

  @Test
  @DisplayName("logIn should throw exception when user is blocked")
  void logIn_shouldThrowExceptionWhenUserIsBlocked() {
    // given
    for (int i = 0; i < 6; i++) {
      testUser.recordFailedLoginAttempt();
    }

    UserLogInRequest request = new UserLogInRequest(testUser.getEmail().email(),
        testUser.getPassword().password());
    when(userRepository.findUserByEmail(new Email(request.email()))).thenReturn(
        Optional.of(testUser));

    // when & then
    UserBlockedException exception = assertThrows(UserBlockedException.class,
        () -> userService.logIn(request));
    assertThat(exception.getMessage()).isEqualTo("User is blocked");
  }

  @Test
  @DisplayName("logIn should increase counter and throw exception when password is incorrect")
  void logIn_shouldIncreaseCounterAndThrowExceptionWhenPasswordIsIncorrect() {
    // given
    UserLogInRequest request = new UserLogInRequest(testUser.getEmail().email(), "wrong-password");
    when(userRepository.findUserByEmail(new Email(request.email()))).thenReturn(
        Optional.of(testUser));

    // when & then
    assertThrows(InvalidCredentialsException.class, () -> userService.logIn(request));

    verify(userRepository).save(testUser);
    assertThat(testUser.getInvalidLogInCounter().counter()).isEqualTo(1);
  }

  @Test
  @DisplayName("requestResetPassword should find user by email and publish event")
  void requestResetPassword_shouldFindUserByEmailAndPublishEvent() {
    // given
    ResetPasswordRequest request = new ResetPasswordRequest(testUser.getEmail().email(),
        "newPassword123!");
    when(userRepository.findUserByEmail(new Email(request.email()))).thenReturn(
        Optional.of(testUser));

    // when
    userService.requestResetPassword(request);

    // then
    verify(userRepository).findUserByEmail(new Email(request.email()));
    verify(domainEventPublisher).publish(any(ResetPasswordEvent.class));
  }

  @Test
  @DisplayName("requestResetPassword should throw InvalidCredentialsException when user not found")
  void requestResetPassword_shouldThrowExceptionWhenUserNotFound() {
    // given
    ResetPasswordRequest request = new ResetPasswordRequest("nonexistent@example.com",
        "newPassword123!");
    when(userRepository.findUserByEmail(new Email(request.email()))).thenReturn(Optional.empty());

    // when & then
    assertThrows(InvalidCredentialsException.class,
        () -> userService.requestResetPassword(request));
  }


  @Test
  @DisplayName("resetPassword should update password and save user")
  void resetPassword_shouldUpdatePasswordAndSaveUser() {
    // given
    UserId userId = testUser.getId();
    String newPassword = "newValidPassword123!";
    when(userRepository.findUserById(userId)).thenReturn(Optional.of(testUser));
    when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

    // when
    userService.resetPassword(userId, newPassword);

    // then
    verify(userRepository).findUserById(userId);
    verify(userRepository).save(testUser);
    assertThat(testUser.getPassword().password()).isEqualTo(newPassword);
  }

  @Test
  @DisplayName("resetPassword should throw InvalidCredentialsException when user not found")
  void resetPassword_shouldThrowExceptionWhenUserNotFound() {
    // given
    UserId userId = new UserId(UUID.randomUUID());
    String newPassword = "newValidPassword123!";
    when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(InvalidCredentialsException.class,
        () -> userService.resetPassword(userId, newPassword));
  }


  @Test
  @DisplayName("verifyUser should verify and save user when verification is VERIFIED")
  void verifyUser_shouldVerifyAndSaveUserWhenVerificationIsVerified() {
    // given
    UserId userId = testUser.getId();
    when(userRepository.findUserById(userId)).thenReturn(Optional.of(testUser));

    // when
    userService.verifyUser(userId, Verification.VERIFIED);

    // then
    verify(userRepository).findUserById(userId);
    verify(userRepository).save(testUser);
    assertThat(testUser.getVerification()).isEqualTo(Verification.VERIFIED);
  }

  @Test
  @DisplayName("verifyUser should delete user when verification is UNVERIFIED")
  void verifyUser_shouldDeleteUserWhenVerificationIsUnverified() {
    // given
    UserId userId = testUser.getId();
    when(userRepository.findUserById(userId)).thenReturn(Optional.of(testUser));

    // when
    userService.verifyUser(userId, Verification.UNVERIFIED);

    // then
    verify(userRepository).findUserById(userId);
    verify(userRepository).delete(testUser);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("verifyUser should throw InvalidCredentialsException when user not found")
  void verifyUser_shouldThrowExceptionWhenUserNotFound() {
    // given
    UserId userId = new UserId(UUID.randomUUID());
    when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(InvalidCredentialsException.class,
        () -> userService.verifyUser(userId, Verification.VERIFIED));
  }
}
