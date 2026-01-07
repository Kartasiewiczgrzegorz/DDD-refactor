package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.PostId;

public record PostLiked(PostId postId, AuthorId likerId) implements DomainEvent {
}
