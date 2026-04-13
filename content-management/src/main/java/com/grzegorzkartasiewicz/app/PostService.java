package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Comment;
import com.grzegorzkartasiewicz.domain.CommentLiked;
import com.grzegorzkartasiewicz.domain.CommentNotExists;
import com.grzegorzkartasiewicz.domain.CommentUnliked;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.PostAction;
import com.grzegorzkartasiewicz.domain.PostChangedEvent;
import com.grzegorzkartasiewicz.domain.PostLiked;
import com.grzegorzkartasiewicz.domain.PostRepository;
import com.grzegorzkartasiewicz.domain.PostUnliked;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing posts and comments. Handles creation, modification, deletion, and liking of
 * content. Implements optimistic updates for likes with eventual consistency.
 */
@RequiredArgsConstructor
@Slf4j
public class PostService {

  public static final String POST_DONT_EXISTS_MESSAGE = "Post with given ID: %s does not exist";
  private final PostRepository postRepository;
  private final DomainEventPublisher eventPublisher;

  /**
   * Creates a new post.
   *
   * @param postCreationRequest Request data containing description and author ID.
   * @return The created post details.
   */
  public PostResponse addPost(PostCreationRequest postCreationRequest) {
    Description description = new Description(postCreationRequest.description());
    AuthorId authorId = new AuthorId(postCreationRequest.authorId());

    Post postToAdd = Post.createNew(description, authorId);
    Post addedPost = postRepository.save(postToAdd);

    handlePostChangeNotification(addedPost, PostAction.POST_CREATED, authorId, null);

    return getPostResponse(addedPost);
  }

  /**
   * Updates an existing post's description.
   */
  public PostResponse updatePost(PostUpdateRequest postUpdateRequest) {
    Description description = new Description(postUpdateRequest.description());
    AuthorId authorId = new AuthorId(postUpdateRequest.authorId());

    Post postToEdit = findPostOrThrow(postUpdateRequest.id());
    postToEdit.edit(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost, PostAction.POST_EDITED, authorId, null);

    return getPostResponse(editedPost);
  }

  /**
   * Deletes a post.
   */
  public void deletePost(PostDeleteRequest postDeleteRequest) {
    AuthorId authorId = new AuthorId(postDeleteRequest.authorId());
    Post postToDelete = findPostOrThrow(postDeleteRequest.id());

    postToDelete.validatePostAuthor(authorId);
    postRepository.delete(postToDelete);
  }

  /**
   * Adds a comment to a post.
   */
  public PostResponse addComment(CommentCreationRequest commentCreationRequest) {
    Description description = new Description(commentCreationRequest.description());
    AuthorId authorId = new AuthorId(commentCreationRequest.authorId());

    Post postToEdit = findPostOrThrow(commentCreationRequest.postId());
    postToEdit.addComment(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    return getPostResponse(editedPost);
  }

  /**
   * Edits an existing comment.
   */
  public PostResponse editComment(CommentUpdateRequest commentUpdateRequest) {
    Description description = new Description(commentUpdateRequest.description());
    AuthorId authorId = new AuthorId(commentUpdateRequest.authorId());
    CommentId commentId = new CommentId(commentUpdateRequest.commentId());

    Post postToEdit = findPostOrThrow(commentUpdateRequest.postId());
    postToEdit.editComment(commentId, description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost, PostAction.COMMENT_EDITED, authorId, commentId);

    return getPostResponse(editedPost);
  }

  /**
   * Removes a comment from a post.
   */
  public void removeComment(CommentDeleteRequest commentDeleteRequest) {
    AuthorId authorId = new AuthorId(commentDeleteRequest.authorId());
    Post postToEdit = findPostOrThrow(commentDeleteRequest.postId());

    postToEdit.removeComment(new CommentId(commentDeleteRequest.commentId()), authorId);

    postRepository.save(postToEdit);
  }

  /**
   * Likes a post optimistically.
   */
  public PostResponse likePost(UUID postId, UUID authorId) {
    Post postToLike = findPostOrThrow(postId);
    postToLike.increaseLikes();

    AuthorId actorId = new AuthorId(authorId);
    eventPublisher.publish(new PostLiked(postToLike.getId(), actorId));

    handlePostChangeNotification(postToLike, PostAction.POST_LIKED, actorId, null);

    return getPostResponse(postToLike);
  }

  /**
   * Unlikes a post optimistically.
   */
  public PostResponse unlikePost(UUID postId, UUID authorId) {
    Post postToUnlike = findPostOrThrow(postId);
    postToUnlike.decreaseLikes();

    AuthorId actorId = new AuthorId(authorId);
    eventPublisher.publish(new PostUnliked(postToUnlike.getId(), actorId));

    handlePostChangeNotification(postToUnlike, PostAction.POST_UNLIKED, actorId, null);

    return getPostResponse(postToUnlike);
  }

  /**
   * Likes a comment optimistically.
   */
  public PostResponse likeComment(UUID postId, UUID commentId, UUID authorId) {
    Post postToEdit = findPostOrThrow(postId);
    CommentId cId = new CommentId(commentId);

    postToEdit.increaseLikesInComment(cId);

    AuthorId actorId = new AuthorId(authorId);
    eventPublisher.publish(new CommentLiked(postToEdit.getId(), cId, actorId));

    handlePostChangeNotification(postToEdit, PostAction.COMMENT_LIKED, actorId, cId);

    return getPostResponse(postToEdit);
  }

  /**
   * Unlikes a comment optimistically.
   */
  public PostResponse unlikeComment(UUID postId, UUID commentId, UUID authorId) {
    Post postToEdit = findPostOrThrow(postId);
    CommentId cId = new CommentId(commentId);

    postToEdit.decreaseLikesInComment(cId);

    AuthorId actorId = new AuthorId(authorId);
    eventPublisher.publish(new CommentUnliked(postToEdit.getId(), cId, actorId));

    handlePostChangeNotification(postToEdit, PostAction.COMMENT_UNLIKED, actorId, cId);

    return getPostResponse(postToEdit);
  }

  private Post findPostOrThrow(UUID postId) {
    return postRepository.findPostById(new PostId(postId))
        .orElseThrow(() -> new PostNotExists(String.format(POST_DONT_EXISTS_MESSAGE, postId)));
  }

  private List<CommentResponse> mapCommentsToResponse(List<Comment> comments) {
    return comments.stream().map(
            comment -> new CommentResponse(comment.getId().id(), comment.getDescription().text(),
                comment.getAuthorId().id(), comment.getLikeCounter().likeCount()))
        .toList();
  }

  private void handlePostChangeNotification(Post post, PostAction action, AuthorId actorId,
      CommentId commentId) {
    AuthorId recipientId = determineRecipient(post, action, commentId);

    eventPublisher.publish(new PostChangedEvent(
        post.getId(),
        Optional.ofNullable(commentId),
        actorId,
        recipientId,
        action
    ));
  }

  private AuthorId determineRecipient(Post post, PostAction action, CommentId commentId) {
    if (action == PostAction.COMMENT_LIKED || action == PostAction.COMMENT_UNLIKED
        || action == PostAction.COMMENT_EDITED) {
      return post.getComments().stream()
          .filter(c -> c.getId().equals(commentId))
          .map(Comment::getAuthorId)
          .findFirst()
          .orElseThrow(() -> new CommentNotExists("Recipient comment not found"));
    }
    return post.getAuthorId();
  }

  private PostResponse getPostResponse(Post post) {
    return new PostResponse(post.getId().id(), post.getDescription().text(),
        post.getAuthorId().id(), post.getLikeCounter().likeCount(),
        mapCommentsToResponse(post.getComments()));
  }
}
