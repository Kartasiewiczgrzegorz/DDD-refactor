package com.grzegorzkartasiewicz.comment;



import com.grzegorzkartasiewicz.post.vo.PostId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


interface SqlCommentRepository extends Repository<CommentSnapshot,Integer> {
    List<CommentSnapshot> findAll();

    Optional<CommentSnapshot> findById(Integer id);

    CommentSnapshot save(CommentSnapshot entity);

    void deleteById(Integer integer);

    @Modifying
    @Transactional
    void deleteAllByPostId(PostId postId);

    List<CommentSnapshot> findAllByPostId(PostId postId);
}

@org.springframework.stereotype.Repository
class CommentRepositoryImpl implements CommentRepository {
    private final SqlCommentRepository repository;

    CommentRepositoryImpl(final SqlCommentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Comment> findAll() {
        return repository.findAll().stream().map(Comment::restore).toList();
    }

    @Override
    public Optional<Comment> findById(Integer id) {
        return repository.findById(id).map(Comment::restore);
    }

    @Override
    public Comment save(Comment entity) {
        return Comment.restore(repository.save(entity.getSnapshot()));
    }

    @Override
    public void deleteById(Integer integer) {
        repository.deleteById(integer);
    }

    @Override
    public void deleteAllByPostId(PostId postId) {
        repository.deleteAllByPostId(postId);
    }

    @Override
    public List<Comment> findAllByPostId(PostId postId) {
        return repository.findAllByPostId(postId).stream().map(Comment::restore).toList();
    }
}
