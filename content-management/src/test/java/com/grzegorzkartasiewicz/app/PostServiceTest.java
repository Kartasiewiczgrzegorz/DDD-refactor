package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.Comment;
import com.grzegorzkartasiewicz.domain.CommentNotExists;
import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.PostRepository;
import com.grzegorzkartasiewicz.domain.UnauthorizedToEditException;
import com.grzegorzkartasiewicz.domain.ValidationException;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  private static final UUID POST_AUTHOR_ID = UUID.randomUUID();
  private static final String POST_VALID_DESCRIPTION = "Valid post description";
  private static final UUID COMMENT_AUTHOR_ID = UUID.randomUUID();
  private static final String COMMENT_VALID_DESCRIPTION = "Valid comment description";
  private static final String REQUEST_VALID_DESCRIPTION = "Valid description";
  public static final String UPDATE_VALID_DESCRIPTION = "New description";
  @Mock
  private PostRepository postRepository;
  @InjectMocks
  private PostService postService;

  private Post testPost;

  private Comment testComment;

  @BeforeEach
  void setUp() {
    Description postDescription = new Description(POST_VALID_DESCRIPTION);
    AuthorId postAuthorId = new AuthorId(POST_AUTHOR_ID);
    Description commentDescription = new Description(COMMENT_VALID_DESCRIPTION);
    AuthorId commentAuthorId = new AuthorId(COMMENT_AUTHOR_ID);
    testComment = new Comment(new CommentId(UUID.randomUUID()), commentDescription, commentAuthorId,
        new LikeCounter(10));
    testPost = new Post(new PostId(UUID.randomUUID()), postDescription,
        postAuthorId, new LikeCounter(10), new ArrayList<>(List.of(testComment)));
  }

  @Test
  @DisplayName("add post should create and save post when given valid description and author")
  void addPost_shouldCreateAndSavePostWhenGivenValidDescriptionAndAuthor() {
    PostCreationRequest postCreationRequest = new PostCreationRequest(REQUEST_VALID_DESCRIPTION,
        POST_AUTHOR_ID);

    when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

    PostResponse postResponse = postService.addPost(postCreationRequest);

    assertThat(postResponse.authorId()).isEqualTo(postCreationRequest.authorId());
    assertThat(postResponse.description()).isEqualTo(postCreationRequest.description());
    verify(postRepository).save(any(Post.class));
  }

  @Test
  @DisplayName("add post should throw validation exception when description is invalid")
  void addPost_shouldThrowValidationExceptionWhenDescriptionIsInvalid() {
    PostCreationRequest postCreationRequest = new PostCreationRequest(" ",
        POST_AUTHOR_ID);

    assertThrows(ValidationException.class, () -> postService.addPost(postCreationRequest));
  }

  @Test
  @DisplayName("add post should throw validation exception when description is null")
  void addPost_shouldThrowValidationExceptionWhenDescriptionIsNull() {
    PostCreationRequest postCreationRequest = new PostCreationRequest(null,
        POST_AUTHOR_ID);

    assertThrows(ValidationException.class, () -> postService.addPost(postCreationRequest));
  }

  @Test
  @DisplayName("add post should throw validation exception when author id is null")
  void addPost_shouldThrowValidationExceptionWhenAuthorIdIsNull() {
    PostCreationRequest postCreationRequest = new PostCreationRequest(REQUEST_VALID_DESCRIPTION,
        null);

    assertThrows(ValidationException.class, () -> postService.addPost(postCreationRequest));
  }

  @Test
  @DisplayName("edit post should edit description when post exists and user is author")
  void editPost_shouldEditDescriptionWhenPostExistsAndUserIsAuthor() {
    PostUpdateRequest postUpdateRequest = new PostUpdateRequest(testPost.getId().id(),
        UPDATE_VALID_DESCRIPTION, POST_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

    PostResponse postResponse = postService.updatePost(postUpdateRequest);

    verify(postRepository).save(any(Post.class));
    assertThat(postResponse.authorId()).isEqualTo(postUpdateRequest.authorId());
    assertThat(postResponse.description()).isEqualTo(postUpdateRequest.description());
  }

  @Test
  @DisplayName("edit post should throw authorization exception when user is not author")
  void editPost_shouldThrowAuthorizationExceptionWhenUserIsNotAuthor() {
    PostUpdateRequest postUpdateRequest = new PostUpdateRequest(testPost.getId().id(),
        UPDATE_VALID_DESCRIPTION, UUID.randomUUID());

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));

    assertThrows(UnauthorizedToEditException.class,
        () -> postService.updatePost(postUpdateRequest));
  }

  @Test
  @DisplayName("edit post should throw not found exception when post does not exist")
  void editPost_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
    PostUpdateRequest postUpdateRequest = new PostUpdateRequest(testPost.getId().id(),
        UPDATE_VALID_DESCRIPTION, POST_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());

    assertThrows(PostNotExists.class,
        () -> postService.updatePost(postUpdateRequest));
  }

  @Test
  @DisplayName("edit post should throw validation exception when new description is invalid")
  void editPost_shouldThrowValidationExceptionWhenNewDescriptionIsInvalid() {
    PostUpdateRequest postUpdateRequest = new PostUpdateRequest(testPost.getId().id(),
        "   ", POST_AUTHOR_ID);

    assertThrows(ValidationException.class,
        () -> postService.updatePost(postUpdateRequest));
  }

  @Test
  @DisplayName("edit post should throw validation exception when author id is invalid")
  void editPost_shouldThrowValidationExceptionWhenAuthorIdIsInvalid() {
    PostUpdateRequest postUpdateRequest = new PostUpdateRequest(testPost.getId().id(),
        UPDATE_VALID_DESCRIPTION, null);

    assertThrows(ValidationException.class,
        () -> postService.updatePost(postUpdateRequest));
  }

  @Test
  @DisplayName("delete post should delete post when post exists and user is author")
  void deletePost_shouldDeletePostWhenPostExistsAndUserIsAuthor() {
    PostDeleteRequest postDeleteRequest = new PostDeleteRequest(testPost.getId().id(),
        POST_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));

    postService.deletePost(postDeleteRequest);

    verify(postRepository).delete(any(Post.class));
  }

  @Test
  @DisplayName("delete post should throw authorization exception when user is not author")
  void deletePost_shouldThrowAuthorizationExceptionWhenUserIsNotAuthor() {
    PostDeleteRequest postDeleteRequest = new PostDeleteRequest(testPost.getId().id(),
        UUID.randomUUID());

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));

    assertThrows(UnauthorizedToEditException.class,
        () -> postService.deletePost(postDeleteRequest));
  }

  @Test
  @DisplayName("delete post should throw not found exception when post does not exist")
  void deletePost_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
    PostDeleteRequest postDeleteRequest = new PostDeleteRequest(testPost.getId().id(),
        POST_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());

    assertThrows(PostNotExists.class,
        () -> postService.deletePost(postDeleteRequest));
  }

  @Test
  @DisplayName("delete post should throw validation exception when author id is invalid")
  void deletePost_shouldThrowValidationExceptionWhenAuthorIdIsInvalid() {
    PostDeleteRequest postDeleteRequest = new PostDeleteRequest(testPost.getId().id(), null);

    assertThrows(ValidationException.class,
        () -> postService.deletePost(postDeleteRequest));
  }


  @Test
  @DisplayName("add comment should add comment to post when post exists and data is valid")
  void addComment_shouldAddCommentToPostWhenPostExistsAndDataIsValid() {
    CommentCreationRequest commentCreationRequest = new CommentCreationRequest(
        testPost.getId().id(), REQUEST_VALID_DESCRIPTION, COMMENT_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    when(postRepository.save(any(Post.class))).thenReturn(testPost);

    PostResponse postResponse = postService.addComment(commentCreationRequest);

    CommentResponse commentResponse = postResponse.comments().stream()
        .filter(comment -> comment.description().equals(REQUEST_VALID_DESCRIPTION)).findFirst()
        .orElseThrow();
    assertThat(commentResponse.authorId()).isEqualTo(
        commentCreationRequest.authorId());
    verify(postRepository).save(any(Post.class));
  }

  @Test
  @DisplayName("add comment should throw not found exception when post does not exist")
  void addComment_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
    CommentCreationRequest commentCreationRequest = new CommentCreationRequest(
        testPost.getId().id(), REQUEST_VALID_DESCRIPTION, COMMENT_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());

    assertThrows(PostNotExists.class,
        () -> postService.addComment(commentCreationRequest));
  }

  @Test
  @DisplayName("add comment should throw validation exception when comment description is invalid")
  void addComment_shouldThrowValidationExceptionWhenCommentDescriptionIsInvalid() {
    CommentCreationRequest commentCreationRequest = new CommentCreationRequest(
        testPost.getId().id(), "  ", COMMENT_AUTHOR_ID);

    assertThrows(ValidationException.class,
        () -> postService.addComment(commentCreationRequest));
  }

  @Test
  @DisplayName("add comment should throw validation exception when comment author id is invalid")
  void addComment_shouldThrowValidationExceptionWhenCommentAuthorIdIsInvalid() {
    CommentCreationRequest commentCreationRequest = new CommentCreationRequest(
        testPost.getId().id(), REQUEST_VALID_DESCRIPTION, null);

    assertThrows(ValidationException.class,
        () -> postService.addComment(commentCreationRequest));
  }

  @Test
  @DisplayName("edit comment should edit comment when post exists and user is comment author")
  void editComment_shouldEditCommentWhenPostExistsAndUserIsCommentAuthor() {
    CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(
        testPost.getId().id(), testComment.getId().id(), REQUEST_VALID_DESCRIPTION,
        COMMENT_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

    PostResponse postResponse = postService.editComment(commentUpdateRequest);

    CommentResponse commentResponse = postResponse.comments().get(0);
    assertThat(commentResponse.id()).isEqualTo(testComment.getId().id());
    assertThat(commentResponse.authorId()).isEqualTo(
        commentUpdateRequest.authorId());
    assertThat(commentResponse.description()).isEqualTo(
        commentUpdateRequest.description());
    verify(postRepository).save(any(Post.class));
  }

  @Test
  @DisplayName("edit comment should throw authorization exception when user is not comment author")
  void editComment_shouldThrowAuthorizationExceptionWhenUserIsNotCommentAuthor() {
    CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(
        testPost.getId().id(), testComment.getId().id(), REQUEST_VALID_DESCRIPTION,
        UUID.randomUUID());

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));

    assertThrows(UnauthorizedToEditException.class,
        () -> postService.editComment(commentUpdateRequest));
  }

  @Test
  @DisplayName("edit comment should throw not found exception when post does not exist")
  void editComment_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
    CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(
        testPost.getId().id(), testComment.getId().id(), REQUEST_VALID_DESCRIPTION,
        COMMENT_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());

    assertThrows(PostNotExists.class,
        () -> postService.editComment(commentUpdateRequest));
  }

  @Test
  @DisplayName("edit comment should throw not found exception when comment does not exist")
  void editComment_shouldThrowNotFoundExceptionWhenCommentDoesNotExist() {
    CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(
        testPost.getId().id(), testComment.getId().id(), "  ",
        COMMENT_AUTHOR_ID);

    assertThrows(ValidationException.class,
        () -> postService.editComment(commentUpdateRequest));
  }

  @Test
  @DisplayName("edit comment should throw validation exception when new description is invalid")
  void editComment_shouldThrowValidationExceptionWhenNewDescriptionIsInvalid() {
    CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(
        testPost.getId().id(), testComment.getId().id(), REQUEST_VALID_DESCRIPTION,
        null);

    assertThrows(ValidationException.class,
        () -> postService.editComment(commentUpdateRequest));
  }

  @Test
  @DisplayName("remove comment should remove comment when post exists and user is comment author")
  void removeComment_shouldRemoveCommentWhenPostExistsAndUserIsCommentAuthor() {
    CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest(testPost.getId().id(),
        testComment.getId().id(),
        COMMENT_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));

    postService.removeComment(commentDeleteRequest);

    verify(postRepository).save(any(Post.class));
  }

  @Test
  @DisplayName("remove comment should throw authorization exception when user is not comment author")
  void removeComment_shouldThrowAuthorizationExceptionWhenUserIsNotCommentAuthor() {
    CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest(testPost.getId().id(),
        testComment.getId().id(),
        UUID.randomUUID());

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));

    assertThrows(UnauthorizedToEditException.class,
        () -> postService.removeComment(commentDeleteRequest));
  }

  @Test
  @DisplayName("remove comment should throw not found exception when post does not exist")
  void removeComment_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
    CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest(testPost.getId().id(),
        testComment.getId().id(),
        COMMENT_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());

    assertThrows(PostNotExists.class,
        () -> postService.removeComment(commentDeleteRequest));
  }

  @Test
  @DisplayName("remove comment should throw not found exception when comment does not exist")
  void removeComment_shouldThrowNotFoundExceptionWhenCommentDoesNotExist() {
    CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest(testPost.getId().id(),
        UUID.randomUUID(),
        COMMENT_AUTHOR_ID);

    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));

    assertThrows(CommentNotExists.class,
        () -> postService.removeComment(commentDeleteRequest));
  }

  @Test
  @DisplayName("like post should increase like count and save post when post exists")
  void likePost_shouldIncreaseLikeCountAndSavePostWhenPostExists() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

    PostResponse postResponse = postService.likePost(testPost.getId().id());

    assertThat(testPost.getLikeCounter().likeCount()).isEqualTo(postResponse.likeCount());
  }

  @Test
  @DisplayName("like post should throw not found exception when post does not exist")
  void likePost_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());
    UUID postId = testPost.getId().id();

    assertThrows(PostNotExists.class,
        () -> postService.likePost(postId));
  }

  @Test
  @DisplayName("unlike post should decrease like count and save post when post exists and has likes")
  void unlikePost_shouldDecreaseLikeCountAndSavePostWhenPostExistsAndHasLikes() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

    PostResponse postResponse = postService.unlikePost(testPost.getId().id());

    assertThat(testPost.getLikeCounter().likeCount()).isEqualTo(postResponse.likeCount());
  }

  @Test
  @DisplayName("unlike post should check invariant and not decrease below zero")
  void unlikePost_shouldCheckInvariantAndNotDecreaseBelowZero() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());
    UUID postId = testPost.getId().id();

    assertThrows(PostNotExists.class,
        () -> postService.unlikePost(postId));
  }

  @Test
  @DisplayName("like comment should increase comment like count and save post")
  void likeComment_shouldIncreaseCommentLikeCountAndSavePost() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

    PostResponse postResponse = postService.likeComment(testPost.getId().id(),
        testComment.getId().id());

    assertThat(testComment.getLikeCounter().likeCount()).isEqualTo(
        postResponse.comments().get(0).likeCount());
  }

  @Test
  @DisplayName("like comment should throw exception when post does not exist")
  void likeComment_shouldThrowExceptionWhenPostDoesNotExist() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());
    UUID postId = testPost.getId().id();
    UUID commentId = testComment.getId().id();

    assertThrows(PostNotExists.class,
        () -> postService.likeComment(postId, commentId));
  }

  @Test
  @DisplayName("like comment should throw exception when comment does not exist")
  void likeComment_shouldThrowExceptionWhenCommentDoesNotExist() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    UUID postId = testPost.getId().id();
    UUID commentId = UUID.randomUUID();

    assertThrows(CommentNotExists.class,
        () -> postService.likeComment(postId, commentId));
  }

  @Test
  @DisplayName("unlike comment should decrease comment like count")
  void unlikeComment_shouldDecreaseCommentLikeCount() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

    PostResponse postResponse = postService.unlikeComment(testPost.getId().id(),
        testComment.getId().id());

    assertThat(testComment.getLikeCounter().likeCount()).isEqualTo(
        postResponse.comments().get(0).likeCount());
  }

  @Test
  @DisplayName("unlike comment should throw exception when comment does not exist")
  void unlikeComment_shouldThrowExceptionWhenCommentDoesNotExist() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.empty());
    UUID postId = testPost.getId().id();
    UUID commentId = testComment.getId().id();

    assertThrows(PostNotExists.class,
        () -> postService.unlikeComment(postId, commentId));
  }

  @Test
  @DisplayName("unlike comment should check invariant and not decrease below zero")
  void unlikeComment_shouldCheckInvariantAndNotDecreaseBelowZero() {
    when(postRepository.findPostById(testPost.getId())).thenReturn(Optional.ofNullable(testPost));
    UUID postId = testPost.getId().id();
    UUID commentId = UUID.randomUUID();

    assertThrows(CommentNotExists.class,
        () -> postService.unlikeComment(postId, commentId));
  }
}