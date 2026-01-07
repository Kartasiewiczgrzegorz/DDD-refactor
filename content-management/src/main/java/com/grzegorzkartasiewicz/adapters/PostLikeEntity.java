package com.grzegorzkartasiewicz.adapters;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"postId", "likerId"})
})
@AllArgsConstructor
@NoArgsConstructor
class PostLikeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private UUID postId;

  private UUID likerId;

  public PostLikeEntity(UUID postId, UUID likerId) {
    this.postId = postId;
    this.likerId = likerId;
  }
}
