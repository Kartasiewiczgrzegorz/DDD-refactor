package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Email;
import com.grzegorzkartasiewicz.domain.UserId;
import java.util.UUID;
import org.springframework.data.repository.Repository;

public interface SqlUserRepository extends Repository<UserEntity, UUID> {

  UserEntity save(UserEntity signedUser);

  UserEntity findUserById(UUID id);

  void deleteById(UUID userId);

  UserEntity findByEmail(Email email);
}

