package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record UnfollowUserRequest(UUID followerId, UUID followedId) {

}
