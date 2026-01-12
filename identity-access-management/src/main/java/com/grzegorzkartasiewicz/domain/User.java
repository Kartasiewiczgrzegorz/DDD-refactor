package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.Blocked;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.InvalidLogInCounter;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.Password;
import com.grzegorzkartasiewicz.domain.vo.RawPassword;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import com.grzegorzkartasiewicz.domain.vo.Verification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Aggregate Root representing a User in the identity and access management context. Manages user
 * authentication, password management, and account status (blocking/verification).
 */
@Getter
@AllArgsConstructor
public class User {

  UserId id;
  Name name;
  Email email;
  Password password;
  Verification verification;
  InvalidLogInCounter invalidLogInCounter;
  Blocked blocked;

  User(Name name, Email email, Password password) {
    this.id = new UserId(null);
    this.name = name;
    this.email = email;
    this.password = password;
    this.verification = Verification.UNVERIFIED;
    this.invalidLogInCounter = new InvalidLogInCounter(0);
    this.blocked = Blocked.NOT_BLOCKED;
  }

  /**
   * Factory method to create a new User.
   *
   * @param name            The user's name.
   * @param email           The user's email.
   * @param rawPassword     The user's raw password (will be encoded).
   * @param passwordEncoder The encoder to use for password hashing.
   * @return A new User instance.
   */
  public static User createNew(Name name, Email email, RawPassword rawPassword,
      PasswordEncoder passwordEncoder) {
    String encodedPassword = passwordEncoder.encode(rawPassword.rawPassword());
    return new User(name, email, new Password(encodedPassword));
  }

  /**
   * Marks the user account as verified.
   */
  public void verify() {
    this.verification = Verification.VERIFIED;
  }

  /**
   * Verifies if the provided raw password matches the user's stored password.
   *
   * @param rawPassword The raw password to verify.
   * @param passwordEncoder The encoder to use for verification.
   * @throws PasswordDoesNotMatchException if the passwords do not match.
   */
  public void verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
    if (!passwordEncoder.matches(rawPassword, this.password.password())) {
      throw new PasswordDoesNotMatchException("Passwords do not match");
    }
  }

  /**
   * Records a failed login attempt and blocks the account if the threshold is exceeded.
   */
  public void recordFailedLoginAttempt() {
    this.invalidLogInCounter = this.invalidLogInCounter.increase();

    if (this.invalidLogInCounter.counter() > 5) {
      this.block();
    }
  }

  private void block() {
    this.blocked = Blocked.BLOCKED;
  }

  /**
   * Checks if the user account is blocked.
   *
   * @return true if blocked, false otherwise.
   */
  public boolean isBlocked() {
    return this.blocked == Blocked.BLOCKED;
  }

  /**
   * Resets the user's password.
   *
   * @param rawNewPassword The new raw password.
   * @param passwordEncoder The encoder to use for hashing the new password.
   */
  public void resetPassword(RawPassword rawNewPassword, PasswordEncoder passwordEncoder) {
    String encodedPassword = passwordEncoder.encode(rawNewPassword.rawPassword());
    this.password = new Password(encodedPassword);
  }
}
