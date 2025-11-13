package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Comment {
  CommentId id;
  Description description;
  UserId authorId;
  LikeCounter likeCounter;

  public Comment(Description description, UserId authorId) {
    this.id = new CommentId(null);
    description.validate();
    this.description = description;
    this.authorId = authorId;
    this.likeCounter = new LikeCounter(0);
  }

  public static Comment createNew(String text, UUID authorId) {
    return new Comment(new Description(text), new UserId(
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
}
