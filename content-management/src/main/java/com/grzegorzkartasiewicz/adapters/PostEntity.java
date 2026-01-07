package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
class PostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Convert(converter = DescriptionConverter.class)
  private Description description;

  @Convert(converter = AuthorIdConverter.class)
  private AuthorId authorId;

  @Convert(converter = LikeCounterConverter.class)
  private LikeCounter likeCounter;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentEntity> comments = new ArrayList<>();

  Post toDomain() {
    return new Post(
        new PostId(id),
        description,
        authorId,
        likeCounter,
        comments.stream().map(CommentEntity::toDomain).toList()
    );
  }

  static PostEntity fromDomain(Post post) {
    PostEntity entity = new PostEntity();
    entity.id = post.getId() == null ? null : post.getId().id();
    entity.description = post.getDescription();
    entity.authorId = post.getAuthorId();
    entity.likeCounter = post.getLikeCounter();
    if (post.getComments() != null) {
      entity.comments = post.getComments().stream()
          .map(comment -> CommentEntity.fromDomain(comment, entity))
          .toList();
    }
    return entity;
  }
}
