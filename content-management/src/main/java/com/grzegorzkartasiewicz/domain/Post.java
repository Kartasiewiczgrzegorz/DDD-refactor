package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Post {

  private PostId id;
  private Description description;
  private AuthorId authorId;
  private LikeCounter likeCounter;
  private List<Comment> comments;


  Post(Description description, AuthorId authorId) {
    this.id = new PostId(null);
    this.description = description;
    this.authorId = authorId;
    this.likeCounter = new LikeCounter(0);
    this.comments = new ArrayList<>();
  }

  public static Post createNew(Description text, AuthorId authorId) {
    return new Post(text, authorId);
  }

  public void edit(Description newText, AuthorId authorId) {
    validateIfAuthorIsTheSame(this.authorId, authorId);
    this.description = newText;
  }

  public void increaseLikes() {
    //TODO change to eventual consistency
    this.likeCounter = this.likeCounter.increase();
  }

  public void decreaseLikes() {
    if (this.likeCounter.likeCount() == 0) {
      return;
    }
    this.likeCounter = this.likeCounter.decrease();
  }

  public void addComment(Description text, AuthorId authorId) {
    this.comments.add(Comment.createNew(text, authorId));
  }

  public void editComment(CommentId commentId, Description newText, AuthorId authorId) {
    this.comments.stream().filter(comment -> comment.getId().equals(commentId)).findFirst()
        .ifPresentOrElse(comment -> {
          validateIfAuthorIsTheSame(comment.getAuthorId(), authorId);
          comment.edit(newText);
        }, () -> {
          throw new CommentNotExists(String.format("Comment with given ID: %s does not exist",
              commentId.id()));
        });
  }

  public void removeComment(CommentId commentId, AuthorId authorId) {
    this.comments.stream().filter(comment -> comment.getId().equals(commentId)).findFirst()
        .ifPresentOrElse(comment -> validateIfAuthorIsTheSame(comment.getAuthorId(), authorId),
            () -> {
              throw new CommentNotExists(String.format("Comment with given ID: %s does not exist",
                  commentId.id()));
            });
    this.comments.removeIf(comment -> comment.getId().equals(commentId));
  }

  public void increaseLikesInComment(CommentId commentId) {
    this.comments.stream().filter(comment -> comment.getId().equals(commentId)).findFirst()
        .ifPresentOrElse(Comment::increaseLikes,
            () -> {
          throw new CommentNotExists(String.format("Comment with given ID: %s does not exist",
              commentId.id()));
            });
  }

  public void decreaseLikesInComment(CommentId commentId) {
    this.comments.stream().filter(comment -> comment.getId().equals(commentId)).findFirst()
        .ifPresentOrElse(Comment::decreaseLikes,
            () -> {
              throw new CommentNotExists(String.format("Comment with given ID: %s does not exist",
                  commentId.id()));
            });
  }

  public void validateIfAuthorIsTheSame(AuthorId authorId, AuthorId authorIdThatWantsToEdit) {
    if (!authorId.equals(authorIdThatWantsToEdit)) {
      throw new UnauthorizedToEditException("User is not authorized to edit post or comment.");
    }
  }
}
