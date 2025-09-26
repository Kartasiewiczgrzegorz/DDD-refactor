package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.DomainEventPublisher;
import com.grzegorzkartasiewicz.comment.vo.CommentCreator;
import com.grzegorzkartasiewicz.comment.CommentDTO;
import com.grzegorzkartasiewicz.comment.CommentFacade;
import com.grzegorzkartasiewicz.comment.vo.CommentId;
import com.grzegorzkartasiewicz.post.vo.PostCreator;
import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.UserFacade;
import com.grzegorzkartasiewicz.user.vo.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PostFacade {
    private static final Logger logger = LoggerFactory.getLogger(PostFacade.class);
    private final PostRepository repository;
    private final CommentFacade commentFacade;
    private final UserFacade userFacade;
    private final DomainEventPublisher publisher;

    PostFacade(PostRepository repository, CommentFacade commentFacade, UserFacade userFacade, DomainEventPublisher publisher) {
        this.repository = repository;
        this.commentFacade = commentFacade;
        this.userFacade = userFacade;
        this.publisher = publisher;
    }

    public List<PostDTO> getAllPostsForHomePage() {
        return repository.findAll().stream()
                .map(this::assemblePostDTO)
                .toList();
    }

    public List<PostDTO> getPostsForUser(int userId) {
        return repository.findAllByUserId(new UserId(userId)).stream()
                .map(this::assemblePostDTO)
                .toList();
    }

    public PostDTO createPost(PostCreator source){
        return assemblePostDTO(repository.save(Post.createFrom(source)));
    }
    public CommentDTO createComment(UserDTO user, int postId, String description){
        logger.info("Creating comment to save in DB!");
        Post post = repository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with given id was not found!"));

        CommentCreator commentCreator = post.prepareNewComment(description, new UserId(user.getId()));

        return commentFacade.createComment(commentCreator);
    }

    public PostDTO editPost(PostId postId, String description) {
        logger.info("Edit post with id: {}", postId.id());
        return repository.findById(postId.id()).map(post -> post.edit(description)).map(this::assemblePostDTO).orElse(null);
    }

    List<PostDTO> searchPosts(String search) {
        logger.info("Searching for matching users and posts!");
        return repository.findAllByDescriptionContainingIgnoreCase(search).stream()
                .map(this::assemblePostDTO)
                .toList();
    }

    public void deleteComment(CommentId commentId){
        commentFacade.deleteComment(commentId);
    }

    public void deletePost(int postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with given id not found"));

        post.markAsDeleted();

        post.getDomainEvents().forEach(publisher::publish);
        post.clearDomainEvents();

        repository.deleteById(postId);
        logger.info("Post with id: {} deleted and PostDeletedEvent published.", postId);
    }

    public PostDTO assemblePostDTO(Post post) {
        PostSnapshot snapshot = post.getSnapshot();
        UserDTO author = userFacade.getUser(snapshot.getUserId());
        List<CommentDTO> comments = commentFacade.getCommentsForPost(snapshot.getId());

        return PostDTO.toDTO(post, author, comments);
    }
}
