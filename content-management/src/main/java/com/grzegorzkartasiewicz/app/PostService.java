package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Comment;
import com.grzegorzkartasiewicz.domain.CommentLiked;
import com.grzegorzkartasiewicz.domain.CommentUnliked;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.PostLiked;
import com.grzegorzkartasiewicz.domain.PostRepository;
import com.grzegorzkartasiewicz.domain.PostUnliked;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.List;
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

    handlePostChangeNotification(addedPost);

    return getPostResponse(addedPost);
  }

  /**
   * Updates an existing post's description.
   *
   * @param postUpdateRequest Request data containing post ID, new description, and author ID.
   * @return The updated post details.
   */
  public PostResponse updatePost(PostUpdateRequest postUpdateRequest) {
    Description description = new Description(postUpdateRequest.description());
    AuthorId authorId = new AuthorId(postUpdateRequest.authorId());

    Post postToEdit = findPostOrThrow(postUpdateRequest.id());
    postToEdit.edit(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);

    return getPostResponse(editedPost);
  }

  /**
   * Deletes a post.
   *
   * @param postDeleteRequest Request data containing post ID and author ID.
   */
  public void deletePost(PostDeleteRequest postDeleteRequest) {
    AuthorId authorId = new AuthorId(postDeleteRequest.authorId());
    Post postToDelete = findPostOrThrow(postDeleteRequest.id());

    postToDelete.validatePostAuthor(authorId);
    postRepository.delete(postToDelete);

    handlePostChangeNotification(postToDelete);
  }

  /**
   * Adds a comment to a post.
   *
   * @param commentCreationRequest Request data containing post ID, description, and author ID.
   * @return The updated post details including the new comment.
   */
  public PostResponse addComment(CommentCreationRequest commentCreationRequest) {
    Description description = new Description(commentCreationRequest.description());
    AuthorId authorId = new AuthorId(commentCreationRequest.authorId());

    Post postToEdit = findPostOrThrow(commentCreationRequest.postId());
    postToEdit.addComment(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);

    return getPostResponse(editedPost);
  }

  /**
   * Edits an existing comment.
   *
   * @param commentUpdateRequest Request data containing post ID, comment ID, new description, and author ID.
   * @return The updated post details.
   */
  public PostResponse editComment(CommentUpdateRequest commentUpdateRequest) {
    Description description = new Description(commentUpdateRequest.description());
    AuthorId authorId = new AuthorId(commentUpdateRequest.authorId());

    Post postToEdit = findPostOrThrow(commentUpdateRequest.postId());
    postToEdit.editComment(new CommentId(commentUpdateRequest.commentId()), description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);

    return getPostResponse(editedPost);
  }

  /**
   * Removes a comment from a post.
   *
   * @param commentDeleteRequest Request data containing post ID, comment ID, and author ID.
   */
  public void removeComment(CommentDeleteRequest commentDeleteRequest) {
    AuthorId authorId = new AuthorId(commentDeleteRequest.authorId());
    Post postToEdit = findPostOrThrow(commentDeleteRequest.postId());

    postToEdit.removeComment(new CommentId(commentDeleteRequest.commentId()), authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);
  }

  /**
   * Likes a post optimistically.
   *
   * @param postId The ID of the post to like.
   * @param authorId The ID of the user liking the post.
   * @return The post details with the optimistically incremented like count.
   */
  public PostResponse likePost(UUID postId, UUID authorId) {
    Post postToLike = findPostOrThrow(postId);
    postToLike.increaseLikes();

    eventPublisher.publish(new PostLiked(postToLike.getId(), new AuthorId(authorId)));

    handlePostChangeNotification(postToLike);

    return getPostResponse(postToLike);
  }

  /**
   * Unlikes a post optimistically.
   *
   * @param postId The ID of the post to unlike.
   * @param authorId The ID of the user unliking the post.
   * @return The post details with the optimistically decremented like count.
   */
  public PostResponse unlikePost(UUID postId, UUID authorId) {
    Post postToUnlike = findPostOrThrow(postId);
    postToUnlike.decreaseLikes();

    eventPublisher.publish(new PostUnliked(postToUnlike.getId(), new AuthorId(authorId)));

    handlePostChangeNotification(postToUnlike);

    return getPostResponse(postToUnlike);
  }

  /**
   * Likes a comment optimistically.
   *
   * @param postId The ID of the post containing the comment.
   * @param commentId The ID of the comment to like.
   * @param authorId The ID of the user liking the comment.
   * @return The post details with the optimistically incremented comment like count.
   */
  public PostResponse likeComment(UUID postId, UUID commentId, UUID authorId) {
    Post postToEdit = findPostOrThrow(postId);
    CommentId cId = new CommentId(commentId);

    postToEdit.increaseLikesInComment(cId);

    eventPublisher.publish(new CommentLiked(postToEdit.getId(), cId, new AuthorId(authorId)));

    handlePostChangeNotification(postToEdit);

    return getPostResponse(postToEdit);
  }

  /**
   * Unlikes a comment optimistically.
   *
   * @param postId The ID of the post containing the comment.
   * @param commentId The ID of the comment to unlike.
   * @param authorId The ID of the user unliking the comment.
   * @return The post details with the optimistically decremented comment like count.
   */
  public PostResponse unlikeComment(UUID postId, UUID commentId, UUID authorId) {
    Post postToEdit = findPostOrThrow(postId);
    CommentId cId = new CommentId(commentId);

    postToEdit.decreaseLikesInComment(cId);

    eventPublisher.publish(new CommentUnliked(postToEdit.getId(), cId, new AuthorId(authorId)));

    handlePostChangeNotification(postToEdit);

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

  private void handlePostChangeNotification(Post post) {
    //TODO: Replace with actual notification logic, e.g., publish domain event or send email
    log.info("Notification: Post or its comment changed: {}", post.getId().id());
  }

  private PostResponse getPostResponse(Post post) {
    return new PostResponse(post.getId().id(), post.getDescription().text(),
        post.getAuthorId().id(), post.getLikeCounter().likeCount(),
        mapCommentsToResponse(post.getComments()));
  }
}
