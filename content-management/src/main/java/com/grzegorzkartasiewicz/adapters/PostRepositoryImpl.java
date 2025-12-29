package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.PostRepository;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class PostRepositoryImpl implements PostRepository {

  private final SqlPostRepository repository;

  @Override
  public Post save(Post post) {
    return repository.save(PostEntity.fromDomain(post)).toDomain();
  }

  @Override
  public Optional<Post> findPostById(PostId id) {
    return Optional.ofNullable(repository.findPostById(id.id())).map(PostEntity::toDomain);
  }

  @Override
  public void delete(Post postToDelete) {
    repository.deleteById(postToDelete.getId().id());
  }
}
