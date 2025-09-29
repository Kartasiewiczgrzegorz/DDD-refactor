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
class UserEntity {

  @Id
  @EmbeddedId
  private UserId id;
  @Convert(converter = NameConverter.class)
  private Name name;
  @Convert(converter = EmailConverter.class)
  private Email email;
  @Convert(converter = PasswordConverter.class)
  private Password password;
  @Enumerated(EnumType.STRING)
  private Verification verification;
  @Convert(converter = InvalidLogInCounterConverter.class)
  private InvalidLogInCounter invalidLogInCounter;
  @Enumerated(EnumType.STRING)
  private Blocked blocked;

  User toDomain() {
    return new User(id, name, email, password, verification, invalidLogInCounter, blocked);
  }

  static UserEntity fromDomain(User user) {
    return new UserEntity(user.getId(), user.getName(), user.getEmail(), user.getPassword(),
        user.getVerification(), user.getInvalidLogInCounter(), user.getBlocked());
  }
}
