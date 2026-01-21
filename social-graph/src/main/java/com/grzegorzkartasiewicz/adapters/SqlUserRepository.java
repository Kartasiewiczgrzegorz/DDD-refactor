package com.grzegorzkartasiewicz.adapters;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SqlUserRepository extends JpaRepository<SocialUserEntity, UUID> {

  List<SocialUserEntity> findAllByIdIn(Collection<UUID> ids);
}
