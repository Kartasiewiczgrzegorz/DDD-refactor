package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.PostId;

public record CommentLiked(PostId postId, CommentId commentId, AuthorId likerId) implements
    DomainEvent {

}
