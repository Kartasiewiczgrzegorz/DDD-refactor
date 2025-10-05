package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Email;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserId;
import com.grzegorzkartasiewicz.domain.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class UserRepositoryImpl implements UserRepository {

  private final SqlUserRepository repository;

  @Override
  public User save(User signedUser) {
    return repository.save(UserEntity.fromDomain(signedUser)).toDomain();
  }

  @Override
  public Optional<User> findUserById(UserId verifiedUserId) {
    return Optional.ofNullable(repository.findUserById(verifiedUserId.id())).map(UserEntity::toDomain);
  }

  @Override
  public void delete(User user) {
    repository.deleteById(user.getId().id());
  }

  @Override
  public Optional<User> findUserByEmail(Email email) {
    return Optional.ofNullable(repository.findByEmail(email)).map(UserEntity::toDomain);
  }
}
