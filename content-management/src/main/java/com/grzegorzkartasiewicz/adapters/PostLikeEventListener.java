package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.PostLiked;
import com.grzegorzkartasiewicz.domain.PostUnliked;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
class PostLikeEventListener {

  private final PostLikeRepository postLikeRepository;
  private final SqlPostRepository sqlPostRepository;

  private final ConcurrentHashMap<UUID, AtomicInteger> likeBuffer = new ConcurrentHashMap<>();

  @Async
  @EventListener
  @Transactional
  public void handle(PostLiked event) {
    try {
        if (postLikeRepository.findByPostIdAndLikerId(event.postId().id(), event.likerId().id()).isEmpty()) {
            postLikeRepository.save(new PostLikeEntity(event.postId().id(), event.likerId().id()));
            // Add +1 to buffer instead of direct update
            likeBuffer.computeIfAbsent(event.postId().id(), k -> new AtomicInteger(0)).incrementAndGet();
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
         var like = postLikeRepository.findByPostIdAndLikerId(event.postId().id(), event.likerId().id());
         if (like.isPresent()) {
             postLikeRepository.delete(like.get());
             // Add -1 to buffer instead of direct update
             likeBuffer.computeIfAbsent(event.postId().id(), k -> new AtomicInteger(0)).decrementAndGet();
         }
     } catch (Exception e) {
         log.error("Error processing unlike for post {}", event.postId().id(), e);
     }
  }

  @Scheduled(fixedDelay = 5000)
  @Transactional
  public void flush() {
      if (likeBuffer.isEmpty()) {
          return;
      }

      log.debug("Flushing like buffer. Entries: {}", likeBuffer.size());

      Iterator<Map.Entry<UUID, AtomicInteger>> iterator = likeBuffer.entrySet().iterator();
      while (iterator.hasNext()) {
          Map.Entry<UUID, AtomicInteger> entry = iterator.next();
          UUID postId = entry.getKey();
          AtomicInteger atomicDelta = entry.getValue();
          
          int delta = atomicDelta.getAndSet(0);
          
          if (delta != 0) {
              try {
                  sqlPostRepository.updateLikeCount(postId, delta);
              } catch (Exception e) {
                  log.error("Failed to flush like count for post {}. Delta: {}", postId, delta, e);
                  atomicDelta.compareAndSet(0, delta);
              }
          }
          if (atomicDelta.get() == 0) {
              iterator.remove();
          }
      }
  }
}