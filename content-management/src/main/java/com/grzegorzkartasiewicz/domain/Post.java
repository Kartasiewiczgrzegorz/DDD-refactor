package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Aggregate Root representing a Post in the system. Manages the lifecycle of the post, including
 * comments and like counters.
 */
@AllArgsConstructor
public class Post {

  @Getter
  private PostId id;
  @Getter
  private Description description;
  @Getter
  private AuthorId authorId;
  @Getter
  private LikeCounter likeCounter;

  private List<Comment> comments;


  Post(Description description, AuthorId authorId) {
    this.id = new PostId(null);
    this.description = description;
    this.authorId = authorId;
    this.likeCounter = new LikeCounter(0);
    this.comments = new ArrayList<>();
  }

  /**
   * Factory method to create a new Post.
   *
   * @param text     The description of the post.
   * @param authorId The ID of the author creating the post.
   * @return A new Post instance.
   */
  public static Post createNew(Description text, AuthorId authorId) {
    return new Post(text, authorId);
  }

  public List<Comment> getComments() {
    return Collections.unmodifiableList(comments);
  }

  /**
   * Edits the description of the post.
   *
   * @param newText The new description text.
   * @param authorId The ID of the author initiating the edit (must match the post author).
   * @throws UnauthorizedToEditException if the authorId does not match the post author.
   */
  public void edit(Description newText, AuthorId authorId) {
    validateAuthor(this.authorId, authorId);
    this.description = newText;
  }

  /**
   * Optimistically increases the like counter.
   * The actual persistence is handled via eventual consistency.
   */
  public void increaseLikes() {
    //TODO change to eventual consistency
    this.likeCounter = this.likeCounter.increase();
  }

  /**
   * Optimistically decreases the like counter.
   * The actual persistence is handled via eventual consistency.
   */
  public void decreaseLikes() {
    if (this.likeCounter.likeCount() > 0) {
      this.likeCounter = this.likeCounter.decrease();
    }
  }

  public void addComment(Description text, AuthorId authorId) {
    this.comments.add(Comment.createNew(text, authorId));
  }

  public void editComment(CommentId commentId, Description newText, AuthorId authorId) {
    Comment comment = findCommentOrThrow(commentId);
    validateAuthor(comment.getAuthorId(), authorId);
    comment.edit(newText);
  }

  public void removeComment(CommentId commentId, AuthorId authorId) {
    Comment comment = findCommentOrThrow(commentId);
    validateAuthor(comment.getAuthorId(), authorId);
    this.comments.remove(comment);
  }

  public void increaseLikesInComment(CommentId commentId) {
    findCommentOrThrow(commentId).increaseLikes();
  }

  public void decreaseLikesInComment(CommentId commentId) {
    findCommentOrThrow(commentId).decreaseLikes();
  }

  public void validatePostAuthor(AuthorId authorIdThatWantsToEdit) {
    validateAuthor(this.authorId, authorIdThatWantsToEdit);
  }

  private void validateAuthor(AuthorId ownerId, AuthorId editorId) {
    if (!ownerId.equals(editorId)) {
      throw new UnauthorizedToEditException("User is not authorized to edit post or comment.");
    }
  }

  private Comment findCommentOrThrow(CommentId commentId) {
    return this.comments.stream()
        .filter(comment -> comment.getId().equals(commentId))
        .findFirst()
        .orElseThrow(() -> new CommentNotExists(
            String.format("Comment with given ID: %s does not exist", commentId.id())
        ));
  }
}
