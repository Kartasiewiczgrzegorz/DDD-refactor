package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Blocked;
import com.grzegorzkartasiewicz.domain.Email;
import com.grzegorzkartasiewicz.domain.InvalidLogInCounter;
import com.grzegorzkartasiewicz.domain.Name;
import com.grzegorzkartasiewicz.domain.Password;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserId;
import com.grzegorzkartasiewicz.domain.Verification;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
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

  public User toDomain() {
    return new User(new UserId(id), name, email, password, verification, invalidLogInCounter, blocked);
  }

  public static UserEntity fromDomain(User user) {
    return new UserEntity(user.getId().id(), user.getName(), user.getEmail(), user.getPassword(),
        user.getVerification(), user.getInvalidLogInCounter(), user.getBlocked());
  }
}
