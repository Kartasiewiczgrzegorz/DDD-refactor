package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class PostTest {

  @Test
  void editPostShouldChangeDescription() {
    Post testPost = getTestPost();

    Description newDescription = new Description("new description");
    testPost.edit(newDescription);

    assertThat(testPost.getDescription()).isEqualTo(newDescription);
  }

  @Test
  void increaseLikesShouldIncreaseLikeCounter() {
    Post testPost = getTestPost();
    int oldLikeCount = testPost.getLikeCounter().likeCount();

    testPost.increaseLikes();

    assertThat(testPost.getLikeCounter().likeCount()).isEqualTo(oldLikeCount + 1);
  }

  @Test
  void decreaseLikesShouldDecreaseLikeCounter() {
    Post testPost = getTestPost();
    testPost.increaseLikes();
    int oldLikeCount = testPost.getLikeCounter().likeCount();

    testPost.decreaseLikes();

    assertThat(testPost.getLikeCounter().likeCount()).isEqualTo(oldLikeCount - 1);
  }

  @Test
  void decreaseLikesShouldNotDecreaseLikeCounterIfLikeCountIsZero() {
    Post testPost = getTestPost();
    int oldLikeCount = testPost.getLikeCounter().likeCount();
    assertThat(oldLikeCount).isZero();

    testPost.decreaseLikes();

    assertThat(testPost.getLikeCounter().likeCount()).isEqualTo(oldLikeCount);
  }

  @Test
  void addCommentShouldAddComment() {
    Post testPost = getTestPost();
    Comment testComment = getTestComment();

    assertThat(testPost.getComments()).isEmpty();

    testPost.addComment(testComment.getDescription(), testComment.getAuthorId());

    assertThat(testPost.getComments()).hasSize(1);
    assertThat(testPost.getComments().stream().map(Comment::getDescription)).contains(
        testComment.getDescription());
    assertThat(testPost.getComments().stream().map(Comment::getAuthorId)).contains(
        testComment.getAuthorId());
  }

  @Test
  void editCommentShouldChangeDescriptionInComment() {
    Post testPost = getTestPostWithComments();
    Comment comment = testPost.getComments().stream().findFirst().orElseThrow(AssertionError::new);

    Description newDescription = new Description("new description");
    testPost.editComment(comment.getId(), newDescription);

    assertThat(testPost.getComments()).hasSize(1);
    assertThat(testPost.getComments().stream().map(Comment::getDescription)).contains(
        newDescription);
  }

  @Test
  void removeCommentShouldRemoveComment() {
    Post testPost = getTestPostWithComments();
    Comment comment = testPost.getComments().stream().findFirst().orElseThrow(AssertionError::new);

    assertThat(testPost.getComments()).hasSize(1);

    testPost.removeComment(comment.getId());

    assertThat(testPost.getComments()).isEmpty();
  }

  @Test
  void increaseLikesInCommentShouldIncreaseLikeCounter() {
    Post testPost = getTestPostWithComments();
    Comment comment = testPost.getComments().stream().findFirst().orElseThrow(AssertionError::new);
    int oldLikeCount = comment.getLikeCounter().likeCount();

    testPost.increaseLikesInComment(comment.getId());

    assertThat(comment.getLikeCounter().likeCount()).isEqualTo(oldLikeCount + 1);
  }

  @Test
  void decreaseLikesInCommentShouldDecreaseLikeCounter() {
    Post testPost = getTestPostWithComments();
    Comment comment = testPost.getComments().stream().findFirst().orElseThrow(AssertionError::new);
    comment.increaseLikes();
    int oldLikeCount = comment.getLikeCounter().likeCount();

    testPost.decreaseLikesInComment(comment.getId());

    assertThat(comment.getLikeCounter().likeCount()).isEqualTo(oldLikeCount - 1);
  }

  @Test
  void decreaseLikesInCommentShouldNotDecreaseLikeCounterIfLikeCountIsZero() {
    Post testPost = getTestPostWithComments();
    Comment comment = testPost.getComments().stream().findFirst().orElseThrow(AssertionError::new);
    int oldLikeCount = comment.getLikeCounter().likeCount();
    assertThat(oldLikeCount).isZero();

    testPost.decreaseLikesInComment(comment.getId());

    assertThat(comment.getLikeCounter().likeCount()).isEqualTo(oldLikeCount);
  }


  private static @NotNull Post getTestPost() {
    return new Post(new Description("text"), new UserId(UUID.randomUUID()));
  }

  private static @NotNull Post getTestPostWithComments() {
    Post post = new Post(new Description("text"), new UserId(UUID.randomUUID()));
    Comment testComment = getTestComment();
    post.addComment(testComment.getDescription(), testComment.getAuthorId());
    return post;
  }

  private static @NotNull Comment getTestComment() {
    return new Comment(new Description("commentText"), new UserId(UUID.randomUUID()));
  }
}