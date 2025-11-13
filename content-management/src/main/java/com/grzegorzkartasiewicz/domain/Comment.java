package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class Comment {
  CommentId id;
  Description description;
  UserId authorId;
  LikeCounter likeCounter;

  Comment(Description description, UserId authorId) {
    this.id = new CommentId(null);
    description.validate();
    this.description = description;
    this.authorId = authorId;
    this.likeCounter = new LikeCounter(0);
  }

  static Comment createNew(Description text, UserId authorId) {
    return new Comment(text, authorId);
  }

  void edit(Description newText) {
    this.description = newText;
  }

  void increaseLikes() {
    // pomyśleć jak zapewnić spójność atomową licznika
    this.likeCounter = this.likeCounter.increase();
  }
  void decreaseLikes() {
    this.likeCounter = this.likeCounter.decrease();
  }
}
