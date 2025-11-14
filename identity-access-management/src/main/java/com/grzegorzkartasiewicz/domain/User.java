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

  public static User createNew(Name name, Email email, RawPassword rawPassword,
      PasswordEncoder passwordEncoder) {
    String encodedPassword = passwordEncoder.encode(rawPassword.rawPassword());
    return new User(name, email, new Password(encodedPassword));
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

  public void resetPassword(RawPassword rawNewPassword, PasswordEncoder passwordEncoder) {
    String encodedPassword = passwordEncoder.encode(rawNewPassword.rawPassword());
    this.password = new Password(encodedPassword);
  }
}
