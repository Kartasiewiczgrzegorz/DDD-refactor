package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record FriendRequestSendingRequest(UUID requesterId, UUID targetId) {

}
