package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record FriendRemovalRequest(UUID initiatorId, UUID friendId) {

}
