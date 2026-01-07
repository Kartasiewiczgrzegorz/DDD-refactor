package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.PostLiked;
import com.grzegorzkartasiewicz.domain.PostUnliked;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
class PostLikeEventListener {

  private final PostLikeRepository postLikeRepository;
  private final SqlPostRepository sqlPostRepository;

  @Async
  @EventListener
  @Transactional
  public void handle(PostLiked event) {
    try {
      if (postLikeRepository.findByPostIdAndLikerId(event.postId().id(), event.likerId().id())
          .isEmpty()) {
        postLikeRepository.save(new PostLikeEntity(event.postId().id(), event.likerId().id()));
        sqlPostRepository.incrementLikeCount(event.postId().id());
      } else {
        log.info("Post {} already liked by {}", event.postId().id(), event.likerId().id());
      }
    } catch (Exception e) {
      log.error("Error processing like for post {}", event.postId().id(), e);
    }
  }

  @Async
  @EventListener
  @Transactional
  public void handle(PostUnliked event) {
    try {
      var like = postLikeRepository.findByPostIdAndLikerId(event.postId().id(),
          event.likerId().id());
      if (like.isPresent()) {
        postLikeRepository.delete(like.get());
        sqlPostRepository.decrementLikeCount(event.postId().id());
      }
    } catch (Exception e) {
      log.error("Error processing unlike for post {}", event.postId().id(), e);
    }
  }
}
