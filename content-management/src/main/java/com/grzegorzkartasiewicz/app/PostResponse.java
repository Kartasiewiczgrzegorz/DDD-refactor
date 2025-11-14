package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record PostResponse(UUID id, String description, UUID authorId) {

}
