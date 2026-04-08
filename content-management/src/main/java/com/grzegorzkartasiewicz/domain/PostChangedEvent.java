package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.Optional;

public record PostChangedEvent(
    PostId postId,
    Optional<CommentId> commentId,
    AuthorId actorId,
    AuthorId recipientId,
    PostAction action
) implements DomainEvent {

}
