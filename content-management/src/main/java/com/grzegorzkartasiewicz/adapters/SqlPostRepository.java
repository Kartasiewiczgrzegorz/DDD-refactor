package com.grzegorzkartasiewicz.adapters;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

interface SqlPostRepository extends Repository<PostEntity, UUID> {

  PostEntity save(PostEntity post);

  PostEntity findPostById(UUID id);

  void deleteById(UUID id);

  @Modifying
  @Query(value = "UPDATE posts SET like_counter = like_counter + :delta WHERE id = :id", nativeQuery = true)
  void updateLikeCount(@Param("id") UUID id, @Param("delta") int delta);
}
