package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record FriendRequestRejectionRequest(UUID rejectorId, UUID requesterId) {

}
