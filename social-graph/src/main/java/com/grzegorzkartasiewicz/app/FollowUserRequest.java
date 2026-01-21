package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record FollowUserRequest(UUID followerId, UUID followedId) {

}
