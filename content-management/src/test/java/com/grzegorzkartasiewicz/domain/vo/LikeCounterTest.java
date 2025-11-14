package com.grzegorzkartasiewicz.domain.vo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LikeCounterTest {

  @Test
  void increaseShouldIncreaseCountByOne() {
    LikeCounter likeCounter = new LikeCounter(0);

    LikeCounter increasedLikeCounter = likeCounter.increase();

    assertThat(increasedLikeCounter.likeCount()).isEqualTo(likeCounter.likeCount() + 1);
  }

  @Test
  void decreaseShouldDecreaseCountByOne() {
    LikeCounter likeCounter = new LikeCounter(1);

    LikeCounter increasedLikeCounter = likeCounter.decrease();

    assertThat(increasedLikeCounter.likeCount()).isEqualTo(likeCounter.likeCount() - 1);
  }
}