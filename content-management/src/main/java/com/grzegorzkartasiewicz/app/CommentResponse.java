package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record CommentResponse(UUID id, String description, UUID authorId) {

}
