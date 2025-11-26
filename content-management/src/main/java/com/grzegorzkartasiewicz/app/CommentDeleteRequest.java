package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record CommentDeleteRequest(UUID postId, UUID commentId, UUID authorId) {

}
