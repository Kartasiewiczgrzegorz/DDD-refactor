package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.comment.vo.CommentCreator;
import com.grzegorzkartasiewicz.post.vo.PostDeletedEvent;
import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    @DisplayName("should prepare comment creator with correct data")
    void shouldPrepareCommentCreatorWithCorrectData() {
        // given
        var postSnapshot = new PostSnapshot(10, "Original post", new UserId(1));
        var post = Post.restore(postSnapshot);
        var commentDescription = "This is a great comment!";
        var authorId = new UserId(2);

        // when
        CommentCreator commentCreator = post.prepareNewComment(commentDescription, authorId);

        // then
        assertThat(commentCreator).isNotNull();
        assertThat(commentCreator.description()).isEqualTo(commentDescription);
        assertThat(commentCreator.postId()).isEqualTo(new PostId(10));
        assertThat(commentCreator.userId()).isEqualTo(authorId);
    }

    @Test
    @DisplayName("should register PostDeletedEvent when marked as deleted")
    void shouldRegisterPostDeletedEventWhenMarkedAsDeleted() {
        // given
        var postSnapshot = new PostSnapshot(10, "A post to be deleted", new UserId(1));
        var post = Post.restore(postSnapshot);

        // when
        post.markAsDeleted();
        var domainEvents = post.getDomainEvents();

        // then
        assertThat(domainEvents)
                .isNotNull()
                .hasSize(1);

        var event = domainEvents.get(0);
        assertThat(event).isInstanceOf(PostDeletedEvent.class);

        var postDeletedEvent = (PostDeletedEvent) event;
        assertThat(postDeletedEvent.getPostId()).isEqualTo(new PostId(10));
    }
}