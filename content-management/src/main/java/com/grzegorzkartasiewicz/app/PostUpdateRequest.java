package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record PostUpdateRequest(UUID id, String description, UUID authorId) {

}
