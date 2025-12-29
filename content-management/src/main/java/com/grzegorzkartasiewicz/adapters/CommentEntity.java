package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Comment;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
class CommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Convert(converter = DescriptionConverter.class)
  private Description description;

  @Convert(converter = AuthorIdConverter.class)
  private AuthorId authorId;

  @Convert(converter = LikeCounterConverter.class)
  private LikeCounter likeCounter;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private PostEntity post;

  Comment toDomain() {
    return new Comment(new CommentId(id), description, authorId, likeCounter);
  }

  static CommentEntity fromDomain(Comment comment, PostEntity post) {
    return new CommentEntity(
        comment.getId() == null ? null : comment.getId().id(),
        comment.getDescription(),
        comment.getAuthorId(),
        comment.getLikeCounter(),
        post
    );
  }
}
