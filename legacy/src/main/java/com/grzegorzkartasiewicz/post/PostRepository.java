package com.grzegorzkartasiewicz.post;


import com.grzegorzkartasiewicz.user.vo.UserId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface PostRepository {
    List<Post> findAll();

    Optional<Post> findById(Integer id);

    Post save(Post entity);

    void deleteById(Integer integer);

    List<Post> findAllByDescriptionContainingIgnoreCase(String description);

    List<Post> findAllByUserId(UserId userId);
}
