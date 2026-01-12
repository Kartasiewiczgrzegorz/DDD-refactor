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
@Table(name = "comment_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"commentId", "likerId"})
})
@AllArgsConstructor
@NoArgsConstructor
class CommentLikeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private UUID postId; // De-normalized for easier querying if needed, or just link to commentId

  private UUID commentId;

  private UUID likerId;

  public CommentLikeEntity(UUID postId, UUID commentId, UUID likerId) {
    this.postId = postId;
    this.commentId = commentId;
    this.likerId = likerId;
  }
}
