package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Comment {
  private CommentId id;
  private Description description;
  private AuthorId authorId;
  private LikeCounter likeCounter;

  Comment(Description description, AuthorId authorId) {
    this.id = new CommentId(null);
    this.description = description;
    this.authorId = authorId;
    this.likeCounter = new LikeCounter(0);
  }

  static Comment createNew(Description text, AuthorId authorId) {
    return new Comment(text, authorId);
  }

  void edit(Description newText) {
    this.description = newText;
  }

  void increaseLikes() {
    this.likeCounter = this.likeCounter.increase();
  }
  void decreaseLikes() {
    if (this.likeCounter.likeCount() == 0) {
      return;
    }
    this.likeCounter = this.likeCounter.decrease();
  }
}
