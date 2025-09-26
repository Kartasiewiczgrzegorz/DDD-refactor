package com.grzegorzkartasiewicz.comment.vo;

import com.grzegorzkartasiewicz.DomainEvent;
import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.vo.UserId;

public class CommentEvent implements DomainEvent {
    private CommentId commentId;
    private State state;
    private CommentData data;

    public CommentEvent(CommentId commentId, State state, CommentData data) {
        this.commentId = commentId;
        this.state = state;
        this.data = data;
    }

    public CommentId getCommentId() {
        return commentId;
    }

    public State getState() {
        return state;
    }

    public CommentData getData() {
        return data;
    }

    public static record CommentData(String description, PostId postId, UserId userId) {
    }
}
