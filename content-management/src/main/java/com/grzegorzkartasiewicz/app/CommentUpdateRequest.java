package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record CommentUpdateRequest(UUID postId, UUID commentId, String description, UUID authorId) {

}
