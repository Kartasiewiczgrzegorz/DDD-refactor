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

@RequiredArgsConstructor
public class PostService {

  public static final String POST_DONT_EXISTS_MESSAGE = "Post with given ID: %s does not exist";
  private final PostRepository postRepository;

  public PostResponse addPost(PostCreationRequest postCreationRequest) {
    Description description = new Description(postCreationRequest.description());
    description.validate();
    AuthorId authorId = new AuthorId(postCreationRequest.authorId());
    authorId.validate();
    Post postToAdd = Post.createNew(description,
        authorId);

    Post addedPost = postRepository.save(postToAdd);

    //TODO send notification email

    return getPostResponse(addedPost);
  }

  public PostResponse updatePost(PostUpdateRequest postUpdateRequest) {
    Description description = new Description(postUpdateRequest.description());
    description.validate();
    AuthorId authorId = new AuthorId(postUpdateRequest.authorId());
    authorId.validate();
    Post postToEdit = postRepository.findPostById(new PostId(postUpdateRequest.id())).orElseThrow(
        () -> new PostNotExists(
            String.format(POST_DONT_EXISTS_MESSAGE, postUpdateRequest.id())));

    postToEdit.edit(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    //TODO send notification email

    return getPostResponse(editedPost);
  }

  public void deletePost(PostDeleteRequest postDeleteRequest) {
    AuthorId authorId = new AuthorId(postDeleteRequest.authorId());
    authorId.validate();

    Post postToDelete = postRepository.findPostById(new PostId(postDeleteRequest.id())).orElseThrow(
        () -> new PostNotExists(
            String.format(POST_DONT_EXISTS_MESSAGE, postDeleteRequest.id())));

    postToDelete.validateIfAuthorIsTheSame(postToDelete.getAuthorId(), authorId);

    postRepository.delete(postToDelete);
  }

  public PostResponse addComment(CommentCreationRequest commentCreationRequest) {
    Description description = new Description(commentCreationRequest.description());
    description.validate();
    AuthorId authorId = new AuthorId(commentCreationRequest.authorId());
    authorId.validate();

    Post postToEdit = postRepository.findPostById(new PostId(commentCreationRequest.postId()))
        .orElseThrow(
            () -> new PostNotExists(
                String.format(POST_DONT_EXISTS_MESSAGE,
                    commentCreationRequest.postId())));

    postToEdit.addComment(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    //TODO send notification email

    return getPostResponse(editedPost);
  }

  private List<CommentResponse> mapCommentsToResponse(List<Comment> comments) {
    return comments.stream().map(
            comment -> new CommentResponse(comment.getId().id(), comment.getDescription().text(),
                comment.getAuthorId().id(), comment.getLikeCounter().likeCount()))
        .toList();
  }

  public PostResponse editComment(CommentUpdateRequest commentUpdateRequest) {
    Description description = new Description(commentUpdateRequest.description());
    description.validate();
    AuthorId authorId = new AuthorId(commentUpdateRequest.authorId());
    authorId.validate();

    Post postToEdit = postRepository.findPostById(new PostId(commentUpdateRequest.postId()))
        .orElseThrow(
            () -> new PostNotExists(
                String.format(POST_DONT_EXISTS_MESSAGE,
                    commentUpdateRequest.postId())));

    postToEdit.editComment(new CommentId(commentUpdateRequest.commentId()), description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    //TODO send notification email

    return getPostResponse(editedPost);
  }

  private PostResponse getPostResponse(Post post) {
    return new PostResponse(post.getId().id(), post.getDescription().text(),
        post.getAuthorId().id(), post.getLikeCounter().likeCount(),
        mapCommentsToResponse(post.getComments()));
  }

  public void removeComment(CommentDeleteRequest commentDeleteRequest) {
    AuthorId authorId = new AuthorId(commentDeleteRequest.authorId());
    authorId.validate();

    Post postToDelete = postRepository.findPostById(new PostId(commentDeleteRequest.postId()))
        .orElseThrow(
            () -> new PostNotExists(
                String.format(POST_DONT_EXISTS_MESSAGE,
                    commentDeleteRequest.postId())));

    postToDelete.removeComment(new CommentId(commentDeleteRequest.commentId()), authorId);

    postRepository.save(postToDelete);
  }

  public PostResponse likePost(UUID postId) {
    Post postToLike = postRepository.findPostById(new PostId(postId))
        .orElseThrow(
            () -> new PostNotExists(
                String.format(POST_DONT_EXISTS_MESSAGE,
                    postId)));

    postToLike.increaseLikes();

    Post likedPost = postRepository.save(postToLike);

    //TODO send notification email

    return getPostResponse(likedPost);
  }

  public PostResponse unlikePost(UUID postId) {
    Post postToUnlike = postRepository.findPostById(new PostId(postId))
        .orElseThrow(
            () -> new PostNotExists(
                String.format(POST_DONT_EXISTS_MESSAGE,
                    postId)));

    postToUnlike.decreaseLikes();

    Post unlikedPost = postRepository.save(postToUnlike);

    //TODO send notification email

    return getPostResponse(unlikedPost);
  }

  public PostResponse likeComment(UUID postId, UUID commentId) {
    Post postToLike = postRepository.findPostById(new PostId(postId))
        .orElseThrow(
            () -> new PostNotExists(
                String.format(POST_DONT_EXISTS_MESSAGE,
                    postId)));

    postToLike.increaseLikesInComment(new CommentId(commentId));

    Post likedPost = postRepository.save(postToLike);

    //TODO send notification email

    return getPostResponse(likedPost);

  }

  public PostResponse unlikeComment(UUID postId, UUID commentId) {
    Post postToUnlike = postRepository.findPostById(new PostId(postId))
        .orElseThrow(
            () -> new PostNotExists(
                String.format(POST_DONT_EXISTS_MESSAGE,
                    postId)));

    postToUnlike.decreaseLikesInComment(new CommentId(commentId));

    Post unlikedPost = postRepository.save(postToUnlike);

    //TODO send notification email

    return getPostResponse(unlikedPost);
  }
}
