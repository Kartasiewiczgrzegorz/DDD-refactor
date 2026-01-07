package com.grzegorzkartasiewicz.adapters;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface PostLikeRepository extends JpaRepository<PostLikeEntity, UUID> {
    Optional<PostLikeEntity> findByPostIdAndLikerId(UUID postId, UUID likerId);
    void deleteByPostIdAndLikerId(UUID postId, UUID likerId);
}
