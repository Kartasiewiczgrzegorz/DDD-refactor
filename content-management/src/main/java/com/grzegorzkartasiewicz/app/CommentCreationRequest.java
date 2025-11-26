package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record CommentCreationRequest(UUID postId, String description, UUID authorId) {

}
