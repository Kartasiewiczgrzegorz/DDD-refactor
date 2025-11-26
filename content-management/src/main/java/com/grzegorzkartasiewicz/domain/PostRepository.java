package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.Optional;

public interface PostRepository {

  Post save(Post post);

  Optional<Post> findPostById(PostId id);

  void delete(Post postToDelete);
}
