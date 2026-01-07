package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.PostId;

public record PostUnliked(PostId postId, AuthorId likerId) implements DomainEvent {
}
