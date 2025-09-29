package adapters;

import domain.Blocked;
import domain.Email;
import domain.InvalidLogInCounter;
import domain.Name;
import domain.Password;
import domain.User;
import domain.UserId;
import domain.Verification;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

  @Id
  @EmbeddedId
  UserId id;
  @Convert(converter = NameConverter.class)
  Name name;
  @Convert(converter = EmailConverter.class)
  Email email;
  @Convert(converter = PasswordConverter.class)
  Password password;
  @Enumerated(EnumType.STRING)
  Verification verification;
  @Convert(converter = InvalidLogInCounterConverter.class)
  InvalidLogInCounter invalidLogInCounter;
  @Enumerated(EnumType.STRING)
  Blocked blocked;

  public User toDomain() {
    return new User(id, name, email, password, verification, invalidLogInCounter, blocked);
  }

  public static UserEntity fromDomain(User user) {
    return new UserEntity(user.getId(), user.getName(), user.getEmail(), user.getPassword(),
        user.getVerification(), user.getInvalidLogInCounter(), user.getBlocked());
  }
}
