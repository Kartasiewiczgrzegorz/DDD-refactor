package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Optional;

public interface UserRepository {

  User save(User signedUser);

  Optional<User> findUserById(UserId verifiedUserId);

  void delete(User user);

  Optional<User> findUserByEmail(Email email);
}
