package com.grzegorzkartasiewicz.comment;



import com.grzegorzkartasiewicz.post.vo.PostId;

import java.util.List;
import java.util.Optional;

interface CommentRepository {
    List<Comment> findAll();

    Optional<Comment> findById(Integer id);

    Comment save(Comment entity);

    void deleteById(Integer integer);

    void deleteAllByPostId(PostId postId);

    List<Comment> findAllByPostId(PostId postId);
}
