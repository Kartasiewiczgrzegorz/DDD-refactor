package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Post {

  PostId id;
  Description description;
  UserId authorId;
  LikeCounter likeCounter;
  List<Comment> comments;


  public Post(Description description, UserId authorId) {
    this.id = new PostId(null);
    description.validate();
    this.description = description;
    this.authorId = authorId;
    this.likeCounter = new LikeCounter(0);
    this.comments = new ArrayList<>();
  }

  public static Post createNew(String text, UUID authorId) {
    return new Post(new Description(text), new UserId(
        authorId));
  }

  public void edit(String newText) {
    Description newDescription = new Description(newText);
    newDescription.validate();
    this.description = newDescription;
  }

  public void increaseLikes() {
    // pomyśleć jak zapewnić spójność atomową licznika
    this.likeCounter = this.likeCounter.increase();
  }

  public void decreaseLikes() {
    this.likeCounter = this.likeCounter.decrease();
  }

  public void addComment(String text, UUID authorId) {
    this.comments.add(Comment.createNew(text, authorId));
  }

  public void editComment(CommentId commentId, String newText) {
    this.comments.stream().filter(comment -> comment.getId().equals(commentId)).findFirst()
        .ifPresent(comment -> {
          comment.edit(newText);
        });
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
