package com.grzegorzkartasiewicz.comment;

import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.vo.UserId;

class CommentSnapshot {

    private int id;

    private String description;

    private PostId postId;

    private UserId userId;

    protected CommentSnapshot() {
    }

    CommentSnapshot(int id, String description, PostId postId, UserId userId) {
        this.id = id;
        this.description = description;
        this.postId = postId;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public PostId getPostId() {
        return postId;
    }

    public UserId getUserId() {
        return userId;
    }
}
