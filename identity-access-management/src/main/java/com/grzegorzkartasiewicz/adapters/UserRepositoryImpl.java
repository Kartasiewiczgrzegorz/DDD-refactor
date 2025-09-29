package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Email;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserId;
import com.grzegorzkartasiewicz.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

  private final SqlUserRepository repository;

  @Override
  public User save(User signedUser) {
    return repository.save(UserEntity.fromDomain(signedUser)).toDomain();
  }

  @Override
  public User findUserById(UserId verifiedUserId) {
    return repository.findUserById(verifiedUserId).toDomain();
  }

  @Override
  public void delete(User user) {
    repository.deleteById(user.getId());
  }

  @Override
  public User findUserByEmail(Email email) {
    return repository.findByEmail(email).toDomain();
  }
}
