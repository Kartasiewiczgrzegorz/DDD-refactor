package com.grzegorzkartasiewicz.domain.vo;

public record LikeCounter(int likeCount) {

  public LikeCounter increase() {
    return new LikeCounter(likeCount + 1);
  }

  public LikeCounter decrease() {
    return new LikeCounter(likeCount - 1);
  }
}
