package domain;

import app.UserRegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    name.validate();
    this.name = name;
    email.validate();
    this.email = email;
    password.validate();
    this.password = password;
    this.verification = Verification.UNVERIFIED;
    this.invalidLogInCounter = new InvalidLogInCounter(0);
    this.blocked = Blocked.NOT_BLOCKED;
  }

  public User(UserRegistrationRequest userRegistrationRequest) {
    this(new Name(userRegistrationRequest.firstName(), userRegistrationRequest.lastName()), new Email(
        userRegistrationRequest.email()), new Password(userRegistrationRequest.password()));
  }

  public void verify() {
    this.verification = Verification.VERIFIED;
  }

  public void verifyPassword(String password) {
    this.password.isEqual(password);
  }

  public void increaseInvalidLogInCounter() {
    this.invalidLogInCounter.increase();

    if (this.invalidLogInCounter.counter() > 5) {
      this.block();
    }
  }

  private void block() {
    this.blocked = Blocked.BLOCKED;
  }

  public boolean isBlocked() {
    return this.blocked == Blocked.NOT_BLOCKED;
  }

  public void resetPassword(String password) {
    Password newPassword = new Password(password);
    newPassword.validate();
    this.password = newPassword;
  }
}
