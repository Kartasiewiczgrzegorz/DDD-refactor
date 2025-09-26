package com.grzegorzkartasiewicz.post.vo;

import com.grzegorzkartasiewicz.DomainEvent;

public class PostDeletedEvent implements DomainEvent {
    private final PostId postId;

    public PostDeletedEvent(PostId postId) {
        this.postId = postId;
    }

    public PostId getPostId() {
        return postId;
    }
}
