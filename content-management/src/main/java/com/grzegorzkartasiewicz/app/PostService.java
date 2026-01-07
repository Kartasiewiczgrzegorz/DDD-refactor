package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Comment;
import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.PostRepository;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PostService {

  public static final String POST_DONT_EXISTS_MESSAGE = "Post with given ID: %s does not exist";
  private final PostRepository postRepository;

  public PostResponse addPost(PostCreationRequest postCreationRequest) {
    Description description = validateDescription(postCreationRequest.description());
    AuthorId authorId = validateAuthorId(postCreationRequest.authorId());

    Post postToAdd = Post.createNew(description, authorId);
    Post addedPost = postRepository.save(postToAdd);

    handlePostChangeNotification(addedPost);

    return getPostResponse(addedPost);
  }

  public PostResponse updatePost(PostUpdateRequest postUpdateRequest) {
    Description description = validateDescription(postUpdateRequest.description());
    AuthorId authorId = validateAuthorId(postUpdateRequest.authorId());

    Post postToEdit = findPostOrThrow(postUpdateRequest.id());
    postToEdit.edit(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);

    return getPostResponse(editedPost);
  }

  public void deletePost(PostDeleteRequest postDeleteRequest) {
    AuthorId authorId = validateAuthorId(postDeleteRequest.authorId());
    Post postToDelete = findPostOrThrow(postDeleteRequest.id());

    postToDelete.validatePostAuthor(authorId);
    postRepository.delete(postToDelete);

    handlePostChangeNotification(postToDelete); // Notify about deletion
  }

  public PostResponse addComment(CommentCreationRequest commentCreationRequest) {
    Description description = validateDescription(commentCreationRequest.description());
    AuthorId authorId = validateAuthorId(commentCreationRequest.authorId());

    Post postToEdit = findPostOrThrow(commentCreationRequest.postId());
    postToEdit.addComment(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);

    return getPostResponse(editedPost);
  }

  public PostResponse editComment(CommentUpdateRequest commentUpdateRequest) {
    Description description = validateDescription(commentUpdateRequest.description());
    AuthorId authorId = validateAuthorId(commentUpdateRequest.authorId());

    Post postToEdit = findPostOrThrow(commentUpdateRequest.postId());
    postToEdit.editComment(new CommentId(commentUpdateRequest.commentId()), description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);

    return getPostResponse(editedPost);
  }

  public void removeComment(CommentDeleteRequest commentDeleteRequest) {
    AuthorId authorId = validateAuthorId(commentDeleteRequest.authorId());
    Post postToEdit = findPostOrThrow(commentDeleteRequest.postId());

    postToEdit.removeComment(new CommentId(commentDeleteRequest.commentId()), authorId);

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);
  }

  public PostResponse likePost(UUID postId) {
    Post postToLike = findPostOrThrow(postId);
    postToLike.increaseLikes();

    Post likedPost = postRepository.save(postToLike);

    handlePostChangeNotification(likedPost);

    return getPostResponse(likedPost);
  }

  public PostResponse unlikePost(UUID postId) {
    Post postToUnlike = findPostOrThrow(postId);
    postToUnlike.decreaseLikes();

    Post unlikedPost = postRepository.save(postToUnlike);

    handlePostChangeNotification(unlikedPost);

    return getPostResponse(unlikedPost);
  }

  public PostResponse likeComment(UUID postId, UUID commentId) {
    Post postToEdit = findPostOrThrow(postId);
    postToEdit.increaseLikesInComment(new CommentId(commentId));

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);

    return getPostResponse(editedPost);
  }

  public PostResponse unlikeComment(UUID postId, UUID commentId) {
    Post postToEdit = findPostOrThrow(postId);
    postToEdit.decreaseLikesInComment(new CommentId(commentId));

    Post editedPost = postRepository.save(postToEdit);

    handlePostChangeNotification(editedPost);

    return getPostResponse(editedPost);
  }

  private Description validateDescription(String descriptionText) {
    Description description = new Description(descriptionText);
    description.validate();
    return description;
  }

  private AuthorId validateAuthorId(UUID authorUuid) {
    AuthorId authorId = new AuthorId(authorUuid);
    authorId.validate();
    return authorId;
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
