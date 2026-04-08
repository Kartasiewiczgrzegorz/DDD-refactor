package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.UserId;

public record FriendRequestSent(UserId requesterId, UserId targetId) implements DomainEvent {

}
