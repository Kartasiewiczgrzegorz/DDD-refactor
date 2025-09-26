package com.grzegorzkartasiewicz.comment;

import com.grzegorzkartasiewicz.comment.vo.CommentCreator;
import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.vo.UserId;


class Comment {

    static Comment restore(CommentSnapshot snapshot) {
        return new Comment(
                snapshot.getId(),
                snapshot.getDescription(),
                snapshot.getPostId(),
                snapshot.getUserId()
        );
    }

    static Comment createFrom(final CommentCreator source) {
        return new Comment(
                0,
                source.description(),
                source.postId(),
                source.userId()
        );
    }

    private int id;

    private String description;

    private PostId postId;

    private UserId userId;

    private Comment(int id, String description, PostId postId, UserId userId) {
        this.id = id;
        this.description = description;
        this.postId = postId;
        this.userId = userId;
    }

    CommentSnapshot getSnapshot() {
        return new CommentSnapshot(id, description, postId, userId);
    }
}
