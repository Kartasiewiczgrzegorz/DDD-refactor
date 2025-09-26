package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.DomainEvent;
import com.grzegorzkartasiewicz.comment.vo.CommentCreator;
import com.grzegorzkartasiewicz.post.vo.PostCreator;
import com.grzegorzkartasiewicz.post.vo.PostDeletedEvent;
import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.vo.UserId;

import java.util.ArrayList;
import java.util.List;


class Post {

    static Post restore(PostSnapshot snapshot) {
        return new Post(
                snapshot.getId(),
                snapshot.getDescription(),
                snapshot.getUserId()
        );
    }

    static Post createFrom(final PostCreator source) {
        return new Post(
                0,
                source.description(),
                source.userId()
        );
    }

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private int id;

    private String description;

    private UserId userId;


    private Post(int id, String description, UserId userId) {
        this.id = id;
        this.description = description;
        this.userId = userId;
    }

    PostSnapshot getSnapshot() {
        return new PostSnapshot(id, description, userId);
    }

    Post edit(String description) {
        this.description = description;
        return this;
    }

    CommentCreator prepareNewComment(final String description, final UserId authorId) {
        return new CommentCreator(description, new PostId(this.id), authorId);
    }

    void markAsDeleted() {
        this.domainEvents.add(new PostDeletedEvent(new PostId(this.id)));
    }

    List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
