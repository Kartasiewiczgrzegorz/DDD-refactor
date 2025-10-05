package com.grzegorzkartasiewicz.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    name.validate();
    this.name = name;
    email.validate();
    this.email = email;
    this.password = password;
    this.verification = Verification.UNVERIFIED;
    this.invalidLogInCounter = new InvalidLogInCounter(0);
    this.blocked = Blocked.NOT_BLOCKED;
  }

  public static User createNew(String firstName, String surname, String email, String rawPassword,
      PasswordEncoder passwordEncoder) {
    Password.validate(rawPassword);
    String encodedPassword = passwordEncoder.encode(rawPassword);
    return new User(new Name(firstName, surname), new Email(
        email), new Password(encodedPassword));
  }

  public void verify() {
    this.verification = Verification.VERIFIED;
  }

  public void verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
    if (!passwordEncoder.matches(rawPassword, this.password.password())) {
      throw new PasswordDoesNotMatchException("Passwords do not match");
    }
  }

  public void recordFailedLoginAttempt() {
    this.invalidLogInCounter = this.invalidLogInCounter.increase();

    if (this.invalidLogInCounter.counter() > 5) {
      this.block();
    }
  }

  private void block() {
    this.blocked = Blocked.BLOCKED;
  }

  public boolean isBlocked() {
    return this.blocked == Blocked.BLOCKED;
  }

  public void resetPassword(String rawNewPassword, PasswordEncoder passwordEncoder) {
    Password.validate(rawNewPassword);
    rawNewPassword = passwordEncoder.encode(rawNewPassword);
    this.password = new Password(rawNewPassword);
  }
}
