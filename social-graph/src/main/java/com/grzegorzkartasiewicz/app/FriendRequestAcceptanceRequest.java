package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record FriendRequestAcceptanceRequest(UUID approverId, UUID requesterId) {

}
