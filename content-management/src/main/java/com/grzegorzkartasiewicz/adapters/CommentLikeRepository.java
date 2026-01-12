package com.grzegorzkartasiewicz.adapters;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, UUID> {

  Optional<CommentLikeEntity> findByCommentIdAndLikerId(UUID commentId, UUID likerId);
}
