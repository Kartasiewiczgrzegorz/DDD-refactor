package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findById(UserId userId);

  User save(User userToSave);
}
