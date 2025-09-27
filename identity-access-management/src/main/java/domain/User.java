package domain;

class User {

  UserId id;
  Name name;
  String surname;
  Email email;
  Password password;

  public User(Name name, Email email, Password password) {
    name.validate();
    this.name = name;
    email.validate();
    this.email = email;
    password.validate();
    this.password = password;
  }
}
