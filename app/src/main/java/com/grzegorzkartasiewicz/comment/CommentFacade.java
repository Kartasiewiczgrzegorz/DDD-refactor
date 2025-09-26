package com.grzegorzkartasiewicz.comment;

import com.grzegorzkartasiewicz.comment.vo.CommentCreator;
import com.grzegorzkartasiewicz.comment.vo.CommentId;
import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.UserFacade;

import java.util.List;

public class CommentFacade {
    private final CommentRepository repository;
    private final UserFacade userFacade;

    CommentFacade(CommentRepository repository, UserFacade userFacade) {
        this.repository = repository;
        this.userFacade = userFacade;
    }


    public CommentDTO createComment(CommentCreator newComment){
        Comment savedComment = repository.save(Comment.createFrom(newComment));
        return assembleCommentDTO(savedComment);
    }

    public void deleteComment(CommentId commentId){
        repository.deleteById(commentId.id());
    }

    public void deleteCommentsForPost(PostId postId){
        repository.deleteAllByPostId(postId);
    }

    public List<CommentDTO> getCommentsForPost(int postId) {
        return repository.findAllByPostId(new PostId(postId)).stream()
                .map(this::assembleCommentDTO)
                .toList();
    }

    private CommentDTO assembleCommentDTO(Comment comment) {
        CommentSnapshot snapshot = comment.getSnapshot();
        UserDTO author = userFacade.getUser(snapshot.getUserId());

        return CommentDTO.toDTO(comment, author);
    }
}
