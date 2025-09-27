package domain;

import app.UserRegistrationRequest;
import lombok.Getter;

@Getter
public class User {

  UserId id;
  Name name;
  String surname;
  Email email;
  Password password;
  Verification verification;

  User(Name name, Email email, Password password) {
    name.validate();
    this.name = name;
    email.validate();
    this.email = email;
    password.validate();
    this.password = password;
    this.verification = Verification.UNVERIFIED;
  }

  public User(UserRegistrationRequest userRegistrationRequest) {
    this(new Name(userRegistrationRequest.firstName(), userRegistrationRequest.lastName()), new Email(
        userRegistrationRequest.email()), new Password(userRegistrationRequest.password()));
  }

  public void verify() {
    this.verification = Verification.VERIFIED;
  }
}
