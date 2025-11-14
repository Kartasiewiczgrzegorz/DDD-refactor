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

  public void edit(Description newText) {
    this.description = newText;
  }

  public void increaseLikes() {
    // pomyśleć jak zapewnić spójność atomową licznika
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

  public void editComment(CommentId commentId, Description newText) {
    this.comments.stream().filter(comment -> comment.getId().equals(commentId)).findFirst()
        .ifPresent(comment -> comment.edit(newText));
  }

  public void removeComment(CommentId commentId) {
    this.comments.removeIf(comment -> comment.getId().equals(commentId));
  }

  public void increaseLikesInComment(CommentId commentId) {
    this.comments.stream().filter(comment -> comment.getId().equals(commentId)).findFirst()
        .ifPresent(Comment::increaseLikes);
  }

  public void decreaseLikesInComment(CommentId commentId) {
    this.comments.stream().filter(comment -> comment.getId().equals(commentId)).findFirst()
        .ifPresent(Comment::decreaseLikes);
  }
}
