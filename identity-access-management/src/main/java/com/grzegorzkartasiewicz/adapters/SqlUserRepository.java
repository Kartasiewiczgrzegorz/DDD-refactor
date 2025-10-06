package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.Email;
import java.util.UUID;
import org.springframework.data.repository.Repository;

interface SqlUserRepository extends Repository<UserEntity, UUID> {

  UserEntity save(UserEntity signedUser);

  UserEntity findUserById(UUID id);

  void deleteById(UUID userId);

  UserEntity findByEmail(Email email);
}

