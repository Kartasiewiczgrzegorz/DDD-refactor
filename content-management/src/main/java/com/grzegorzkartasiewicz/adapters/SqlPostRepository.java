package com.grzegorzkartasiewicz.adapters;

import java.util.UUID;
import org.springframework.data.repository.Repository;

interface SqlPostRepository extends Repository<PostEntity, UUID> {

  PostEntity save(PostEntity post);

  PostEntity findPostById(UUID id);

  void deleteById(UUID id);
}
