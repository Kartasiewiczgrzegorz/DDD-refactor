package com.grzegorzkartasiewicz.adapters;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

interface SqlCommentRepository extends Repository<CommentEntity, UUID> {

  @Modifying
  @Query(value = "UPDATE comments SET like_counter = like_counter + :delta WHERE id = :id", nativeQuery = true)
  void updateLikeCount(@Param("id") UUID id, @Param("delta") int delta);
}
