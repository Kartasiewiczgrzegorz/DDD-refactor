package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.RawPassword;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import com.grzegorzkartasiewicz.domain.vo.Verification;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private final String emailStr = "test@example.com";
  private final String password = "Password123!";
  private final UserId userId = new UserId(UUID.randomUUID());
  @Mock
  private UserRepository userRepository;
  @Mock
  private AuthorizationPort authorizationPort;
  @Mock
  private DomainEventPublisher publisher;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private UserService userService;

  @Test
  @DisplayName("signUp should create user, publish event and return registered user info")
  void signUp_shouldCreateUserAndReturnInfo() {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest("John", "Doe", emailStr,
        password);
    when(userRepository.findUserByEmail(any(Email.class))).thenReturn(Optional.empty());
    when(passwordEncoder.encode(password)).thenReturn("hashedPassword");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(authorizationPort.generateToken(any(User.class))).thenReturn(new Token("jwt-token"));

    // when
    RegisteredUser result = userService.signUp(request);

    // then
    assertThat(result.email()).isEqualTo(emailStr);
    assertThat(result.token()).isEqualTo("jwt-token");
    verify(userRepository).save(any(User.class));
    verify(publisher).publish(any(UserEmailVerificationNeededEvent.class));
  }

  @Test
  @DisplayName("signUp should throw exception if user already exists")
  void signUp_shouldThrowIfUserExists() {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest("John", "Doe", emailStr,
        password);
    when(userRepository.findUserByEmail(any(Email.class))).thenReturn(
        Optional.of(org.mockito.Mockito.mock(User.class)));

    // when & then
    assertThatThrownBy(() -> userService.signUp(request))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("logIn should return logged user info when credentials are valid")
  void logIn_shouldReturnInfoOnSuccess() {
    // given
    UserLogInRequest request = new UserLogInRequest(emailStr, password);
    User user = User.createNew(new com.grzegorzkartasiewicz.domain.vo.Name("John", "Doe"),
        new Email(emailStr), new RawPassword(password), passwordEncoder);

    when(userRepository.findUserByEmail(any(Email.class))).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, user.getPassword().password())).thenReturn(true);
    when(authorizationPort.generateToken(user)).thenReturn(new Token("jwt-token"));

    // when
    LoggedUser result = userService.logIn(request);

    // then
    assertThat(result.email()).isEqualTo(emailStr);
    assertThat(result.token()).isEqualTo("jwt-token");
  }

  @Test
  @DisplayName("logIn should throw exception and record failed attempt when password does not match")
  void logIn_shouldRecordFailedAttemptOnWrongPassword() {
    // given
    UserLogInRequest request = new UserLogInRequest(emailStr, "WrongPass");
    User user = User.createNew(new com.grzegorzkartasiewicz.domain.vo.Name("John", "Doe"),
        new Email(emailStr), new RawPassword(password), passwordEncoder);

    when(userRepository.findUserByEmail(any(Email.class))).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("WrongPass", user.getPassword().password())).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> userService.logIn(request))
        .isInstanceOf(InvalidCredentialsException.class);
    verify(userRepository).save(user);
    assertThat(user.getInvalidLogInCounter().counter()).isEqualTo(1);
  }

  @Test
  @DisplayName("requestResetPassword should publish ResetPasswordEvent")
  void requestResetPassword_shouldPublishEvent() {
    // given
    ResetPasswordRequest request = new ResetPasswordRequest(emailStr, "NewPassword");
    User user = org.mockito.Mockito.mock(User.class);
    when(user.getId()).thenReturn(userId);
    when(userRepository.findUserByEmail(any(Email.class))).thenReturn(Optional.of(user));

    // when
    userService.requestResetPassword(request);

    // then
    verify(publisher).publish(any(ResetPasswordEvent.class));
  }

  @Test
  @DisplayName("resetPassword should update user password and save")
  void resetPassword_shouldUpdatePassword() {
    // given
    User user = User.createNew(new com.grzegorzkartasiewicz.domain.vo.Name("John", "Doe"),
        new Email(emailStr), new RawPassword(password), passwordEncoder);
    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    String newPass = "NewPassword456!";
    when(passwordEncoder.encode(newPass)).thenReturn("hashedNewPassword");

    // when
    userService.resetPassword(userId, newPass);

    // then
    verify(userRepository).save(user);
    assertThat(user.getPassword().password()).isEqualTo("hashedNewPassword");
  }

  @Test
  @DisplayName("verifyUser should mark user as VERIFIED and save")
  void verifyUser_shouldMarkAsVerified() {
    // given
    User user = User.createNew(new com.grzegorzkartasiewicz.domain.vo.Name("John", "Doe"),
        new Email(emailStr), new RawPassword(password), passwordEncoder);
    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

    // when
    userService.verifyUser(userId, Verification.VERIFIED);

    // then
    verify(userRepository).save(user);
    assertThat(user.getVerification()).isEqualTo(Verification.VERIFIED);
  }

  @Test
  @DisplayName("verifyUser should delete user if verification is UNVERIFIED")
  void verifyUser_shouldDeleteIfUnverified() {
    // given
    User user = User.createNew(new com.grzegorzkartasiewicz.domain.vo.Name("John", "Doe"),
        new Email(emailStr), new RawPassword(password), passwordEncoder);
    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

    // when
    userService.verifyUser(userId, Verification.UNVERIFIED);

    // then
    verify(userRepository).delete(user);
  }
}
