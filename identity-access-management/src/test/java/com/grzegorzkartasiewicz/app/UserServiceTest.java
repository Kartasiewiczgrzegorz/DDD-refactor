package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.app.AuthorizationPort;
import com.grzegorzkartasiewicz.app.LoggedUser;
import com.grzegorzkartasiewicz.app.RegisteredUser;
import com.grzegorzkartasiewicz.app.ResetPasswordRequest;
import com.grzegorzkartasiewicz.app.Token;
import com.grzegorzkartasiewicz.app.UserLogInRequest;
import com.grzegorzkartasiewicz.app.UserRegistrationRequest;
import com.grzegorzkartasiewicz.app.UserService;
import com.grzegorzkartasiewicz.domain.Blocked;
import com.grzegorzkartasiewicz.domain.Email;
import com.grzegorzkartasiewicz.domain.InvalidLogInCounter;
import com.grzegorzkartasiewicz.domain.Name;
import com.grzegorzkartasiewicz.domain.Password;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserId;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.Verification;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private AuthorizationPort authorizationPort;
  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  void setUp() {
    Name name = new Name("John", "Doe");
    Email email = new Email("john.doe@example.com");
    Password password = new Password("Password123!");
    testUser = new User(new UserId(UUID.randomUUID()), name, email, password,
        Verification.UNVERIFIED,
        new InvalidLogInCounter(0), Blocked.NOT_BLOCKED);
  }

  @Test
  @DisplayName("signIn should save user and return registered user with token")
  void signIn_shouldSaveUserAndReturnRegisteredUserWithToken() {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest("John", "Doe",
        "john.doe@example.com", "Password123!");
    Token token = new Token("dummy-token");
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(authorizationPort.generateToken(any(User.class))).thenReturn(token);

    // when
    RegisteredUser registeredUser = userService.signIn(request);

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
  @DisplayName("logIn should return logged user when credentials are correct")
  void logIn_shouldReturnLoggedUserWhenCredentialsAreCorrect() {
    // given
    UserLogInRequest request = new UserLogInRequest(testUser.getEmail(),
        testUser.getPassword().password());
    Token token = new Token("dummy-token");

    when(userRepository.findUserByEmail(request.email())).thenReturn(testUser);
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
  @DisplayName("logIn should throw exception when user is blocked")
  void logIn_shouldThrowExceptionWhenUserIsBlocked() {
    // given
    testUser.increaseInvalidLogInCounter(); // Simulate multiple failed logins
    testUser.increaseInvalidLogInCounter();
    testUser.increaseInvalidLogInCounter();
    testUser.increaseInvalidLogInCounter();
    testUser.increaseInvalidLogInCounter();
    testUser.increaseInvalidLogInCounter(); // This will block the user

    UserLogInRequest request = new UserLogInRequest(testUser.getEmail(),
        testUser.getPassword().password());
    when(userRepository.findUserByEmail(request.email())).thenReturn(testUser);

    // when & then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.logIn(request));
    assertThat(exception.getMessage()).isEqualTo("User is blocked");
  }

  @Test
  @DisplayName("logIn should increase counter and throw exception when password is incorrect")
  void logIn_shouldIncreaseCounterAndThrowExceptionWhenPasswordIsIncorrect() {
    // given
    UserLogInRequest request = new UserLogInRequest(testUser.getEmail(), "wrong-password");
    when(userRepository.findUserByEmail(request.email())).thenReturn(testUser);

    // when & then
    assertThrows(IllegalArgumentException.class, () -> userService.logIn(request));

    verify(userRepository).save(testUser);
    assertThat(testUser.getInvalidLogInCounter().counter()).isEqualTo(1);
  }

  @Test
  @DisplayName("requestResetPassword should find user by email")
  void requestResetPassword_shouldFindUserByEmail() {
    // given
    ResetPasswordRequest request = new ResetPasswordRequest(testUser.getEmail(), "newPassword123!");
    when(userRepository.findUserByEmail(request.email())).thenReturn(testUser);

    // when
    userService.requestResetPassword(request);

    // then
    verify(userRepository).findUserByEmail(request.email());
  }

  @Test
  @DisplayName("resetPassword should update password and save user")
  void resetPassword_shouldUpdatePasswordAndSaveUser() {
    // given
    UserId userId = testUser.getId();
    String newPassword = "newValidPassword123!";
    when(userRepository.findUserById(userId)).thenReturn(testUser);

    // when
    userService.resetPassword(userId, newPassword);

    // then
    verify(userRepository).findUserById(userId);
    verify(userRepository).save(testUser);
    assertThat(testUser.getPassword().password()).isEqualTo(newPassword);
  }

  @Test
  @DisplayName("verifyUser should verify and save user when verification is VERIFIED")
  void verifyUser_shouldVerifyAndSaveUserWhenVerificationIsVerified() {
    // given
    UserId userId = testUser.getId();
    when(userRepository.findUserById(userId)).thenReturn(testUser);

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
    when(userRepository.findUserById(userId)).thenReturn(testUser);

    // when
    userService.verifyUser(userId, Verification.UNVERIFIED);

    // then
    verify(userRepository).findUserById(userId);
    verify(userRepository).delete(testUser);
    verify(userRepository, never()).save(any(User.class));
  }
}