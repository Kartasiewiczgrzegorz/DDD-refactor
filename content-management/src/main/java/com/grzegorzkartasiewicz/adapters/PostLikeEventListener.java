package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.CommentLiked;
import com.grzegorzkartasiewicz.domain.CommentUnliked;
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

/**
 * Event Listener handling eventual consistency for likes on Posts and Comments.
 * <p>
 * This component implements the <strong>Write-Behind</strong> pattern to efficiently handle
 * high-throughput like operations. Instead of updating the `like_counter` column in the database
 * synchronously for every single event (which would cause row-level locking and contention), it
 * uses an in-memory buffer to aggregate changes.
 * </p>
 * <h3>Key Mechanisms:</h3>
 * <ul>
 *     <li><strong>Asynchronous Processing:</strong> Events are handled asynchronously via {@link Async}, returning control to the caller immediately.</li>
 *     <li><strong>Uniqueness Check:</strong> Before buffering a counter change, it verifies uniqueness by attempting to save
 *     a {@link PostLikeEntity} or {@link CommentLikeEntity}. This ensures a user cannot like the same content multiple times.</li>
 *     <li><strong>In-Memory Buffering:</strong> Valid changes are aggregated in {@link ConcurrentHashMap} buffers (one for posts, one for comments).
 *     The value is an {@link AtomicInteger} representing the net change (delta) since the last flush (e.g., +10, -2).</li>
 *     <li><strong>Scheduled Flush:</strong> A scheduled task runs periodically (e.g., every 5 seconds) to "flush" the buffers.
 *     It executes a single native SQL UPDATE per active post/comment to apply the aggregated delta.</li>
 * </ul>
 * <p>
 * This approach drastically reduces database load and lock contention during viral traffic spikes.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
class PostLikeEventListener {

  private final PostLikeRepository postLikeRepository;
  private final SqlPostRepository sqlPostRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final SqlCommentRepository sqlCommentRepository;

  private final ConcurrentHashMap<UUID, AtomicInteger> postLikeBuffer = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, AtomicInteger> commentLikeBuffer = new ConcurrentHashMap<>();

  /**
   * Handles the {@link PostLiked} event. Checks for duplicate likes and buffers an increment for
   * the post's like counter.
   *
   * @param event The event data.
   */
  @Async
  @EventListener
  @Transactional
  public void handle(PostLiked event) {
    try {
      if (postLikeRepository.findByPostIdAndLikerId(event.postId().id(), event.likerId().id())
          .isEmpty()) {
        postLikeRepository.save(new PostLikeEntity(event.postId().id(), event.likerId().id()));
        postLikeBuffer.computeIfAbsent(event.postId().id(), k -> new AtomicInteger(0))
            .incrementAndGet();
      }
    } catch (Exception e) {
      log.error("Error processing like for post {}", event.postId().id(), e);
    }
  }

  /**
   * Handles the {@link PostUnliked} event. Verifies the like exists, removes it, and buffers a
   * decrement for the post's like counter.
   *
   * @param event The event data.
   */
  @Async
  @EventListener
  @Transactional
  public void handle(PostUnliked event) {
    try {
      var like = postLikeRepository.findByPostIdAndLikerId(event.postId().id(),
          event.likerId().id());
      if (like.isPresent()) {
        postLikeRepository.delete(like.get());
        postLikeBuffer.computeIfAbsent(event.postId().id(), k -> new AtomicInteger(0))
            .decrementAndGet();
      }
    } catch (Exception e) {
      log.error("Error processing unlike for post {}", event.postId().id(), e);
    }
  }

  /**
   * Handles the {@link CommentLiked} event. Checks for duplicate likes and buffers an increment for
   * the comment's like counter.
   *
   * @param event The event data.
   */
  @Async
  @EventListener
  @Transactional
  public void handle(CommentLiked event) {
    try {
      if (commentLikeRepository.findByCommentIdAndLikerId(event.commentId().id(),
          event.likerId().id()).isEmpty()) {
        commentLikeRepository.save(
            new CommentLikeEntity(event.postId().id(), event.commentId().id(),
                event.likerId().id()));
        commentLikeBuffer.computeIfAbsent(event.commentId().id(), k -> new AtomicInteger(0))
            .incrementAndGet();
      }
    } catch (Exception e) {
      log.error("Error processing like for comment {}", event.commentId().id(), e);
    }
  }

  /**
   * Handles the {@link CommentUnliked} event. Verifies the like exists, removes it, and buffers a
   * decrement for the comment's like counter.
   *
   * @param event The event data.
   */
  @Async
  @EventListener
  @Transactional
  public void handle(CommentUnliked event) {
    try {
      var like = commentLikeRepository.findByCommentIdAndLikerId(event.commentId().id(),
          event.likerId().id());
      if (like.isPresent()) {
        commentLikeRepository.delete(like.get());
        commentLikeBuffer.computeIfAbsent(event.commentId().id(), k -> new AtomicInteger(0))
            .decrementAndGet();
      }
    } catch (Exception e) {
      log.error("Error processing unlike for comment {}", event.commentId().id(), e);
    }
  }

  /**
   * Scheduled task to flush aggregated like counts to the database. Runs periodically (configured
   * via fixedDelay) to minimize database write operations.
   */
  @Scheduled(fixedDelay = 5000)
  @Transactional
  public void flush() {
    flushBuffer(postLikeBuffer, sqlPostRepository::updateLikeCount, "post");
    flushBuffer(commentLikeBuffer, sqlCommentRepository::updateLikeCount, "comment");
  }

  private void flushBuffer(ConcurrentHashMap<UUID, AtomicInteger> buffer, UpdateAction updater,
      String type) {
    if (buffer.isEmpty()) {
      return;
    }
    log.debug("Flushing {} like buffer. Entries: {}", type, buffer.size());

    Iterator<Map.Entry<UUID, AtomicInteger>> iterator = buffer.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<UUID, AtomicInteger> entry = iterator.next();
      UUID id = entry.getKey();
      AtomicInteger atomicDelta = entry.getValue();

      int delta = atomicDelta.getAndSet(0);

      if (delta != 0) {
        try {
          updater.update(id, delta);
        } catch (Exception e) {
          log.error("Failed to flush like count for {} {}. Delta: {}", type, id, delta, e);
          atomicDelta.addAndGet(delta);
        }
      }
      if (atomicDelta.get() == 0) {
        iterator.remove();
      }
    }
  }

  @FunctionalInterface
  interface UpdateAction {

    void update(UUID id, int delta);
  }
}
