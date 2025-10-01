package com.grzegorzkartasiewicz.domain;

import java.util.Optional;

public interface UserRepository {

  User save(User signedUser);

  Optional<User> findUserById(UserId verifiedUserId);

  void delete(User user);

  Optional<User> findUserByEmail(Email email);
}
