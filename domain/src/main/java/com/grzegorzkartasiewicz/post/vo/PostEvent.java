package com.grzegorzkartasiewicz.post.vo;

import com.grzegorzkartasiewicz.DomainEvent;
import com.grzegorzkartasiewicz.user.vo.UserId;

public class PostEvent implements DomainEvent {
    private PostId postId;
    private State state;
    private PostData data;

    public PostEvent(PostId postId, State state, PostData data) {
        this.postId = postId;
        this.state = state;
        this.data = data;
    }

    public PostId getPostId() {
        return postId;
    }

    public State getState() {
        return state;
    }

    public PostData getData() {
        return data;
    }

    public static record PostData(String description, UserId userId) {
    }
}
